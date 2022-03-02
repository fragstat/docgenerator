package ru.ferrotrade.docgenerator.service.barcodeGenerationService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface BarcodeGenerationService {

    ByteArrayInputStream generateCode();

}
