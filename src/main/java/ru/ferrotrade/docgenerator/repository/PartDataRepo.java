package ru.ferrotrade.docgenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.ferrotrade.docgenerator.model.PartData;

public interface PartDataRepo extends JpaRepository<PartData, String> {
}
