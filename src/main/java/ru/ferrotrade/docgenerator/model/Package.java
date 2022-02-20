package ru.ferrotrade.docgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ferrotrade.docgenerator.assets.enums.Location;
import ru.ferrotrade.docgenerator.assets.enums.PositionStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Package {

    private long id;

    private String mark, diameter, packing, date;

    private String comment;

    private String part, plav, manufacturer;

    private PositionStatus status;

    private Location location;

    @Column(name = "weight")
    private double mass = 0.0;

    private List<Position> positionsList;

}
