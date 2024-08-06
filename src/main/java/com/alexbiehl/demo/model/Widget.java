package com.alexbiehl.demo.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Widget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String shortDescription;
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "widget_locations",
            joinColumns = @JoinColumn(
                    name = "widget_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "location_id", referencedColumnName = "id"))
    private Set<Location> availableLocations;

    public Widget() {
    }

    public Widget(long id, String shortDescription, String description) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public Widget(String shortDescription, String description) {
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Location> getAvailableLocations() {
        return availableLocations;
    }

    public void setAvailableLocations(Set<Location> availableLocations) {
        this.availableLocations = availableLocations;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Widget widget)) return false;

        return getId() == widget.getId() && getShortDescription().equals(widget.getShortDescription()) && Objects.equals(getDescription(), widget.getDescription());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getShortDescription().hashCode();
        result = 31 * result + Objects.hashCode(getDescription());
        return result;
    }
}
