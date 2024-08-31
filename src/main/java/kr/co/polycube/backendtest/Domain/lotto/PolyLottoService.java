package kr.co.polycube.backendtest.Domain.lotto;


import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPool;
import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PolyLottoService {
    private final PolyLottoRepository polyLottoRepository;
    private final PolyLottoPoolRepository polyLottoPoolRepository;

    public PolyLotto save(PolyLotto polyLotto) {
        return polyLottoRepository.save(polyLotto);
    }

    public Boolean updateLottoPool(int round, PolyLotto polyLotto) {
        PolyLottoPool polyLottoPool = polyLottoPoolRepository.getPolyLottoPoolByRound(round);
        if (polyLottoPool == null) {
            return false;
        }
        polyLottoPool.addPolyLotto(polyLotto);
        polyLottoPoolRepository.save(polyLottoPool);
        return true;
    }

    public List<Integer> MakeRandomLottoNumbers() {
        int[] lottoNumbers = new int[6];
        for (int i = 0; i < 6; i++) {
            lottoNumbers[i] = (int) (Math.random() * 45) + 1;
            for (int j = 0; j < i; j++) {
                if (lottoNumbers[i] == lottoNumbers[j]) {
                    i--;
                    break;
                }
            }
        }
        return List.of(lottoNumbers[0], lottoNumbers[1], lottoNumbers[2], lottoNumbers[3], lottoNumbers[4], lottoNumbers[5]);
    }

    public int makeRandomBonusNumber(List<Integer> lottoNumbers) {
        int bonusNumber = (int) (Math.random() * 45) + 1;
        for (int i = 0; i < 6; i++) {
            if (bonusNumber == lottoNumbers.get(i)) {
                bonusNumber = (int) (Math.random() * 45) + 1;
                i = -1;
            }
        }
        return bonusNumber;
    }

    public int getRound() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSaturday7pm = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))
                .withHour(19)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        if (now.isBefore(lastSaturday7pm)) {
            lastSaturday7pm = lastSaturday7pm.minusWeeks(1);
        }

        int round = now.get(WeekFields.of(Locale.getDefault()).weekOfYear());

        int year = lastSaturday7pm.getYear();
        String roundStr = String.format("%d%02d", year, round);
        return Integer.parseInt(roundStr);
    }

    public PolyLottoPool getPolyLottoPoolByRound(int round) {
        if (polyLottoPoolRepository.getPolyLottoPoolByRound(round) == null) {
            PolyLottoPool polyLottoPool = new PolyLottoPool(round);
            polyLottoPoolRepository.save(polyLottoPool);
            return polyLottoPool;
        }
        return polyLottoPoolRepository.getPolyLottoPoolByRound(round);
    }
}



