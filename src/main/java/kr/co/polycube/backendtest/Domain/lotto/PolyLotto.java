package kr.co.polycube.backendtest.Domain.lotto;

import jakarta.persistence.*;
import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPool;
import kr.co.polycube.backendtest.Domain.user.PolyUser;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class PolyLotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private List<Integer> lottoNumbers;

    @Column(nullable = false)
    private int bonusNumber;

    @Column(nullable = false)
    private int round;

    @Column(nullable = false)
    private int rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lottoPool_id", nullable = false)
    private PolyLottoPool polyLottoPool;

    public PolyLotto(List<Integer> lottoNumbers, int bonusNumber, int round, int rank, PolyLottoPool polyLottoPool) {
        this.lottoNumbers = lottoNumbers;
        this.bonusNumber = bonusNumber;
        this.round = round;
        this.rank = rank;
        this.polyLottoPool = polyLottoPool;
    }

    public PolyLotto(List<Integer> lottoNumbers, int bonusNumber) {
        this.lottoNumbers = lottoNumbers;
        this.bonusNumber = bonusNumber;
        this.round = 0;
        this.rank = 0;
    }

    public PolyLotto() {
    }

}