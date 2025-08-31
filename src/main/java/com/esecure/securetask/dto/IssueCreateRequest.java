package com.esecure.securetask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IssueCreateRequest {
    @NotBlank @Size(max=120)
    private String title;
    @Size(max=2000)
    private String description;
    private Double latitude;
    private Double longitude;
    private String assignedGroup; // optional
    private String street;
    private String city;
    private String state;
    private String postalCode;

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public Double getLatitude(){ return latitude; }
    public void setLatitude(Double latitude){ this.latitude = latitude; }
    public Double getLongitude(){ return longitude; }
    public void setLongitude(Double longitude){ this.longitude = longitude; }
    public String getAssignedGroup(){ return assignedGroup; }
    public void setAssignedGroup(String assignedGroup){ this.assignedGroup = assignedGroup; }

    public String getStreet(){ return street; }
    public void setStreet(String street){ this.street = street; }
    public String getCity(){ return city; }
    public void setCity(String city){ this.city = city; }
    public String getState(){ return state; }
    public void setState(String state){ this.state = state; }
    public String getPostalCode(){ return postalCode; }
    public void setPostalCode(String postalCode){ this.postalCode = postalCode; }
}
