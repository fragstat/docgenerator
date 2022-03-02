package ru.ferrotrade.docgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import ru.ferrotrade.docgenerator.assets.enums.Location;
import ru.ferrotrade.docgenerator.assets.enums.PositionStatus;

import java.text.DateFormat;
import java.util.Date;

@ToString
@AllArgsConstructor
@Getter
@Setter
public class Position {

    private Long id;

    private long createdFrom;

    @NonNull
    private String mark, diameter, packing, date;

    private String comment;

    @NonNull
    private String part, plav, manufacturer;

    private Float mass;

    private PositionStatus status;

    private Location location;

    private Package pack;

    public Position() {
    }

    public Position(String mark, String diameter, String packing, String comment, String part, String plav,
                    Float mass, String manufacturer, PositionStatus status, Location location) {
        this.mark = mark;
        this.diameter = diameter;
        this.packing = packing;
        this.date = DateFormat.getInstance().format(new Date()).trim();
        this.comment = comment;
        this.part = part;
        this.plav = plav;
        this.mass = mass;
        this.createdFrom = -1;
        this.manufacturer = manufacturer.trim().toUpperCase();
        this.status = status;
        this.location = location;
    }

    public Position(String mark, String diameter, String packing, String comment, String part, String plav,
                    Float mass, long createdFrom, String manufacturer, PositionStatus status, Location location) {
        this.mark = mark;
        this.diameter = diameter;
        this.packing = packing;
        this.date = DateFormat.getInstance().format(new Date()).trim();
        this.comment = comment;
        this.part = part;
        this.plav = plav;
        this.mass = mass;
        this.createdFrom = createdFrom;
        this.manufacturer = manufacturer;
        this.status = status;
        this.location = location;
    }

}
