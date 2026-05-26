package com.splitsmart.service;

import com.splitsmart.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByGroupId(Long groupId);
}
