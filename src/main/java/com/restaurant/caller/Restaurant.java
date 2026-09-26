package com.restaurant.caller;

public class Restaurant {

    private String restaurant;
    private String phoneNumber;
    private String promotion;
    private String payment;
    private String ifYesDay;
    private String status;

    public Restaurant() {
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getIfYesDay() {
        return ifYesDay;
    }

    public void setIfYesDay(String ifYesDay) {
        this.ifYesDay = ifYesDay;
    }

    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }
}