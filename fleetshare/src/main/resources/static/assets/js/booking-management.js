// Booking Management - Filtering
(function () {
    'use strict';

    // Booking Filtering
    const searchInput = document.getElementById('booking-search');
    const statusFilter = document.getElementById('status-filter');
    const bookingRows = document.querySelectorAll('.booking-row');

    function filterBookings() {
        if (!searchInput || !statusFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const selectedStatus = statusFilter.value;

        bookingRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const status = row.getAttribute('data-status');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesStatus = !selectedStatus || status === selectedStatus;

            if (matchesSearch && matchesStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('bookings-table', bookingRows);
    }

    if (searchInput) {
        searchInput.addEventListener('input', filterBookings);
        statusFilter.addEventListener('change', filterBookings);
    }

    // Update "No data" message based on visible rows
    function updateNoDataMessage(tableId, rows) {
        const table = document.getElementById(tableId);
        if (!table) return;

        const visibleRows = Array.from(rows).filter(row => row.style.display !== 'none');

        let noDataDiv = table.parentElement.querySelector('.no-data-filtered');

        if (visibleRows.length === 0 && rows.length > 0) {
            if (!noDataDiv) {
                noDataDiv = document.createElement('div');
                noDataDiv.className = 'no-data no-data-filtered';
                noDataDiv.innerHTML = '<i class="mdi mdi-calendar-remove mdi-48px"></i><p>No bookings match your filters</p>';
                table.parentElement.appendChild(noDataDiv);
            }
            table.style.display = 'none';
        } else {
            if (noDataDiv) {
                noDataDiv.remove();
            }
            table.style.display = '';
        }
    }

})();
