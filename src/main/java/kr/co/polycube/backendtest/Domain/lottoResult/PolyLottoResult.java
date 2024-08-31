package kr.co.polycube.backendtest.Domain.lottoResult;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class PolyLottoResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int round;
    private List<Integer> winningNumbers;
    private int bonusNumber;
    // 전체의 50% 중 44%는 복권 기금으로 사용됨, 판매수수료 5.5%, 위탁 1.5%를 제외한 나머지 금액을 당첨금으로 사용
    // 로또 결과 합산 5등 5000원 4등 50000원 3등과 2등은 남은 금액의 12.5%씩 나눠가짐 1등은 남은 금액의 75%를 가져감

    private int carryOverAmount;
    private int totalPrize;
    private int carryOverPrize;

    private int totalLottoCount;
    private int FirstPrizeTotal;
    private int FirstPrize;

    private int SecondPrizeCount;
    private int SecondPrizeTotal;
    private int SecondPrize;

    private int ThirdPrizeCount;
    private int ThirdPrizeTotal;
    private int ThirdPrize;

    private int FourthPrizeCount;
    private int FourthPrizeTotal;
    private int FifthPrizeCount;
    private int FifthPrizeTotal;

    private int LotteryFund;
    private int SalesCommission;
    private int Commission;

}
