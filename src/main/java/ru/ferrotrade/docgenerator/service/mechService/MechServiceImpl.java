package ru.ferrotrade.docgenerator.service.mechService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.model.ChemicalElement;
import ru.ferrotrade.docgenerator.model.PartData;
import ru.ferrotrade.docgenerator.model.PhysicalParameter;
import ru.ferrotrade.docgenerator.model.PlavData;
import ru.ferrotrade.docgenerator.repository.PartDataRepo;
import ru.ferrotrade.docgenerator.view.PartInfoSaveView;
import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MechServiceImpl implements MechService {

    private final PartDataRepo repo;

    @Override
    public PartInfoSaveView getPartView(String part) {
        PartData pd = repo.findById(part).orElse(null);
        if (pd != null) {
            List<PhysicalParameter> mechParams = Arrays.stream(pd.getParametersValues().split(";"))
                    .map(el -> new PhysicalParameter(el.split(":")[0], el.split(":")[1]))
                    .collect(Collectors.toList());
            return new PartInfoSaveView(part, mechParams);
        }
        return null;
    }

    @Override
    public void savePartInfo(PartInfoSaveView saveView) {
        StringBuilder sb = new StringBuilder();
        saveView.parameters.stream()
                .filter(e -> e.value != null && !e.value.isEmpty())
                .forEachOrdered(sb::append);
        if (!repo.existsById(saveView.partId.trim()) ||
                repo.getById(saveView.partId.trim()).getParametersValues().split(";").length
                        < saveView.parameters.size()) {
            repo.save(new PartData(saveView.partId.trim(), sb.toString()));
        }
    }
}
