package kr.co.polycube.backendtest.Domain.winner;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rank;
    private Long LottoId;
    private String winningNumbers;
    private String selectedNumbers;
    private String BonusNumber;
    private int round;

    public Winner(int rank, Long lottoId, String winningNumbers, String selectedNumbers, String BonusNumber, int round) {
        this.rank = rank;
        this.LottoId = lottoId;
        this.winningNumbers = winningNumbers;
        this.selectedNumbers = selectedNumbers;
        this.BonusNumber = BonusNumber;
        this.round = round;
    }

    public Winner(){
    }
}
