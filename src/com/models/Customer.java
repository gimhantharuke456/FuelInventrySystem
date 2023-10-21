package com.models;

public class Customer {
    private int customerId;
    private String customerName;
    private String contactInfo;
    private boolean loyaltyMembership;
    private double creditLimit;

    // Getter and Setter methods

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean isLoyaltyMembership() {
        return loyaltyMembership;
    }

    public void setLoyaltyMembership(boolean loyaltyMembership) {
        this.loyaltyMembership = loyaltyMembership;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
