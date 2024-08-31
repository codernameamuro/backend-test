package kr.co.polycube.backendtest.Domain.lottoResult;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolyLottoResultService {

    private final PolyLottoResultRepository polyLottoResultRepository;

    public PolyLottoResult save(PolyLottoResult polyLottoResult) {
        return polyLottoResultRepository.save(polyLottoResult);
    }

    public PolyLottoResult findTopByOrderByRoundDesc() {
        return polyLottoResultRepository.findTopByOrderByRoundDesc();
    }
}
