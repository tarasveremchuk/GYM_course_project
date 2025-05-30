package com.gym.repository;

import com.gym.model.StaffDTO;
import com.gym.model.StaffRole;
import com.gym.util.DatabaseConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StaffRepository {

    public List<StaffDTO> findAll() {
        List<StaffDTO> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StaffDTO staff = mapResultSetToStaff(rs);
                staffList.add(staff);
            }
        } catch (SQLException e) {
            log.error("Error fetching all staff members: ", e);
        }
        return staffList;
    }

    public StaffDTO findById(Long id) {
        String sql = "SELECT * FROM staff WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error fetching staff member by ID: ", e);
        }
        return null;
    }

    public StaffDTO save(StaffDTO staff) {
        if (staff.getId() == null) {
            return insert(staff);
        } else {
            return update(staff);
        }
    }

    private StaffDTO insert(StaffDTO staff) {
        String sql = "INSERT INTO staff (full_name, role, phone, email, salary, photo_url, " +
                    "monday_schedule, tuesday_schedule, wednesday_schedule, thursday_schedule, " +
                    "friday_schedule, saturday_schedule, sunday_schedule) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStaffParameters(stmt, staff);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    staff.setId(rs.getLong("id"));
                    return staff;
                }
            }
        } catch (SQLException e) {
            log.error("Error inserting staff member: ", e);
        }
        return null;
    }

    private StaffDTO update(StaffDTO staff) {
        String sql = "UPDATE staff SET full_name = ?, role = ?, phone = ?, email = ?, " +
                    "salary = ?, photo_url = ?, monday_schedule = ?, tuesday_schedule = ?, " +
                    "wednesday_schedule = ?, thursday_schedule = ?, friday_schedule = ?, " +
                    "saturday_schedule = ?, sunday_schedule = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStaffParameters(stmt, staff);
            stmt.setLong(14, staff.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return staff;
            }
        } catch (SQLException e) {
            log.error("Error updating staff member: ", e);
        }
        return null;
    }

    public boolean hasActiveTrainings(Long id) {
        String sql = "SELECT COUNT(*) FROM trainings WHERE trainer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            log.error("Error checking for active trainings: ", e);
            return false;
        }
    }

    public boolean delete(Long id) throws SQLException {
        if (hasActiveTrainings(id)) {
            throw new SQLException("Cannot delete staff member because they have active trainings. Please reassign or delete the trainings first.");
        }

        String sql = "DELETE FROM staff WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private void setStaffParameters(PreparedStatement stmt, StaffDTO staff) throws SQLException {
        stmt.setString(1, staff.getFullName());
        stmt.setString(2, staff.getRole().getValue());
        stmt.setString(3, staff.getPhone());
        stmt.setString(4, staff.getEmail());
        stmt.setBigDecimal(5, staff.getSalary());
        stmt.setString(6, staff.getPhotoUrl());
        stmt.setString(7, staff.getMondaySchedule());
        stmt.setString(8, staff.getTuesdaySchedule());
        stmt.setString(9, staff.getWednesdaySchedule());
        stmt.setString(10, staff.getThursdaySchedule());
        stmt.setString(11, staff.getFridaySchedule());
        stmt.setString(12, staff.getSaturdaySchedule());
        stmt.setString(13, staff.getSundaySchedule());
    }

    private StaffDTO mapResultSetToStaff(ResultSet rs) throws SQLException {
        StaffDTO staff = new StaffDTO();
        staff.setId(rs.getLong("id"));
        staff.setFullName(rs.getString("full_name"));
        staff.setRole(StaffRole.fromValue(rs.getString("role")));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setSalary(rs.getBigDecimal("salary"));
        staff.setPhotoUrl(rs.getString("photo_url"));
        staff.setMondaySchedule(rs.getString("monday_schedule"));
        staff.setTuesdaySchedule(rs.getString("tuesday_schedule"));
        staff.setWednesdaySchedule(rs.getString("wednesday_schedule"));
        staff.setThursdaySchedule(rs.getString("thursday_schedule"));
        staff.setFridaySchedule(rs.getString("friday_schedule"));
        staff.setSaturdaySchedule(rs.getString("saturday_schedule"));
        staff.setSundaySchedule(rs.getString("sunday_schedule"));
        return staff;
    }
} 