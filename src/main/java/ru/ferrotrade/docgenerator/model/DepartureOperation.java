package ru.ferrotrade.docgenerator.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@NoArgsConstructor
@Data
public class DepartureOperation {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private long id;

    private String bill;

    private float weight;

    private List<Position> positions;

    private String contrAgent;

    private String username;

    private String date;

    public DepartureOperation(String bill, String customer, String username, List<Position> positions) {
        this.bill = bill;
        this.contrAgent = customer;
        this.username = username;
        this.positions = positions;
        this.date = dateFormat.format(Calendar.getInstance().getTime());
        this.weight = (float) positions.stream().mapToDouble(Position::getMass).sum();
    }
}


