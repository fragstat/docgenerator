package ru.ferrotrade.docgenerator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class PlavData {

    @Id
    private String plavId;

    private String elementsValues;

    public PlavData(String plavId, String elementsValues) {
        this.plavId = plavId;
        this.elementsValues = elementsValues;
    }
}
