package kr.co.polycube.backendtest.Domain.lottoPool;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PolyLottoPoolRepository extends JpaRepository<PolyLottoPool, Long> {

    PolyLottoPool getPolyLottoPoolByRound(int round);

    PolyLottoPool findFirstByOrderByIdDesc();

}
