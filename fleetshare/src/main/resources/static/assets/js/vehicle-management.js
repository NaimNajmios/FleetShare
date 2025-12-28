// Vehicle Management - Filtering
(function () {
    'use strict';

    // Vehicle Filtering
    const searchInput = document.getElementById('vehicle-search');
    const yearFilter = document.getElementById('year-filter');
    const categoryFilter = document.getElementById('category-filter');
    const vehicleRows = document.querySelectorAll('.vehicle-row');

    function filterVehicles() {
        if (!searchInput || !yearFilter || !categoryFilter) return;

        const searchTerm = searchInput.value.toLowerCase();
        const selectedYear = yearFilter.value;
        const selectedCategory = categoryFilter.value;

        vehicleRows.forEach(row => {
            const searchableText = Array.from(row.querySelectorAll('.searchable'))
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');

            const year = row.getAttribute('data-year');
            const category = row.getAttribute('data-category');

            const matchesSearch = searchableText.includes(searchTerm);
            const matchesYear = !selectedYear || year === selectedYear;
            const matchesCategory = !selectedCategory || category === selectedCategory;

            if (matchesSearch && matchesYear && matchesCategory) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        updateNoDataMessage('vehicles-table', vehicleRows);
    }

    if (searchInput) {
        searchInput.addEventListener('input', filterVehicles);
        yearFilter.addEventListener('change', filterVehicles);
        categoryFilter.addEventListener('change', filterVehicles);
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
                noDataDiv.className = 'text-center p-5 text-muted no-data no-data-filtered';
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
