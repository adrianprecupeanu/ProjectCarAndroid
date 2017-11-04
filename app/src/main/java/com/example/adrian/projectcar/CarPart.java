package com.example.adrian.projectcar;

public class CarPart {
    private String carPartNameField;
    private String sellerEmailField;
    private int estimatedPriceField;

    public CarPart() {
    }

    public String getCarPartNameField() {
        return carPartNameField;
    }

    public String getSellerEmailField() {
        return sellerEmailField;
    }

    public int getEstimatedPriceField() {
        return estimatedPriceField;
    }

    public void setCarPartNameField(String carPartNameField) {
        this.carPartNameField = carPartNameField;
    }

    public void setSellerEmailField(String sellerEmailField) {
        this.sellerEmailField = sellerEmailField;
    }

    public void setEstimatedPriceField(int estimatedPriceField) {
        this.estimatedPriceField = estimatedPriceField;
    }
}
