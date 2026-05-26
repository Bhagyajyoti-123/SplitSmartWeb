package com.splitsmart.controller;

import com.splitsmart.ai.GeminiService;
import com.splitsmart.model.*;
import com.splitsmart.service.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SplitController {

    @Autowired SplitService splitService;
    @Autowired GeminiService geminiService;

    @PostMapping("/groups")
    public Group createGroup(@RequestBody Map<String, String> body) {
        return splitService.createGroup(body.get("name"));
    }

    @GetMapping("/groups")
    public List<Group> getAllGroups() {
        return splitService.getAllGroups();
    }

    @GetMapping("/groups/{id}")
    public Group getGroup(@PathVariable Long id) {
        return splitService.getGroup(id);
    }

    @PostMapping("/groups/{id}/members")
    public Person addMember(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return splitService.addMember(id, body.get("name"));
    }

    @PostMapping("/groups/{id}/expenses")
    public Expense addExpense(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String description = (String) body.get("description");
        double amount = Double.parseDouble(body.get("amount").toString());
        String category = (String) body.get("category");
        Long paidById = Long.parseLong(body.get("paidById").toString());
        List<Long> splitAmongIds = ((List<?>) body.get("splitAmongIds"))
                .stream().map(o -> Long.parseLong(o.toString())).toList();
        return splitService.addExpense(id, description, amount, category, paidById, splitAmongIds);
    }

    @GetMapping("/groups/{id}/balances")
    public Map<String, Double> getBalances(@PathVariable Long id) {
        return splitService.getBalances(id);
    }

    @GetMapping("/groups/{id}/settlements")
    public List<SettlementDTO> getSettlements(@PathVariable Long id) {
        return splitService.getSettlements(id);
    }

    @GetMapping("/groups/{id}/breakdown")
    public Map<String, Double> getBreakdown(@PathVariable Long id) {
        return splitService.getCategoryBreakdown(id);
    }

    @GetMapping("/groups/{id}/ai-insights")
    public Map<String, String> getAIInsights(@PathVariable Long id) {
        Map<String, Double> breakdown = splitService.getCategoryBreakdown(id);
        double total = splitService.getGroup(id).getTotalExpenses();
        String insight = geminiService.getSpendingInsight(breakdown, total);
        return Map.of("insight", insight);
    }
}
