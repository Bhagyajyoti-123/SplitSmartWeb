package com.splitsmart.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Person> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Expense> expenses = new ArrayList<>();

    public Group() {}
    public Group(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Person> getMembers() { return members; }
    public List<Expense> getExpenses() { return expenses; }
    public void setName(String name) { this.name = name; }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}
