package ru.ferrotrade.docgenerator.service.documentGeneratingService;

import ru.ferrotrade.docgenerator.model.DepartureOperation;

import java.io.File;

public interface DocumentGeneratingService {

    File generateCertificatesFromDepartureOperation(DepartureOperation operation);

    boolean checkAbilityToGenerateCertificates(DepartureOperation operation);

    boolean getDocs();

}
