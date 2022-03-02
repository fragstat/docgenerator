package ru.ferrotrade.docgenerator.service.certificateService;

import ru.ferrotrade.docgenerator.view.ZeroCertificateGenerationView;

import java.io.File;

public interface CertificateService {

    File saveCertificate(ZeroCertificateGenerationView view);

}
