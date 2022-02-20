package ru.ferrotrade.docgenerator.service.documentParsingService;

import ru.ferrotrade.docgenerator.view.PartInfoSaveView;
import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;

import java.io.File;

public interface DocumentParsingService {

    boolean savePlavInfo(PlavInfoSaveView plavInfoSaveView);

    boolean savePartInfo(PartInfoSaveView partInfoSaveView);

    boolean parseCert(File f);

    boolean convertToDocx();
}
