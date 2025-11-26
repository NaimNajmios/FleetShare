// Vehicle Management - Tab Switching and Filtering
(function () {
    'use strict';

    // Tab Switching
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', function () {
            const tabId = this.getAttribute('data-tab');

            // Remove active class from all tabs and buttons
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Add active class to clicked button and corresponding content
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Vehicle Filtering
    const searchInput = document.getElementById('all-search');
    const yearFilter = document.getElementById('all-year-filter');
    const vehicleRows = document.querySelectorAll('.vehicle-row');

    function filterVehicles() {
        if (!searchInput || !yearFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const selectedYear = yearFilter.value;

        vehicleRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const year = row.getAttribute('data-year');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesYear = !selectedYear || year === selectedYear;

            if (matchesSearch && matchesYear) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('all-vehicles-table', vehicleRows);
    }

    if (searchInput) {
        searchInput.addEventListener('input', filterVehicles);
        yearFilter.addEventListener('change', filterVehicles);
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
                noDataDiv.innerHTML = '<i class="mdi mdi-car-off mdi-48px"></i><p>No vehicles match your filters</p>';
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
