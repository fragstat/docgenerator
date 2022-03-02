package ru.ferrotrade.docgenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.ferrotrade.docgenerator.model.PlavData;

@Repository
public interface PlavDataRepo extends JpaRepository<PlavData, String> {
}
