package com.here.hellomap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSegment {
    private String id;
    private Address address;
    private Availability availability;
    private PaymentInfo paymentInfo;
    private List<ParkingRestriction> parkingRestrictions;
    private ContactInfo contactInfo;
    private TpegOpenLR tpegOpenLR;
    private List<Probability> probability;
    private int capacity;
    private PriceSchema priceSchema;

    public String getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public Availability getAvailability() {
        return availability;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public List<ParkingRestriction> getParkingRestrictions() {
        return parkingRestrictions;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public TpegOpenLR getTpegOpenLR() {
        return tpegOpenLR;
    }

    public List<Probability> getProbability() {
        return probability;
    }

    public int getCapacity() {
        return capacity;
    }

    public PriceSchema getPriceSchema() {
        return priceSchema;
    }
}
