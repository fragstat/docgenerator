package ru.ferrotrade.docgenerator.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ferrotrade.docgenerator.model.ChemicalElement;

import java.util.List;

@Data
@AllArgsConstructor
public class PlavInfoSaveView {

    public String plavId;

    public List<ChemicalElement> elements;

}
