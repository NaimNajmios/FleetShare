package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Stores the price snapshot at the time of booking creation.
 * This preserves the rate even if the vehicle price changes later.
 */
@Entity
@Table(name = "bookingpricesnapshot")
public class BookingPriceSnapshot {

    @Id
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "rate_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerDay;

    @Column(name = "days_rented", nullable = false)
    private Integer daysRented;

    @Column(name = "total_calculated_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCalculatedCost;

    // Constructors
    public BookingPriceSnapshot() {
    }

    public BookingPriceSnapshot(Long bookingId, BigDecimal ratePerDay, Integer daysRented,
            BigDecimal totalCalculatedCost) {
        this.bookingId = bookingId;
        this.ratePerDay = ratePerDay;
        this.daysRented = daysRented;
        this.totalCalculatedCost = totalCalculatedCost;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public Integer getDaysRented() {
        return daysRented;
    }

    public void setDaysRented(Integer daysRented) {
        this.daysRented = daysRented;
    }

    public BigDecimal getTotalCalculatedCost() {
        return totalCalculatedCost;
    }

    public void setTotalCalculatedCost(BigDecimal totalCalculatedCost) {
        this.totalCalculatedCost = totalCalculatedCost;
    }
}
