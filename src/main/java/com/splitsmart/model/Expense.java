package com.splitsmart.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double amount;
    private String category;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "paid_by_id")
    private Person paidBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "expense_splits",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> splitAmong = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;

    public Expense() {}

    public Expense(String description, double amount, String category,
                   Person paidBy, List<Person> splitAmong, Group group) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.paidBy = paidBy;
        this.splitAmong = splitAmong;
        this.group = group;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public Person getPaidBy() { return paidBy; }
    public List<Person> getSplitAmong() { return splitAmong; }
    public Group getGroup() { return group; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
