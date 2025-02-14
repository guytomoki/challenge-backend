package ch.guytomoki.challenge.signingRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SigningRequestRepository extends JpaRepository<SigningRequest, Long> {

	Optional<SigningRequest> findFirstByUserEmailOrderById(String email);
}
