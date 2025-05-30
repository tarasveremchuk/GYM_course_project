package com.gym.dao.impl;

import com.gym.model.*;
import com.gym.util.DatabaseConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class ReportDao {
    
    
    public Map<Client, Integer> getClientVisitStats(LocalDateTime from, LocalDateTime to) {
        Map<Client, Integer> visitStats = new HashMap<>();
        String sql = "SELECT c.id, c.full_name, COUNT(v.id) as visit_count " +
                    "FROM clients c " +
                    "LEFT JOIN visits v ON c.id = v.client_id " +
                    "WHERE v.visit_date BETWEEN ? AND ? " +
                    "GROUP BY c.id, c.full_name " +
                    "ORDER BY visit_count DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getLong("id"));
                client.setFullName(rs.getString("full_name"));
                visitStats.put(client, rs.getInt("visit_count"));
            }
        } catch (SQLException e) {
            log.error("Error getting client visit statistics", e);
        }
        return visitStats;
    }
    
    
    public List<TrainerStats> getTrainerStats(LocalDateTime from, LocalDateTime to) {
        List<TrainerStats> trainerStats = new ArrayList<>();
        String sql = "SELECT " +
                    "    s.id, s.full_name, " +
                    "    COUNT(DISTINCT b.client_id) as total_clients, " +
                    "    COUNT(b.id) as total_bookings, " +
                    "    SUM(CASE WHEN b.status = 'attended' THEN 1 ELSE 0 END) as attended_count, " +
                    "    SUM(CASE WHEN b.status = 'cancelled' THEN 1 ELSE 0 END) as cancelled_count " +
                    "FROM staff s " +
                    "LEFT JOIN trainings t ON s.id = t.trainer_id " +
                    "LEFT JOIN bookings b ON t.id = b.training_id " +
                    "WHERE s.role = 'trainer' " +
                    "AND t.scheduled_at BETWEEN ? AND ? " +
                    "GROUP BY s.id, s.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainerStats stats = new TrainerStats();
                stats.setTrainerId(rs.getLong("id"));
                stats.setTrainerName(rs.getString("full_name"));
                stats.setTotalClients(rs.getInt("total_clients"));
                stats.setTotalBookings(rs.getInt("total_bookings"));
                stats.setAttendedCount(rs.getInt("attended_count"));
                stats.setCancelledCount(rs.getInt("cancelled_count"));
                trainerStats.add(stats);
            }
        } catch (SQLException e) {
            log.error("Error getting trainer statistics", e);
        }
        return trainerStats;
    }
    
    
    public PaymentStats getPaymentStats(LocalDateTime from, LocalDateTime to) {
        PaymentStats stats = new PaymentStats();
        
        
        String membershipSql = "SELECT " +
                    "    m.type as membership_type, " +
                    "    m.is_paid, " +
                    "    m.price as amount, " +
                    "    m.start_date, " +
                    "    COUNT(*) as membership_count " +
                    "FROM memberships m " +
                    "WHERE m.start_date BETWEEN ? AND ? " +
                    "GROUP BY m.type, m.is_paid, m.start_date, m.price " +
                    "ORDER BY m.start_date, m.type";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(membershipSql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("membership_type");
                boolean isPaid = rs.getBoolean("is_paid");
                BigDecimal amount = rs.getBigDecimal("amount");
                LocalDate date = rs.getDate("start_date").toLocalDate();
                int count = rs.getInt("membership_count");
                
                
                String typeWithStatus = type.toUpperCase() + " (" + (isPaid ? "PAID" : "UNPAID") + ")";
                
                
                BigDecimal totalAmount = amount.multiply(BigDecimal.valueOf(count));
                
                
                stats.addDailyAmount(date, typeWithStatus, totalAmount);
                
                
                stats.addMembershipCount(date, typeWithStatus, count);
            }
            
        } catch (SQLException e) {
            log.error("Error getting membership payment statistics", e);
        }
        
        String sql = "SELECT " +
                    "    SUM(amount) as total_amount, " +
                    "    method, " +
                    "    DATE(payment_date) as pay_date " +
                    "FROM payments " +
                    "WHERE payment_date BETWEEN ? AND ? " +
                    "GROUP BY method, DATE(payment_date) " +
                    "ORDER BY pay_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.addDailyAmount(
                    rs.getDate("pay_date").toLocalDate(),
                    rs.getString("method"),
                    rs.getBigDecimal("total_amount")
                );
            }
        } catch (SQLException e) {
            log.error("Error getting payment statistics", e);
        }
        return stats;
    }
    
    
    public List<OutstandingPayment> getOutstandingPayments() {
        List<OutstandingPayment> outstandingPayments = new ArrayList<>();
        String sql = "SELECT " +
                    "    c.id as client_id, " +
                    "    c.full_name, " +
                    "    m.type as membership_type, " +
                    "    m.start_date, " +
                    "    m.end_date " +
                    "FROM memberships m " +
                    "JOIN clients c ON m.client_id = c.id " +
                    "WHERE m.is_paid = false";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OutstandingPayment payment = new OutstandingPayment();
                payment.setClientId(rs.getLong("client_id"));
                payment.setClientName(rs.getString("full_name"));
                payment.setMembershipType(rs.getString("membership_type"));
                
                
                java.sql.Date startDate = rs.getDate("start_date");
                if (startDate != null) {
                    payment.setStartDate(startDate.toLocalDate());
                }
                
                java.sql.Date endDate = rs.getDate("end_date");
                if (endDate != null) {
                    payment.setEndDate(endDate.toLocalDate());
                }
                outstandingPayments.add(payment);
            }
        } catch (SQLException e) {
            log.error("Error getting outstanding payments", e);
        }
        return outstandingPayments;
    }
    
    
    public List<TrainingStats> getTopTrainings(int limit, LocalDateTime from, LocalDateTime to) {
        List<TrainingStats> topTrainings = new ArrayList<>();
        String sql = "SELECT " +
                    "    t.id, " +
                    "    t.name, " +
                    "    COUNT(b.id) as total_bookings, " +
                    "    SUM(CASE WHEN b.status = 'attended' THEN 1 ELSE 0 END) as attended_count, " +
                    "    s.full_name as trainer_name " +
                    "FROM trainings t " +
                    "LEFT JOIN bookings b ON t.id = b.training_id " +
                    "LEFT JOIN staff s ON t.trainer_id = s.id " +
                    "WHERE t.scheduled_at BETWEEN ? AND ? " +
                    "GROUP BY t.id, t.name, s.full_name " +
                    "ORDER BY total_bookings DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainingStats stats = new TrainingStats();
                stats.setTrainingId(rs.getLong("id"));
                stats.setTrainingName(rs.getString("name"));
                stats.setTotalBookings(rs.getInt("total_bookings"));
                stats.setAttendedCount(rs.getInt("attended_count"));
                stats.setTrainerName(rs.getString("trainer_name"));
                topTrainings.add(stats);
            }
        } catch (SQLException e) {
            log.error("Error getting top trainings", e);
        }
        return topTrainings;
    }
    
    
    public Map<String, Integer> getVisitsByDayOfWeek(LocalDateTime from, LocalDateTime to) {
        Map<String, Integer> visitsByDay = new LinkedHashMap<>();
        String sql = "SELECT " +
                    "    EXTRACT(DOW FROM visit_date) as day_of_week, " +
                    "    COUNT(*) as visit_count " +
                    "FROM visits " +
                    "WHERE visit_date BETWEEN ? AND ? " +
                    "GROUP BY day_of_week " +
                    "ORDER BY day_of_week";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            while (rs.next()) {
                int dayIndex = rs.getInt("day_of_week");
                visitsByDay.put(days[dayIndex], rs.getInt("visit_count"));
            }
        } catch (SQLException e) {
            log.error("Error getting visits by day of week", e);
        }
        return visitsByDay;
    }
} 