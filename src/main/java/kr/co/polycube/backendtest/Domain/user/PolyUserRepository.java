package kr.co.polycube.backendtest.Domain.user;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PolyUserRepository extends JpaRepository<PolyUser, Long> {
}
