package ru.ferrotrade.docgenerator.service.zeroCertificateGenerationService;

import ru.ferrotrade.docgenerator.model.Certificate;
import ru.ferrotrade.docgenerator.model.DepartureOperation;

import java.io.File;
import java.io.FileOutputStream;

public interface ZeroCertificateGeneratingService {

    File generateCertificatesFromCertificate(Certificate certificate);

    boolean checkAbilityToGenerateCertificates(DepartureOperation operation);

    boolean getDocs();

}
