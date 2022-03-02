package ru.ferrotrade.docgenerator.service.barcodeGenerationService;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class EanGenerationServiceImpl implements BarcodeGenerationService {

    @Override
    public ByteArrayInputStream generateCode() {
        return null;
    }
}
