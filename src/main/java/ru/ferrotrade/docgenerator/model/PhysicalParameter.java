package ru.ferrotrade.docgenerator.model;

public class PhysicalParameter {

    public String name;

    public String value;

    @Override
    public String toString() {
        return name + ":" + value + ";";
    }

    public PhysicalParameter(String name, String value) {
        this.name = name;
        this.value = value.trim().replaceAll(",", ".");
    }

}
