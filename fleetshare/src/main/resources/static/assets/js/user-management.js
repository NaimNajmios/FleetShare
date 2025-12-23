// User Management - Tab Switching and Filtering
(function () {
    'use strict';

    // Tab Switching logic removed as it is now handled by Bootstrap 5
    // Fleet Owners Filtering
    const ownerSearchInput = document.getElementById('owner-search');
    const ownerStatusFilter = document.getElementById('owner-status-filter');
    const ownerRows = document.querySelectorAll('.owner-row');

    function filterOwners() {
        const searchTerm = ownerSearchInput.value.toLowerCase();
        const statusFilter = ownerStatusFilter.value;

        ownerRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const status = row.getAttribute('data-status');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesStatus = !statusFilter || status === statusFilter;

            if (matchesSearch && matchesStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('owners-table', ownerRows);
    }

    if (ownerSearchInput) {
        ownerSearchInput.addEventListener('input', filterOwners);
        ownerStatusFilter.addEventListener('change', filterOwners);
    }

    // Renters Filtering
    const renterSearchInput = document.getElementById('renter-search');
    const renterStatusFilter = document.getElementById('renter-status-filter');
    const renterRows = document.querySelectorAll('.renter-row');

    function filterRenters() {
        const searchTerm = renterSearchInput.value.toLowerCase();
        const statusFilter = renterStatusFilter.value;

        renterRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const status = row.getAttribute('data-status');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesStatus = !statusFilter || status === statusFilter;

            if (matchesSearch && matchesStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('renters-table', renterRows);
    }

    if (renterSearchInput) {
        renterSearchInput.addEventListener('input', filterRenters);
        renterStatusFilter.addEventListener('change', filterRenters);
    }

    // Update "No data" message based on visible rows
    function updateNoDataMessage(tableId, rows) {
        const table = document.getElementById(tableId);
        const visibleRows = Array.from(rows).filter(row => row.style.display !== 'none');

        let noDataDiv = table.parentElement.querySelector('.no-data-filtered');

        if (visibleRows.length === 0 && rows.length > 0) {
            if (!noDataDiv) {
                noDataDiv = document.createElement('div');
                noDataDiv.className = 'text-center p-5 text-muted no-data no-data-filtered';
                noDataDiv.innerHTML = '<i class="mdi mdi-filter-remove mdi-48px"></i><p>No results match your filters</p>';
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
