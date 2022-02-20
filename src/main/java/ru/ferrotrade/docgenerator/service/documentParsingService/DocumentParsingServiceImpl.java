package ru.ferrotrade.docgenerator.service.documentParsingService;

import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.model.ChemicalElement;
import ru.ferrotrade.docgenerator.model.PartData;
import ru.ferrotrade.docgenerator.view.PartInfoSaveView;
import ru.ferrotrade.docgenerator.model.PhysicalParameter;
import ru.ferrotrade.docgenerator.model.PlavData;
import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;
import ru.ferrotrade.docgenerator.repository.PartDataRepo;
import ru.ferrotrade.docgenerator.repository.PlavDataRepo;

import java.io.File;
import java.util.List;

@Service
public class DocumentParsingServiceImpl implements DocumentParsingService {

    private final PartDataRepo partDataRepo;

    private final PlavDataRepo plavDataRepo;

    public DocumentParsingServiceImpl(PartDataRepo partDataRepo, PlavDataRepo plavDataRepo) {
        this.partDataRepo = partDataRepo;
        this.plavDataRepo = plavDataRepo;
    }

    @Override
    public boolean savePlavInfo(PlavInfoSaveView plavInfoSaveView) {
        if (plavInfoSaveView != null && plavInfoSaveView.plavId != null && plavInfoSaveView.elements != null && !plavInfoSaveView.elements.isEmpty()) {
            PlavData plavData = new PlavData(plavInfoSaveView.plavId.trim(),
                    parseElementsToString(plavInfoSaveView.elements));
            if (!plavDataRepo.existsById(plavInfoSaveView.plavId.trim())) {
                plavDataRepo.save(plavData);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean savePartInfo(PartInfoSaveView partInfoSaveView) {
        if (partInfoSaveView != null && partInfoSaveView.partId != null && partInfoSaveView.parameters != null && !partInfoSaveView.parameters.isEmpty()) {
            PartData partData = new PartData(partInfoSaveView.partId.trim(),
                    parseParameterToString(partInfoSaveView.parameters));
            if (!partDataRepo.existsById(partInfoSaveView.partId.trim())) {
                try {
                    partDataRepo.save(partData);
                } catch (Exception e) {
                    System.out.println(partInfoSaveView.partId);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean parseCert(File f) {
        //TODO
        return false;
    }

    @Override
    public boolean convertToDocx() {
        //TODO
        return false;
    }

    private static String parseElementsToString(List<ChemicalElement> elements) {
        StringBuilder sb = new StringBuilder();
        for (ChemicalElement el : elements) {
            sb.append(el.toString());
        }
        return sb.toString();
    }

    private static String parseParameterToString(List<PhysicalParameter> parameters) {
        StringBuilder sb = new StringBuilder();
        for (PhysicalParameter parameter : parameters) {
            sb.append(parameter.toString());
        }
        return sb.toString();
    }

}
