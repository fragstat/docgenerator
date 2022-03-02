package ru.ferrotrade.docgenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ferrotrade.docgenerator.model.Certificate;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {

    boolean existsByPart(String part);

    Certificate getByPart(String part);
}