package com.najmi.fleetshare.dto;

public class BookingCountDTO {
    private long total;
    private long active;
    private long completed;
    private long pending;

    public BookingCountDTO(long total, long active, long completed, long pending) {
        this.total = total;
        this.active = active;
        this.completed = completed;
        this.pending = pending;
    }

    public long getTotal() { return total; }
    public long getActive() { return active; }
    public long getCompleted() { return completed; }
    public long getPending() { return pending; }
}
