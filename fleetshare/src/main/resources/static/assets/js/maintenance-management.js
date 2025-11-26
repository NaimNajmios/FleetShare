// Maintenance Management - Filtering
(function () {
    'use strict';

    // Maintenance Filtering
    const searchInput = document.getElementById('maintenance-search');
    const statusFilter = document.getElementById('status-filter');
    const maintenanceRows = document.querySelectorAll('.maintenance-row');

    function filterMaintenance() {
        if (!searchInput || !statusFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const selectedStatus = statusFilter.value;

        maintenanceRows.forEach(row => {
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

        updateNoDataMessage('maintenance-table', maintenanceRows);
    }

    if (searchInput) {
        searchInput.addEventListener('input', filterMaintenance);
        statusFilter.addEventListener('change', filterMaintenance);
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
                noDataDiv.innerHTML = '<i class="mdi mdi-tools mdi-48px"></i><p>No maintenance records match your filters</p>';
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
