(function() {
    'use strict';

    let debounceTimer = null;
    const DEBOUNCE_DELAY = 300;
    let currentRole = document.body.dataset.role || 'admin';

    function init() {
        const searchInput = document.getElementById('navbar-search-input');
        const searchIcon = document.getElementById('navbar-search-icon');
        
        if (!searchInput) return;

        searchInput.addEventListener('input', handleSearchInput);
        searchInput.addEventListener('focus', handleSearchFocus);
        searchInput.addEventListener('keydown', handleSearchKeydown);

        if (searchIcon) {
            searchIcon.addEventListener('click', () => {
                searchInput.focus();
            });
        }

        document.addEventListener('click', handleDocumentClick);
    }

    function handleSearchInput(e) {
        const query = e.target.value.trim();

        if (debounceTimer) {
            clearTimeout(debounceTimer);
        }

        if (query.length < 2) {
            hideDropdown();
            return;
        }

        debounceTimer = setTimeout(() => {
            performSearch(query);
        }, DEBOUNCE_DELAY);
    }

    function handleSearchFocus(e) {
        const query = e.target.value.trim();
        if (query.length >= 2) {
            showDropdown();
        }
    }

    function handleSearchKeydown(e) {
        if (e.key === 'Escape') {
            hideDropdown();
            e.target.blur();
        } else if (e.key === 'Enter') {
            const query = e.target.value.trim();
            if (query.length >= 2) {
                performSearch(query);
            }
        }
    }

    function handleDocumentClick(e) {
        const searchContainer = document.querySelector('.nav-search');
        const dropdown = document.getElementById('searchResultsDropdown');
        
        if (searchContainer && !searchContainer.contains(e.target) && dropdown) {
            hideDropdown();
        }
    }

    async function performSearch(query) {
        showLoading();

        try {
            const response = await fetch(`/api/search?q=${encodeURIComponent(query)}&role=${currentRole}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Search failed');
            }

            const data = await response.json();
            renderResults(data.results, data.totalResults, query);
        } catch (error) {
            console.error('Search error:', error);
            showError();
        }
    }

    function showLoading() {
        showDropdown();
        const loading = document.getElementById('searchLoading');
        const list = document.getElementById('searchResultsList');
        const noResults = document.getElementById('searchNoResults');
        
        if (loading) loading.style.display = 'block';
        if (list) list.innerHTML = '';
        if (noResults) noResults.style.display = 'none';
    }

    function showError() {
        const list = document.getElementById('searchResultsList');
        if (list) {
            list.innerHTML = `
                <div class="search-error text-center py-3 text-danger">
                    <p class="mb-0"><i class="ti-alert"></i> Error performing search</p>
                </div>
            `;
        }
        showDropdown();
    }

    function renderResults(results, total, query) {
        const loading = document.getElementById('searchLoading');
        const list = document.getElementById('searchResultsList');
        const noResults = document.getElementById('searchNoResults');

        if (loading) loading.style.display = 'none';

        if (!results || total === 0) {
            if (noResults) noResults.style.display = 'block';
            if (list) list.innerHTML = '';
            return;
        }

        if (noResults) noResults.style.display = 'none';

        let html = '';
        const baseUrl = currentRole === 'owner' ? '/owner' : '/admin';

        html += `<div class="search-header px-3 py-2 border-bottom">
            <small class="text-muted">${total} result${total !== 1 ? 's' : ''} for "${escapeHtml(query)}"</small>
        </div>`;

        if (results.vehicles && results.vehicles.length > 0) {
            html += renderSection('Vehicles', 'vehicles', results.vehicles, 'vehicle', baseUrl + '/vehicles');
        }

        if (results.bookings && results.bookings.length > 0) {
            html += renderSection('Bookings', 'bookings', results.bookings, 'booking', baseUrl + '/bookings');
        }

        if (results.users && results.users.length > 0) {
            html += renderSection('Users', 'users', results.users, 'user', baseUrl + '/users');
        }

        if (results.renters && results.renters.length > 0) {
            html += renderSection('Renters', 'renters', results.renters, 'renter', baseUrl + '/customers');
        }

        if (results.owners && results.owners.length > 0) {
            html += renderSection('Fleet Owners', 'owners', results.owners, 'owner', baseUrl + '/users');
        }

        if (list) {
            list.innerHTML = html;
            attachResultListeners();
        }

        showDropdown();
    }

    function renderSection(title, type, items, itemType, baseUrl) {
        const icons = {
            vehicles: 'icon-car',
            bookings: 'icon-book',
            users: 'icon-user',
            renters: 'icon-user',
            owners: 'icon-briefcase'
        };

        let itemsHtml = items.map(item => {
            let label, sublabel, url;
            
            switch(itemType) {
                case 'vehicle':
                    label = `${item.brand} ${item.model}`;
                    sublabel = item.registrationNo;
                    url = `${baseUrl}`;
                    break;
                case 'booking':
                    label = `Booking #${item.bookingId}`;
                    sublabel = `Vehicle ID: ${item.vehicleId}`;
                    url = `${baseUrl}`;
                    break;
                case 'user':
                    label = item.email;
                    sublabel = item.role;
                    url = `${baseUrl}`;
                    break;
                case 'renter':
                    label = item.fullName;
                    sublabel = item.phoneNumber || 'No phone';
                    url = `${baseUrl}`;
                    break;
                case 'owner':
                    label = item.businessName;
                    sublabel = item.contactPhone || 'No phone';
                    url = `${baseUrl}`;
                    break;
            }

            return `
                <a href="${url}" class="search-result-item dropdown-item" data-type="${itemType}">
                    <div class="d-flex align-items-center">
                        <div class="search-result-icon me-2">
                            <i class="${icons[type]}"></i>
                        </div>
                        <div class="search-result-content">
                            <div class="search-result-title">${escapeHtml(label)}</div>
                            <div class="search-result-subtitle text-muted small">${escapeHtml(sublabel || '')}</div>
                        </div>
                    </div>
                </a>
            `;
        }).join('');

        return `
            <div class="search-section">
                <div class="search-section-header px-3 py-1">
                    <small class="text-muted fw-semibold text-uppercase">
                        <i class="${icons[type]} me-1"></i>${title}
                    </small>
                </div>
                ${itemsHtml}
            </div>
        `;
    }

    function attachResultListeners() {
        const items = document.querySelectorAll('.search-result-item');
        items.forEach(item => {
            item.addEventListener('click', () => {
                hideDropdown();
                const input = document.getElementById('navbar-search-input');
                if (input) {
                    input.value = '';
                }
            });
        });
    }

    function showDropdown() {
        const dropdown = document.getElementById('searchResultsDropdown');
        if (dropdown) {
            dropdown.style.display = 'block';
        }
    }

    function hideDropdown() {
        const dropdown = document.getElementById('searchResultsDropdown');
        if (dropdown) {
            dropdown.style.display = 'none';
        }
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
