package kr.co.polycube.backendtest.Domain.winner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    Winner getWinnerByRound(int round);
    List<Winner> getWinnerByRoundAndRank(int round, int rank);
}
