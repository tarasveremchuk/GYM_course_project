package com.gym.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Data
public class PaymentStats {
    private Map<LocalDate, Map<String, BigDecimal>> dailyAmounts = new TreeMap<>();
    private Map<LocalDate, Map<String, Integer>> membershipCounts = new TreeMap<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    public void addDailyAmount(LocalDate date, String paymentMethod, BigDecimal amount) {
        dailyAmounts.computeIfAbsent(date, k -> new HashMap<>())
                   .merge(paymentMethod, amount, BigDecimal::add);
        totalAmount = totalAmount.add(amount);
    }
    
    public Map<String, BigDecimal> getTotalsByPaymentMethod() {
        Map<String, BigDecimal> totals = new HashMap<>();
        dailyAmounts.values().forEach(methodAmounts ->
            methodAmounts.forEach((method, amount) ->
                totals.merge(method, amount, BigDecimal::add)));
        return totals;
    }
    
    public List<LocalDate> getDates() {
        return new ArrayList<>(dailyAmounts.keySet());
    }
    
    public BigDecimal getDailyTotal(LocalDate date) {
        return dailyAmounts.getOrDefault(date, Collections.emptyMap())
                          .values()
                          .stream()
                          .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Повертає суми за методами оплати для конкретної дати
     * 
     * @param date Дата, для якої потрібно отримати дані
     * @return Мапа, де ключ - метод оплати, значення - сума
     */
    public Map<String, BigDecimal> getDailyAmountsByMethod(LocalDate date) {
        return dailyAmounts.getOrDefault(date, Collections.emptyMap());
    }
    
    /**
     * Додає кількість абонементів для конкретної дати та типу
     * 
     * @param date Дата продажу
     * @param membershipType Тип абонементу
     * @param count Кількість проданих абонементів
     */
    public void addMembershipCount(LocalDate date, String membershipType, int count) {
        membershipCounts.computeIfAbsent(date, k -> new HashMap<>())
                       .merge(membershipType, count, Integer::sum);
    }
    
    /**
     * Повертає кількість абонементів за типами для конкретної дати
     * 
     * @param date Дата, для якої потрібно отримати дані
     * @return Мапа, де ключ - тип абонементу, значення - кількість
     */
    public Map<String, Integer> getMembershipCountsByType(LocalDate date) {
        return membershipCounts.getOrDefault(date, Collections.emptyMap());
    }
    
    /**
     * Повертає загальну кількість абонементів за типами
     * 
     * @return Мапа, де ключ - тип абонементу, значення - кількість
     */
    public Map<String, Integer> getTotalMembershipCountsByType() {
        Map<String, Integer> totals = new HashMap<>();
        membershipCounts.values().forEach(typeCounts ->
            typeCounts.forEach((type, count) ->
                totals.merge(type, count, Integer::sum)));
        return totals;
    }
}