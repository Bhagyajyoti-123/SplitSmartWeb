package com.splitsmart.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double totalPaid;
    private double totalShare;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;

    public Person() {}

    public Person(String name, Group group) {
        this.name = name;
        this.group = group;
        this.totalPaid = 0;
        this.totalShare = 0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getTotalPaid() { return totalPaid; }
    public double getTotalShare() { return totalShare; }
    public Group getGroup() { return group; }
    public double getBalance() { return totalPaid - totalShare; }

    public void setTotalPaid(double v) { this.totalPaid = v; }
    public void setTotalShare(double v) { this.totalShare = v; }
    public void setName(String name) { this.name = name; }
    public void setGroup(Group group) { this.group = group; }
}
