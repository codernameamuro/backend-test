package kr.co.polycube.backendtest.Domain.lotto;



import org.springframework.data.jpa.repository.JpaRepository;
public interface PolyLottoRepository extends JpaRepository<PolyLotto, Long> {
    PolyLotto getPolyLottoByRound(int round);
}
