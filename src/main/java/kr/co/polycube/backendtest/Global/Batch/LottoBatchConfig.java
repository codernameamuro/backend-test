package kr.co.polycube.backendtest.Global.Batch;

import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPoolService;
import kr.co.polycube.backendtest.Domain.lottoResult.PolyLottoResult;
import kr.co.polycube.backendtest.Domain.lottoResult.PolyLottoResultService;
import kr.co.polycube.backendtest.Domain.winner.WinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import kr.co.polycube.backendtest.Domain.lotto.PolyLotto;
import kr.co.polycube.backendtest.Domain.lottoPool.PolyLottoPool;
import kr.co.polycube.backendtest.Domain.winner.Winner;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class LottoBatchConfig {

    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final PolyLottoPoolService polyLottoPoolService;
    private final WinnerService winnerService;
    private final PolyLottoResultService polyLottoResultService;

    @Bean
    public Job lottoBatchJob(@Qualifier("lottoBatchStep") Step lottoBatchStep) {
        return new JobBuilder("lottoBatchJob", jobRepository)
                .start(lottoBatchStep)
                .build();
    }

    @Bean
    public Step lottoBatchStep(ItemReader<PolyLotto> reader,
                               ItemProcessor<PolyLotto, Winner> processor,
                               ItemWriter<Winner> writer) {
        return new StepBuilder("lottoBatchStep", jobRepository)
                .<PolyLotto, Winner>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public PolyLottoPool generateWinningNumbers() {
        PolyLottoPool currentPool = new PolyLottoPool();
        currentPool.generateWinningNumbers();
        return currentPool;
    }

    @Bean
    public ItemReader<PolyLotto> lottoBatchReader() {
        return new JpaPagingItemReaderBuilder<PolyLotto>()
                .name("lottoBatchReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT l FROM PolyLotto l WHERE l.round = (SELECT MAX(p.round) FROM PolyLottoPool p)")
                .pageSize(100)
                .build();
    }

    public int calculateRank(int matchCount, boolean bonusMatch) {
        if (matchCount == 6) return 1;
        if (matchCount == 5 && bonusMatch) return 2;
        if (matchCount == 5) return 3;
        if (matchCount == 4) return 4;
        if (matchCount == 3) return 5;
        return 6;
    }

    @Bean
    public ItemProcessor<PolyLotto, Winner> lottoBatchProcessor(PolyLottoPool currentPool) {
        return polyLotto -> {
            List<Integer> winningNumbers = currentPool.getWinningNumbers();
            int bonusNumber = currentPool.getBonusNumber();

            int matchCount = (int) polyLotto.getLottoNumbers().stream()
                    .filter(winningNumbers::contains)
                    .count();

            boolean bonusMatch = polyLotto.getBonusNumber() == bonusNumber;

            int rank = calculateRank(matchCount, bonusMatch);

            if (rank <= 5) {
                return new Winner(rank, winningNumbers.toString(), polyLotto.getLottoNumbers().toString(), String.valueOf(bonusNumber), currentPool.getRound());
            } else {
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<Winner> lottoBatchWriter() {
        JpaItemWriter<Winner> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        makePolyLottoResult();
        return writer;
    }

    public void makePolyLottoResult() {
        PolyLottoPool currentPool = polyLottoPoolService.getCurrentLottoPool();
        if (currentPool == null) return;
        int totalPrize = currentPool.getTotalPrize();

        // 전체의 50% 중 44%는 복권 기금으로 사용됨, 판매수수료 5.5%, 위탁 1.5%를 제외한 나머지 금액을 당첨금으로 사용
        int lotteryFund = (int) (totalPrize * 0.44);
        int salesCommission = (int) (totalPrize * 0.055);
        int commission = (int) (totalPrize * 0.015);
        int totalPrizePool = totalPrize - lotteryFund - salesCommission - commission;

        Map<Integer, Integer> winnerCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            winnerCounts.put(i, winnerService.getWinnerSizeByRoundAndRank(currentPool.getRound(), i));
        }

        int fifthPrizeTotal = winnerCounts.get(5) * 5000;
        int fourthPrizeTotal = winnerCounts.get(4) * 50000;

        int remainingPrize = totalPrizePool - fifthPrizeTotal - fourthPrizeTotal;
        int thirdPrizeTotal = (int) (remainingPrize * 0.125);
        int secondPrizeTotal = (int) (remainingPrize * 0.125);
        int firstPrizeTotal = remainingPrize - thirdPrizeTotal - secondPrizeTotal;

        PolyLottoResult result = new PolyLottoResult();
        result.setRound(currentPool.getRound());
        result.setWinningNumbers(currentPool.getWinningNumbers());
        result.setBonusNumber(currentPool.getBonusNumber());
        result.setTotalPrize(totalPrize);
        result.setLotteryFund(lotteryFund);
        result.setSalesCommission(salesCommission);
        result.setCommission(commission);
        result.setTotalLottoCount(currentPool.getTotalLottoCount());

        result.setFirstPrizeTotal(firstPrizeTotal);
        result.setFirstPrize(winnerCounts.get(1) > 0 ? firstPrizeTotal / winnerCounts.get(1) : 0);

        result.setSecondPrizeCount(winnerCounts.get(2));
        result.setSecondPrizeTotal(secondPrizeTotal);
        result.setSecondPrize(winnerCounts.get(2) > 0 ? secondPrizeTotal / winnerCounts.get(2) : 0);

        result.setThirdPrizeCount(winnerCounts.get(3));
        result.setThirdPrizeTotal(thirdPrizeTotal);
        result.setThirdPrize(winnerCounts.get(3) > 0 ? thirdPrizeTotal / winnerCounts.get(3) : 0);

        result.setFourthPrizeCount(winnerCounts.get(4));
        result.setFourthPrizeTotal(fourthPrizeTotal);

        result.setFifthPrizeCount(winnerCounts.get(5));
        result.setFifthPrizeTotal(fifthPrizeTotal);
        polyLottoResultService.save(result);
    }


    // 1분 마다 실행
    // @Scheduled(cron = "0 0/1 * * * *")
     @Scheduled(cron = "0 0 0 * * SUN")
    // 일요일 00시마다 실행
    public void runBatch() throws Exception {
        PolyLottoPool currentPool = generateWinningNumbers();
        Job job = lottoBatchJob(
                lottoBatchStep(lottoBatchReader(), lottoBatchProcessor(currentPool), lottoBatchWriter()));
        jobLauncher.run(job, new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    }
}