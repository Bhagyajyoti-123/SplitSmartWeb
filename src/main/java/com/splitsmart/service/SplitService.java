package com.splitsmart.service;

import com.splitsmart.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SplitService {

    @Autowired GroupRepository groupRepo;
    @Autowired PersonRepository personRepo;
    @Autowired ExpenseRepository expenseRepo;

    public Group createGroup(String name) {
        return groupRepo.save(new Group(name));
    }

    public List<Group> getAllGroups() {
        return groupRepo.findAll();
    }

    public Group getGroup(Long id) {
        return groupRepo.findById(id).orElseThrow();
    }

    public Person addMember(Long groupId, String name) {
        Group group = getGroup(groupId);
        Person person = new Person(name, group);
        return personRepo.save(person);
    }

    public Expense addExpense(Long groupId, String description, double amount,
                               String category, Long paidById, List<Long> splitAmongIds) {
        Group group = getGroup(groupId);
        Person paidBy = personRepo.findById(paidById).orElseThrow();
        List<Person> splitAmong = personRepo.findAllById(splitAmongIds);
        return expenseRepo.save(new Expense(description, amount, category, paidBy, splitAmong, group));
    }

    public Map<String, Double> getBalances(Long groupId) {
        Group group = getGroup(groupId);
        Map<String, Double> paid = new HashMap<>();
        Map<String, Double> owed = new HashMap<>();

        for (Person p : group.getMembers()) {
            paid.put(p.getName(), 0.0);
            owed.put(p.getName(), 0.0);
        }
        for (Expense e : group.getExpenses()) {
            paid.merge(e.getPaidBy().getName(), e.getAmount(), Double::sum);
            double share = e.getAmount() / e.getSplitAmong().size();
            for (Person p : e.getSplitAmong()) {
                owed.merge(p.getName(), share, Double::sum);
            }
        }

        Map<String, Double> balances = new HashMap<>();
        for (String name : paid.keySet()) {
            balances.put(name, Math.round((paid.get(name) - owed.getOrDefault(name, 0.0)) * 100.0) / 100.0);
        }
        return balances;
    }

    public List<SettlementDTO> getSettlements(Long groupId) {
        Map<String, Double> balances = getBalances(groupId);
        List<Map.Entry<String, Double>> creditors = new ArrayList<>();
        List<Map.Entry<String, Double>> debtors = new ArrayList<>();

        for (Map.Entry<String, Double> e : balances.entrySet()) {
            if (e.getValue() > 0.01) creditors.add(e);
            else if (e.getValue() < -0.01) debtors.add(e);
        }

        List<SettlementDTO> settlements = new ArrayList<>();
        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            double amount = Math.min(creditors.get(i).getValue(), -debtors.get(j).getValue());
            amount = Math.round(amount * 100.0) / 100.0;
            if (amount > 0.01) {
                settlements.add(new SettlementDTO(
                        debtors.get(j).getKey(), creditors.get(i).getKey(), amount));
            }
            creditors.get(i).setValue(creditors.get(i).getValue() - amount);
            debtors.get(j).setValue(debtors.get(j).getValue() + amount);
            if (Math.abs(creditors.get(i).getValue()) < 0.01) i++;
            if (Math.abs(debtors.get(j).getValue()) < 0.01) j++;
        }
        return settlements;
    }

    public Map<String, Double> getCategoryBreakdown(Long groupId) {
        Group group = getGroup(groupId);
        Map<String, Double> breakdown = new HashMap<>();
        for (Expense e : group.getExpenses()) {
            breakdown.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return breakdown;
    }
}
