package com.example.tracker.dto.dashboard;

public class DashboardCardDTO {

    private String label;
    private Integer value;
    private String sub;
    private String color;
    private String icon;

    public static DashboardCardDTO of(String label, Integer value, String sub, String color, String icon) {
        DashboardCardDTO dto = new DashboardCardDTO();
        dto.setLabel(label);
        dto.setValue(value);
        dto.setSub(sub);
        dto.setColor(color);
        dto.setIcon(icon);
        return dto;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
