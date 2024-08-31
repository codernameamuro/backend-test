package kr.co.polycube.backendtest.Domain.lottoResult;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PolyLottoResultRepository extends JpaRepository<PolyLottoResult, Long> {

    PolyLottoResult findTopByOrderByRoundDesc();
}
