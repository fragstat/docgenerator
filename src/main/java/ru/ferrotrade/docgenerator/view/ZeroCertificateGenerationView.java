package ru.ferrotrade.docgenerator.view;

import lombok.Data;

@Data
public class ZeroCertificateGenerationView {

    public String mark;

    public String diameter;

    public String packing;

    public String gost;

    public PlavInfoSaveView plav;

    public PartInfoSaveView part;

}
