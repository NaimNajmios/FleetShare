// Payment Management - Filtering
(function () {
    'use strict';

    // Payment Filtering
    const searchInput = document.getElementById('payment-search');
    const statusFilter = document.getElementById('status-filter');
    const methodFilter = document.getElementById('method-filter');
    const paymentRows = document.querySelectorAll('.payment-row');

    function filterPayments() {
        if (!searchInput || !statusFilter || !methodFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const selectedStatus = statusFilter.value;
        const selectedMethod = methodFilter.value;

        paymentRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const status = row.getAttribute('data-status');
            const method = row.getAttribute('data-method');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesStatus = !selectedStatus || status === selectedStatus;
            const matchesMethod = !selectedMethod || method === selectedMethod;

            if (matchesSearch && matchesStatus && matchesMethod) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('payments-table', paymentRows);
    }

    if (searchInput) {
        searchInput.addEventListener('input', filterPayments);
        statusFilter.addEventListener('change', filterPayments);
        methodFilter.addEventListener('change', filterPayments);
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
                noDataDiv.innerHTML = '<i class="mdi mdi-cash-multiple mdi-48px"></i><p>No payments match your filters</p>';
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
