package de.hnu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import de.hnu.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {}