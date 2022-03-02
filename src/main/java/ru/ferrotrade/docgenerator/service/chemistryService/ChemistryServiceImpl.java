package ru.ferrotrade.docgenerator.service.chemistryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ferrotrade.docgenerator.model.ChemicalElement;
import ru.ferrotrade.docgenerator.model.PlavData;
import ru.ferrotrade.docgenerator.repository.PlavDataRepo;
import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChemistryServiceImpl implements ChemistryService {

    private final PlavDataRepo repo;

    @Override
    public PlavInfoSaveView getPlavView(String plav) {
        PlavData pd = repo.findById(plav).orElse(null);
        if (pd != null) {
            List<ChemicalElement> chemicals = Arrays.stream(pd.getElementsValues().split(";"))
                    .map(el -> new ChemicalElement(el.split(":")[0], el.split(":")[1]))
                    .collect(Collectors.toList());
            return new PlavInfoSaveView(plav, chemicals);
        }
        return null;
    }

    @Override
    public void savePlavInfo(PlavInfoSaveView saveView) {
        StringBuilder sb = new StringBuilder();
        saveView.elements.stream()
                .filter(e -> e.value != null && !e.value.isEmpty())
                .forEachOrdered(sb::append);
        if (!repo.existsById(saveView.plavId.trim()) ||
                repo.getById(saveView.plavId.trim()).getElementsValues().split(";").length
                        == saveView.elements.size()) {
            repo.save(new PlavData(saveView.plavId.trim(), sb.toString()));
        }
    }
}
