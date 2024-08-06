package com.alexbiehl.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String description;
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String country;

    public Location() {
    }

    public Location(long id, String name, String description, String streetAddress, String city, String state, String zipcode, String country) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
    }

    public Location(String name, String description, String streetAddress, String city, String state, String zipcode, String country) {
        this.name = name;
        this.description = description;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        return getId() == location.getId() &&
                getName().equals(location.getName()) &&
                Objects.equals(getDescription(), location.getDescription()) &&
                Objects.equals(getStreetAddress(), location.getStreetAddress()) &&
                Objects.equals(getCity(), location.getCity()) &&
                Objects.equals(getState(), location.getState()) &&
                Objects.equals(getZipcode(), location.getZipcode()) &&
                Objects.equals(getCountry(), location.getCountry());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getName().hashCode();
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(getStreetAddress());
        result = 31 * result + Objects.hashCode(getCity());
        result = 31 * result + Objects.hashCode(getState());
        result = 31 * result + Objects.hashCode(getZipcode());
        result = 31 * result + Objects.hashCode(getCountry());
        return result;
    }
}
