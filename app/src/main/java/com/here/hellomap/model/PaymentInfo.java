package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentInfo {
    private List<String> paymentType;
    private PaymentMethod paymentMethod;

    public List<String> getPaymentType() {
        return paymentType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
