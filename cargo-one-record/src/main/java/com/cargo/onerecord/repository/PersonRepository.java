package com.cargo.onerecord.repository;

import com.cargo.onerecord.model.party.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByIdAndIsDeletedFalse(UUID id);

    List<Person> findByCompanyIdAndIsDeletedFalse(UUID companyId);
}