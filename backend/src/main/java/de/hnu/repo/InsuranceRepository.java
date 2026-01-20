package de.hnu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import de.hnu.domain.Insurance;

public interface InsuranceRepository extends JpaRepository<Insurance, Integer> {}