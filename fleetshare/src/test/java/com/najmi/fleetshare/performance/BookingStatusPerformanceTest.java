package com.najmi.fleetshare.performance;

import com.najmi.fleetshare.entity.BookingStatusLog;
import com.najmi.fleetshare.repository.BookingStatusLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
public class BookingStatusPerformanceTest {

    @Autowired
    private BookingStatusLogRepository bookingStatusLogRepository;

    @Test
    public void testFindLatestStatusForBookingsPerformance() {
        int bookingCount = 1000;
        int logsPerBooking = 5;
        List<Long> bookingIds = new ArrayList<>();
        List<BookingStatusLog> logsToSave = new ArrayList<>();

        // Generate data
        for (long i = 1; i <= bookingCount; i++) {
            bookingIds.add(i);
            for (int j = 0; j < logsPerBooking; j++) {
                BookingStatusLog log = new BookingStatusLog();
                log.setBookingId(i);

                // Set status based on order. Last one should be the "latest"
                if (j == logsPerBooking - 1) {
                    log.setStatusValue(BookingStatusLog.BookingStatus.COMPLETED); // Latest
                } else {
                    log.setStatusValue(BookingStatusLog.BookingStatus.PENDING);
                }

                log.setStatusTimestamp(LocalDateTime.now().minusDays(logsPerBooking - j));
                log.setRemarks("Log " + j);
                logsToSave.add(log);
            }
        }

        // Save all logs
        bookingStatusLogRepository.saveAll(logsToSave);
        bookingStatusLogRepository.flush();

        // Measure performance
        long startTime = System.nanoTime();
        List<BookingStatusLog> results = bookingStatusLogRepository.findLatestStatusForBookings(bookingIds);
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.println("--------------------------------------------------");
        System.out.println("Execution Time: " + durationMs + " ms");
        System.out.println("Fetched " + results.size() + " records");
        System.out.println("--------------------------------------------------");

        // Assertions
        assertThat(results).hasSize(bookingCount);

        // Check a sample
        BookingStatusLog sample = results.stream().filter(l -> l.getBookingId().equals(1L)).findFirst().orElseThrow();
        assertThat(sample.getStatusValue()).isEqualTo(BookingStatusLog.BookingStatus.COMPLETED);
    }
}
