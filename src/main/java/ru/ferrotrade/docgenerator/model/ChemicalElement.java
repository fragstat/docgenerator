package ru.ferrotrade.docgenerator.model;

public class ChemicalElement {

    public String name;

    public String value;

    @Override
    public String toString() {
        return name + ":" + value + ";";
    }

    public ChemicalElement(String name, String value) {
        this.name = name;
        this.value = value.trim().replaceAll(",", ".");
    }
}
