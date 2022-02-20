package ru.ferrotrade.docgenerator.view;

import lombok.Data;
import ru.ferrotrade.docgenerator.model.PhysicalParameter;

import java.util.List;

@Data
public class PartInfoSaveView {

    public String partId;

    public List<PhysicalParameter> parameters;

    public PartInfoSaveView(String partId, List<PhysicalParameter> parameters) {
        this.partId = partId;
        this.parameters = parameters;
    }
}
