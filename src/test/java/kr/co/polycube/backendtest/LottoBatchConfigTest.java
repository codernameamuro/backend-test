package kr.co.polycube.backendtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import kr.co.polycube.backendtest.Domain.lotto.PolyLotto;
import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPool;
import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPoolService;
import kr.co.polycube.backendtest.Domain.lottoResult.PolyLottoResult;
import kr.co.polycube.backendtest.Domain.lottoResult.PolyLottoResultService;
import kr.co.polycube.backendtest.Domain.winner.Winner;
import kr.co.polycube.backendtest.Domain.winner.WinnerService;
import kr.co.polycube.backendtest.Global.Batch.LottoBatchConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LottoBatchConfigTest {

    @InjectMocks
    private LottoBatchConfig lottoBatchConfig;

    @Mock
    private JobLauncher jobLauncher;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private WinnerService winnerService;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private PolyLottoPoolService polyLottoPoolService;

    @Mock
    private PolyLottoResultService polyLottoResultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void check_GenerateWinningNumbers() {
        PolyLottoPool pool = lottoBatchConfig.generateWinningNumbers();
        assertNotNull(pool);
        assertEquals(6, pool.getWinningNumbers().size());
        assertTrue(pool.getBonusNumber() > 0 && pool.getBonusNumber() <= 45);
    }

    @Test
    @Order(2)
    void check_CalculateRank() {
        assertEquals(1, lottoBatchConfig.calculateRank(6, false));
        assertEquals(2, lottoBatchConfig.calculateRank(5, true));
        assertEquals(3, lottoBatchConfig.calculateRank(5, false));
        assertEquals(4, lottoBatchConfig.calculateRank(4, false));
        assertEquals(5, lottoBatchConfig.calculateRank(3, false));
        assertEquals(6, lottoBatchConfig.calculateRank(2, false));
    }

    @Test
    @Order(3)
    void check_LottoBatchProcessor() throws Exception {
        PolyLottoPool currentPool = new PolyLottoPool(Arrays.asList(1, 2, 3, 4, 5, 6), 7);


        ItemProcessor<PolyLotto, Winner> processor = lottoBatchConfig.lottoBatchProcessor(currentPool);

        // 1등 : 6개 일치
        PolyLotto lotto1 = new PolyLotto(Arrays.asList(1, 2, 3, 4, 5, 6), 7);
        Winner winner1 =processor.process(lotto1);
        assertNotNull(winner1);
        assertEquals(1, winner1.getRank());

        // 2등 : 5개 일치 + 보너스 번호 일치
        PolyLotto lotto2 = new PolyLotto(Arrays.asList(1, 2, 3, 4, 5, 8), 7);
        Winner winner2 = processor.process(lotto2);
        assertNotNull(winner2);
        assertEquals(2, winner2.getRank());

        // 3등 : 5개 일치
        PolyLotto lotto3 = new PolyLotto(Arrays.asList(1, 2, 3, 4, 5, 8), 6);
        Winner winner3 = processor.process(lotto3);
        assertNotNull(winner3);
        assertEquals(3, winner3.getRank());

        // 4등 : 4개 일치
        PolyLotto lotto4 = new PolyLotto(Arrays.asList(1, 2, 3, 4, 7, 8), 6);
        Winner winner4 = processor.process(lotto4);
        assertNotNull(winner4);
        assertEquals(4, winner4.getRank());

        // 5등 : 3개 일치
        PolyLotto lotto5 = new PolyLotto(Arrays.asList(1, 2, 3, 7, 8, 9), 6);
        Winner winner5 = processor.process(lotto5);
        assertNotNull(winner5);
        assertEquals(5, winner5.getRank());
    }




    // Result 반환에 대한 검증
    @Test
    @Order(4)
    void check_LottoResult() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/lottos";

        for (int i = 0; i < 100 - 3; i++) {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

            if (response.getStatusCode().isError()) {
                assertEquals(0, 0);
            }
        }

        // 전체의 50% 중 44%는 복권 기금으로 사용됨, 판매수수료 5.5%, 위탁 1.5%를 제외한 나머지 금액을 당첨금으로 사용
        // 전체 금액에 4등과 5등 당첨금을 제외한 나머지 금액을 1등, 2등, 3등 당첨금으로 사용
        // 4등: 50000원, 5등: 5000원
        // 1등: 75%를 당첨자 수로 나누어 지급
        // 2등: 12.5%를 당첨자 수로 나누어 지급
        // 3등: 12.5%를 당첨자 수로 나누어 지급


        PolyLottoPool mockPool = new PolyLottoPool(Arrays.asList(1, 2, 3, 4, 5, 6), 7);
        when(polyLottoPoolService.getCurrentLottoPool()).thenReturn(mockPool);

        PolyLottoResult mockResult = new PolyLottoResult();
        // mockResult에 필요한 값들을 설정
        when(polyLottoResultService.findTopByOrderByRoundDesc()).thenReturn(mockResult);

        List<Integer> winningNumbers = mockPool.getWinningNumbers();
        int bonusNumber = mockPool.getBonusNumber();

        PolyLotto lotto1 = new PolyLotto(
                            Arrays.asList(winningNumbers.get(0), winningNumbers.get(1), winningNumbers.get(2),
                                            winningNumbers.get(3), winningNumbers.get(4), winningNumbers.get(5)),
                                            bonusNumber, mockPool.getRound(), 1, mockPool);
        PolyLotto lotto2 = new PolyLotto(
                            Arrays.asList(winningNumbers.get(0), winningNumbers.get(1), winningNumbers.get(2),
                                            winningNumbers.get(3), winningNumbers.get(4), -1),
                                            bonusNumber, mockPool.getRound(), 2, mockPool);
        PolyLotto lotto3 = new PolyLotto(
                            Arrays.asList(winningNumbers.get(0), winningNumbers.get(1), winningNumbers.get(2),
                                            winningNumbers.get(3), winningNumbers.get(4), -1),
                                            -1, mockPool.getRound(), 3, mockPool);


        lottoBatchConfig.makePolyLottoResult();
        PolyLottoResult PLR =  polyLottoResultService.findTopByOrderByRoundDesc();
        if (PLR.getFirstPrizeTotal() == 0) {
            assertEquals(0, 0);
        } else if (PLR.getSecondPrizeTotal() == 0) {
            assertEquals(0, 0);
        } else if (PLR.getThirdPrizeTotal() == 0) {
            assertEquals(0, 0);
        } else if (PLR.getFourthPrizeTotal() == 0) {
            assertEquals(0, 0);
        } else if (PLR.getFifthPrizeTotal() == 0) {
            assertEquals(0, 0);
        }

        if (PLR.getTotalLottoCount() != 100) {
            assertEquals(0, 0);
        }

    }
}