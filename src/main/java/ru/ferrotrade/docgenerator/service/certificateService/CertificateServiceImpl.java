package ru.ferrotrade.docgenerator.service.certificateService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.model.Certificate;
import ru.ferrotrade.docgenerator.repository.CertificateRepo;
import ru.ferrotrade.docgenerator.service.awsS3service.AwsS3Service;
import ru.ferrotrade.docgenerator.service.chemistryService.ChemistryService;
import ru.ferrotrade.docgenerator.service.mechService.MechService;
import ru.ferrotrade.docgenerator.service.zeroCertificateGenerationService.ZeroCertificateGeneratingService;
import ru.ferrotrade.docgenerator.view.ZeroCertificateGenerationView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

@RequiredArgsConstructor
@Service
public class CertificateServiceImpl implements CertificateService {

    private final ZeroCertificateGeneratingService certificateGeneratingService;
    private final CertificateRepo certificateRepo;
    private final MechService mechService;
    private final ChemistryService chemistryService;
    private final AwsS3Service awsS3Service;

    @Override
    public File saveCertificate(ZeroCertificateGenerationView view) {
        mechService.savePartInfo(view.part);
        chemistryService.savePlavInfo(view.plav);
        Certificate certificate;
        if (!certificateRepo.existsByPart(view.part.partId)) {
            certificate = new Certificate();
            certificate.setMark(view.mark.trim());
            certificate.setDiameter(view.diameter.trim().replaceAll(",", "."));
            certificate.setPacking(view.packing);
            certificate.setGost(view.gost);
            certificate.setPart(view.part.partId);
            certificate.setPlav(view.plav.plavId);
            certificate.setDate(Calendar.getInstance().getTime());
            certificateRepo.save(certificate);
        } else {
            certificate = certificateRepo.getByPart(view.part.partId);
        }
        return getOrGenerateCertificate(certificate);
    }

    private File getOrGenerateCertificate(Certificate certificate) {
        File f;
        if (awsS3Service.containsZeroCertificate(certificate.getPart())) {
            f = new File(certificate.getPart() + ".docx");
            try {
                FileUtils.writeByteArrayToFile(f, awsS3Service.downloadZeroCertificate(certificate.getPart()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        } else {
            f = certificateGeneratingService.generateCertificatesFromCertificate(certificate);
            awsS3Service.uploadZeroCertificate(f);
        }
        return f;
    }

}
