package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethod {
    private List<String> CASH;
    private List<String> ELECTRONIC_PAYMENT;
    private List<String> CARDS;

    public List<String> getCASH() {
        return CASH;
    }

    public List<String> getELECTRONIC_PAYMENT() {
        return ELECTRONIC_PAYMENT;
    }

    public List<String> getCARDS() {
        return CARDS;
    }

}
