package com.flightapp.bookings.model;

public class Passenger {
    private String name;
    private String gender;
    private Integer age;
    private String mealPreference;
    
    public Passenger() {
    }
    
    public Passenger(String name, String gender, Integer age, String mealPreference) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.mealPreference = mealPreference;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getMealPreference() {
        return mealPreference;
    }
    
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
}
