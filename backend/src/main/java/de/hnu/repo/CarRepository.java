package de.hnu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import de.hnu.domain.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {}