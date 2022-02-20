package ru.ferrotrade.docgenerator.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ferrotrade.docgenerator.model.DepartureOperation;
import ru.ferrotrade.docgenerator.model.Position;

@Data
@AllArgsConstructor
public class CertificateGenerationDataView {

    public Position etalon;

    public DepartureOperation departureOperation;

    public Double weight;

    public Integer amount;

    public Integer order;

    public CertificateGenerationDataView(Position etalon, DepartureOperation departureOperation, Double weight, Integer amount) {
        this.etalon = etalon;
        this.departureOperation = departureOperation;
        this.weight = weight;
        this.amount = amount;
    }
}
