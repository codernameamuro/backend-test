package kr.co.polycube.backendtest.Domain.lotto;

import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPool;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PolyLottoController {
    private final PolyLottoService polyLottoService;

    /***
     * Description :This method is updating lotto pool
     * Writer : Jeong eun seong
     * Update Date : 2024-08-31
     * Method : POST
     * Path : /lottos
     * Request : None
     * Response : Map<String, String>
     *      SUCCESS
     *          Response : key = result, value = success
     *          Response : key = round, value = {round} (int)
     *          Response : key = numbers, value = {numbers} (List<Integer>)
     *          Response : key = bonusNumber, value = {bonusNumber} (int)
     *      FAIL
     *          Response : key = result, value = fail / key = reason, value = exception
     *          Response : key = reason, value = exception / key = reason, value = {exception message}
     **/
    @PostMapping("/lottos")
    public ResponseEntity<Map<String, String>> updateLottoPool() {
        Map<String, String> response = new HashMap<>();
        List<Integer> LottoNumbers = polyLottoService.MakeRandomLottoNumbers();
        int bonusNumber = polyLottoService.makeRandomBonusNumber(LottoNumbers);
        int round = polyLottoService.getRound();
        int rank = 0;
        PolyLottoPool polyLottoPool = polyLottoService.getPolyLottoPoolByRound(round);
        PolyLotto polyLotto = new PolyLotto(LottoNumbers, bonusNumber, round, rank, polyLottoPool);

        try {
            polyLottoService.save(polyLotto);
            polyLottoService.updateLottoPool(round, polyLotto);
            response.put("result", "success");
            response.put("round", String.valueOf(round));
            response.put("numbers", LottoNumbers.toString());
            response.put("bonusNumber", String.valueOf(bonusNumber));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "fail");
            response.put("reason", "exception");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
