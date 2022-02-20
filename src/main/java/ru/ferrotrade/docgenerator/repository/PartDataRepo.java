package ru.ferrotrade.docgenerator.repository;

import org.springframework.data.repository.CrudRepository;
import ru.ferrotrade.docgenerator.model.PartData;

public interface PartDataRepo extends CrudRepository<PartData, String> {
}
