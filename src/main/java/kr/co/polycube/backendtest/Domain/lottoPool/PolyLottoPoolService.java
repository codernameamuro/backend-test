package kr.co.polycube.backendtest.Domain.lottoPool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolyLottoPoolService {

    private final PolyLottoPoolRepository PolylottoPoolRepository;

    public void save(PolyLottoPool PolylottoPool) {
        PolylottoPoolRepository.save(PolylottoPool);
    }

    public PolyLottoPool getCurrentLottoPool() {
        return PolylottoPoolRepository.findFirstByOrderByIdDesc();
    }
}
