package ru.ferrotrade.docgenerator.service.chemistryService;

import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;

public interface ChemistryService {

    PlavInfoSaveView getPlavView(String plav);

    void savePlavInfo(PlavInfoSaveView saveView);
}
