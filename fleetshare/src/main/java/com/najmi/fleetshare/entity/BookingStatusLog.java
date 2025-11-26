package com.najmi.fleetshare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookingstatuslog")
public class BookingStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_log_id")
    private Long bookingLogId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_value", nullable = false)
    private BookingStatus statusValue;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "status_timestamp", nullable = false)
    private LocalDateTime statusTimestamp;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Enum for booking status
    public enum BookingStatus {
        PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED, DISPUTED
    }

    // Constructors
    public BookingStatusLog() {
    }

    // Getters and Setters
    public Long getBookingLogId() {
        return bookingLogId;
    }

    public void setBookingLogId(Long bookingLogId) {
        this.bookingLogId = bookingLogId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BookingStatus getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(BookingStatus statusValue) {
        this.statusValue = statusValue;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public LocalDateTime getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(LocalDateTime statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
