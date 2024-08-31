package kr.co.polycube.backendtest.Domain.winner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WinnerService {
    private final WinnerRepository winnerRepository;

    public Winner getWinnerByRound(int round) {
        return winnerRepository.getWinnerByRound(round);
    }

    public int getWinnerSizeByRoundAndRank(int round, int rank) {
        return winnerRepository.getWinnerByRoundAndRank(round, rank).size();
    }
}
