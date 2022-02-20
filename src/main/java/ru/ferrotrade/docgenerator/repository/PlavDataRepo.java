package ru.ferrotrade.docgenerator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.ferrotrade.docgenerator.model.PlavData;

@Repository
public interface PlavDataRepo extends CrudRepository<PlavData, String> {
}
