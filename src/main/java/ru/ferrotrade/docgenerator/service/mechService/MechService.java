package ru.ferrotrade.docgenerator.service.mechService;

import ru.ferrotrade.docgenerator.view.PartInfoSaveView;

public interface MechService {

    PartInfoSaveView getPartView(String part);

    void savePartInfo(PartInfoSaveView saveView);

}
