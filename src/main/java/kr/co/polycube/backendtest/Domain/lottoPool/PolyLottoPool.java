package kr.co.polycube.backendtest.Domain.lottoPool;

import jakarta.persistence.*;
import kr.co.polycube.backendtest.Domain.lotto.PolyLotto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class PolyLottoPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<Integer> winningNumbers = new ArrayList<>();
    private int bonusNumber;
    private int round;
    private int totalPrize;
    private int totalLottoCount;

    @OneToMany(mappedBy = "polyLottoPool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<PolyLotto> polyLottos = new ArrayList<>();

    public PolyLottoPool(int round) {
        this.winningNumbers = new ArrayList<>();
        this.bonusNumber = 0;
        this.round = round;
        this.totalPrize = 0;
        this.totalLottoCount = 0;
    }

    public PolyLottoPool(List<Integer> winningNumbers, int bonusNumber, int round, int totalPrize, int totalLottoCount) {
        this.winningNumbers = winningNumbers;
        this.bonusNumber = bonusNumber;
        this.round = round;
        this.totalPrize = totalPrize;
        this.totalLottoCount = totalLottoCount;
    }

    public PolyLottoPool(List<Integer> winningNumbers, int bonusNumber) {
        this.winningNumbers = winningNumbers;
        this.bonusNumber = bonusNumber;
        this.round = 0;
        this.totalPrize = 0;
        this.totalLottoCount = 0;
    }

    public PolyLottoPool() {
    }

    public void addPolyLotto(PolyLotto polyLotto) {
        this.totalPrize += 1000;
        this.totalLottoCount += 1;
        polyLottos.add(polyLotto);

    }

    public void generateWinningNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 45; i++) {
            numbers.add(i);
        }
        for (int i = 0; i < 7; i++) {
            if (i == 6) {
                this.bonusNumber = numbers.get((int) (Math.random() * numbers.size()));
            } else {
                int index = (int) (Math.random() * numbers.size());
                this.winningNumbers.add(numbers.get(index));
                numbers.remove(index);
            }
        }
    }

}