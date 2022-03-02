package ru.ferrotrade.docgenerator.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "generated_certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mark;
    private String diameter;
    private String packing;
    private String gost;
    private String plav;
    private String part;
    private String qr;
    private Date date;

}
