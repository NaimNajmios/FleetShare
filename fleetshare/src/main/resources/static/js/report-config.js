/**
 * Shared Report Configuration JavaScript
 * Used by both Admin and Owner report pages
 */

(function() {
    'use strict';

    let currentCategory = 'booking';
    let currentReport = 'monthly-revenue';
    let previewModal;
    let reportChartInstance = null;
    let isComparisonMode = false;
    let comparisonData = null;

    const CHART_CONFIGS = {
        'monthly-revenue': {
            type: 'bar',
            labelCol: 'Month',
            datasets: [
                { col: 'Revenue', label: 'Revenue (RM)', type: 'bar', color: 'rgba(102, 126, 234, 0.7)', borderColor: '#667eea', parse: v => parseFloat(String(v).replace(/[^\d.-]/g, '')) || 0 },
                { col: 'Bookings', label: 'Bookings', type: 'line', color: 'rgba(118, 75, 162, 0.8)', borderColor: '#764ba2', yAxisID: 'y1', parse: v => parseInt(v) || 0 }
            ],
            dualAxis: true
        },
        'utilization-rate': {
            type: 'bar',
            indexAxis: 'y',
            labelCol: 'Vehicle',
            datasets: [
                { col: 'Utilization', label: 'Utilization (%)', color: 'rgba(17, 153, 142, 0.7)', borderColor: '#11998e', parse: v => parseFloat(String(v).replace('%', '')) || 0 }
            ]
        },
        'vehicle-performance': {
            type: 'bar',
            labelCol: 'Vehicle',
            datasets: [
                { col: 'Revenue', label: 'Revenue (RM)', color: 'rgba(102, 126, 234, 0.7)', borderColor: '#667eea', parse: v => parseFloat(String(v).replace(/[^\d.-]/g, '')) || 0 }
            ]
        },
        'revenue-analysis': {
            type: 'doughnut',
            labelCol: 'Payment Method',
            datasets: [
                { col: 'Revenue', label: 'Revenue (RM)', parse: v => parseFloat(String(v).replace(/[^\d.-]/g, '')) || 0 }
            ],
            colors: ['#667eea', '#764ba2', '#11998e', '#38ef7d', '#fa709a', '#fee140', '#4facfe', '#f093fb']
        },
        'cost-analysis': {
            type: 'bar',
            labelCol: 'Vehicle',
            datasets: [
                { col: 'Total Cost', label: 'Total Cost (RM)', color: 'rgba(79, 172, 254, 0.7)', borderColor: '#4facfe', parse: v => parseFloat(String(v).replace(/[^\d.-]/g, '')) || 0 }
            ]
        },
        'top-customers': {
            type: 'bar',
            indexAxis: 'y',
            labelCol: 'Customer',
            datasets: [
                { col: 'Revenue', label: 'Revenue (RM)', color: 'rgba(240, 147, 251, 0.7)', borderColor: '#f093fb', parse: v => parseFloat(String(v).replace(/[^\d.-]/g, '')) || 0 }
            ]
        }
    };

    const REPORT_TEMPLATES = {
        booking: [
            { id: 'monthly-revenue', name: 'Monthly Revenue', desc: 'Revenue breakdown by month' },
            { id: 'utilization-rate', name: 'Utilization Rate', desc: 'Fleet utilization analysis' },
            { id: 'booking-summary', name: 'Booking Summary', desc: 'All bookings overview' }
        ],
        vehicle: [
            { id: 'vehicle-performance', name: 'Vehicle Performance', desc: 'Performance metrics per vehicle' },
            { id: 'fleet-status', name: 'Fleet Status', desc: 'Current fleet status overview' },
            { id: 'maintenance-due', name: 'Maintenance Due', desc: 'Upcoming maintenance schedule' }
        ],
        payment: [
            { id: 'payment-summary', name: 'Payment Summary', desc: 'Payment transactions summary' },
            { id: 'outstanding-payments', name: 'Outstanding Payments', desc: 'Pending payment list' },
            { id: 'revenue-analysis', name: 'Revenue Analysis', desc: 'Detailed revenue breakdown' }
        ],
        maintenance: [
            { id: 'maintenance-history', name: 'Maintenance History', desc: 'Past maintenance records' },
            { id: 'cost-analysis', name: 'Cost Analysis', desc: 'Maintenance cost breakdown' },
            { id: 'upcoming-maintenance', name: 'Upcoming Maintenance', desc: 'Scheduled maintenance' }
        ],
        user: [
            { id: 'user-activity', name: 'User Activity', desc: 'Customer booking activity' },
            { id: 'top-customers', name: 'Top Customers', desc: 'Most active customers' },
            { id: 'user-demographics', name: 'User Demographics', desc: 'Customer demographics' }
        ]
    };

    window.ReportBuilder = {
        init: function(apiEndpoint) {
            this.apiEndpoint = apiEndpoint;
            this.bindEvents();
            previewModal = new bootstrap.Modal(document.getElementById('previewModal'));
        },

        bindEvents: function() {
            const self = this;

            document.querySelectorAll('.report-category-card').forEach(card => {
                card.addEventListener('click', function() {
                    self.selectCategory(this.dataset.category, this);
                });

                card.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        self.selectCategory(this.dataset.category, this);
                    }
                });
            });

            const typeContainer = document.getElementById('report-types-container');
            if (typeContainer) {
                typeContainer.addEventListener('click', function(e) {
                    const card = e.target.closest('.report-type-card');
                    if (card) self.selectReport(card.dataset.report, card);
                });

                typeContainer.addEventListener('keydown', function(e) {
                    const card = e.target.closest('.report-type-card');
                    if (card) {
                        if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault();
                            self.selectReport(card.dataset.report, card);
                        } else {
                            self.handleCardNavigation(e, card);
                        }
                    }
                });
            }

            document.querySelectorAll('.duration-pill').forEach(pill => {
                pill.addEventListener('click', function() {
                    document.querySelectorAll('.duration-pill').forEach(p => {
                        if (!p.classList.contains('comparison-toggle')) {
                            p.classList.remove('active');
                        }
                    });
                    const input = this.querySelector('input');
                    if (input && input.type === 'checkbox') {
                        if (!this.classList.contains('active')) {
                            this.classList.add('active');
                        } else {
                            this.classList.remove('active');
                        }
                        self.toggleComparisonMode(this.classList.contains('active'));
                    } else if (input) {
                        this.classList.add('active');
                    }
                    document.querySelector('.date-range').style.display =
                        (input && input.value === 'custom') ? 'flex' : 'none';
                });
            });

            const previewBtn = document.getElementById('btn-preview');
            if (previewBtn) previewBtn.addEventListener('click', () => self.previewReport());

            const pdfBtn = document.getElementById('btn-pdf');
            if (pdfBtn) pdfBtn.addEventListener('click', () => self.downloadReport('pdf'));

            const csvBtn = document.getElementById('btn-csv');
            if (csvBtn) csvBtn.addEventListener('click', () => self.downloadReport('csv'));

            const excelBtn = document.getElementById('btn-excel');
            if (excelBtn) excelBtn.addEventListener('click', () => self.downloadReport('excel'));

            const modalPdfBtn = document.getElementById('modal-btn-pdf');
            if (modalPdfBtn) modalPdfBtn.addEventListener('click', () => self.downloadReport('pdf'));

            const modalCsvBtn = document.getElementById('modal-btn-csv');
            if (modalCsvBtn) modalCsvBtn.addEventListener('click', () => self.downloadReport('csv'));

            const modalExcelBtn = document.getElementById('modal-btn-excel');
            if (modalExcelBtn) modalExcelBtn.addEventListener('click', () => self.downloadReport('excel'));

            const comparisonToggle = document.getElementById('enable-comparison');
            if (comparisonToggle) {
                comparisonToggle.addEventListener('change', function() {
                    self.toggleComparisonMode(this.checked);
                });
            }
        },

        handleCardNavigation: function(e, currentCard) {
            const cards = [...document.querySelectorAll('.report-type-card')];
            const idx = cards.indexOf(currentCard);
            const cols = 1;

            if (e.key === 'ArrowDown' || e.key === 'ArrowRight') {
                e.preventDefault();
                const nextIdx = Math.min(idx + cols, cards.length - 1);
                cards[nextIdx].focus();
            } else if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') {
                e.preventDefault();
                const prevIdx = Math.max(idx - cols, 0);
                cards[prevIdx].focus();
            }
        },

        selectCategory: function(category, element) {
            currentCategory = category;
            document.querySelectorAll('.report-category-card').forEach(card => {
                card.classList.remove('active');
                card.setAttribute('aria-selected', 'false');
            });
            element.classList.add('active');
            element.setAttribute('aria-selected', 'true');

            const container = document.getElementById('report-types-container');
            const templates = REPORT_TEMPLATES[category];

            container.innerHTML = templates.map((t, i) => `
                <div class="report-type-card ${i === 0 ? 'selected' : ''}" 
                     data-report="${t.id}"
                     role="option"
                     tabindex="${i === 0 ? '0' : '-1'}"
                     aria-selected="${i === 0 ? 'true' : 'false'}">
                    <div class="report-name">${t.name}</div>
                    <p class="report-desc">${t.desc}</p>
                </div>
            `).join('');

            currentReport = templates[0].id;
        },

        selectReport: function(reportId, element) {
            currentReport = reportId;
            document.querySelectorAll('.report-type-card').forEach((card, idx) => {
                const isSelected = card.dataset.report === reportId;
                card.classList.toggle('selected', isSelected);
                card.setAttribute('aria-selected', isSelected ? 'true' : 'false');
                card.setAttribute('tabindex', isSelected ? '0' : '-1');
            });
        },

        toggleComparisonMode: function(enabled) {
            isComparisonMode = enabled;
            const comparisonSection = document.getElementById('comparison-section');
            const comparisonFilters = document.getElementById('comparison-filters');

            if (comparisonSection) {
                comparisonSection.classList.toggle('active', enabled);
            }
            if (comparisonFilters) {
                comparisonFilters.style.display = enabled ? 'flex' : 'none';
            }
        },

        getCsrfToken: function() {
            return document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        },

        getCsrfHeader: function() {
            return document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
        },

        getReportParams: function() {
            const duration = document.querySelector('.duration-pill.active input')?.value || 
                           document.querySelector('input[name="duration"]:checked')?.value;
            const vehicleFilter = document.getElementById('vehicle-filter')?.value;
            const ownerFilter = document.getElementById('owner-filter')?.value;

            const params = {
                category: currentCategory,
                reportType: currentReport,
                duration: duration,
                startDate: document.getElementById('start-date')?.value || null,
                endDate: document.getElementById('end-date')?.value || null,
                status: document.getElementById('status-filter')?.value || null,
                vehicleId: vehicleFilter ? parseInt(vehicleFilter) : null,
                ownerId: ownerFilter ? parseInt(ownerFilter) : null,
                comparisonMode: isComparisonMode
            };

            return params;
        },

        validateDateRange: function() {
            const startDate = document.getElementById('start-date')?.value;
            const endDate = document.getElementById('end-date')?.value;

            if (startDate && endDate) {
                const start = new Date(startDate);
                const end = new Date(endDate);
                if (start > end) {
                    if (typeof showToastWithRetry === 'function') {
                        showToastWithRetry('Start date must be before end date', 'error', 'Invalid Date Range');
                    } else {
                        alert('Start date must be before end date');
                    }
                    return false;
                }
            }
            return true;
        },

        previewReport: function() {
            if (!this.validateDateRange()) return;

            const self = this;
            if (window.FleetShareAnalytics) {
                FleetShareAnalytics.reportGenerate(currentCategory, currentReport, 'preview');
            }
            document.getElementById('loading-indicator').style.display = 'block';
            document.getElementById('preview-content').style.display = 'none';
            document.getElementById('chart-container').style.display = 'none';
            document.getElementById('preview-table-content').style.display = 'none';
            document.getElementById('comparison-table-content').style.display = 'none';
            previewModal.show();

            const csrfToken = this.getCsrfToken();
            const csrfHeader = this.getCsrfHeader();

            const headers = { 'Content-Type': 'application/json' };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            fetch(this.apiEndpoint + '/generate', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(this.getReportParams())
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('loading-indicator').style.display = 'none';
                document.getElementById('preview-content').style.display = 'block';

                if (data.error) {
                    document.getElementById('preview-content').innerHTML = `
                        <div class="alert alert-danger">
                            <i class="mdi mdi-alert-circle me-2"></i>${data.error}
                        </div>
                    `;
                } else {
                    self.displayPreview(data);
                }
            })
            .catch(e => {
                document.getElementById('loading-indicator').style.display = 'none';
                document.getElementById('preview-content').style.display = 'block';
                document.getElementById('preview-content').innerHTML = `
                    <div class="alert alert-danger">
                        <i class="mdi mdi-alert-circle me-2"></i>Failed to generate report: ${e.message}
                    </div>
                `;
            });
        },

        displayPreview: function(data) {
            const section = document.getElementById('preview-content');
            const tableSection = document.getElementById('preview-table-content');
            const comparisonSection = document.getElementById('comparison-table-content');

            document.getElementById('previewModalLabel').innerHTML = `
                <i class="mdi mdi-table me-2"></i>${data.reportTitle}
            `;

            const summaryHtml = data.summary ?
                `<div class="summary-badges">${Object.entries(data.summary).map(([k, v]) => {
                    const trend = v.trend ? `<span class="trend-indicator ${v.trend > 0 ? 'trend-up' : 'trend-down'}">${v.trend > 0 ? '↑' : '↓'} ${Math.abs(v.trend).toFixed(1)}%</span>` : '';
                    const displayValue = typeof v === 'object' ? v.value : v;
                    return `<span class="badge bg-primary">${k}: ${displayValue}${trend}</span>`;
                }).join('')}</div>` : '';

            const noData = !data.data || data.data.length === 0;

            section.innerHTML = `
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-muted"><i class="mdi mdi-calendar me-1"></i>${data.period}</span>
                    <span class="badge bg-secondary">${data.data?.length || 0} records</span>
                </div>
                ${summaryHtml}
            `;

            if (data.comparisonData) {
                comparisonData = data.comparisonData;
                this.renderComparisonTable(data, comparisonSection);
                comparisonSection.style.display = 'block';
                tableSection.style.display = 'none';
            } else {
                this.renderChart(data);
                this.renderTable(data, tableSection);
                tableSection.style.display = 'block';
                comparisonSection.style.display = 'none';
            }
        },

        renderChart: function(data) {
            const config = CHART_CONFIGS[currentReport];
            const chartContainer = document.getElementById('chart-container');
            const canvas = document.getElementById('reportChart');

            if (reportChartInstance) {
                reportChartInstance.destroy();
                reportChartInstance = null;
            }

            if (!config || !data.data || data.data.length === 0) {
                chartContainer.style.display = 'none';
                return;
            }

            chartContainer.style.display = 'block';
            const labels = data.data.map(row => row[config.labelCol] || '-');

            let datasets;
            if (config.type === 'doughnut') {
                const values = data.data.map(row => config.datasets[0].parse(row[config.datasets[0].col]));
                datasets = [{
                    data: values,
                    backgroundColor: config.colors.slice(0, values.length),
                    borderWidth: 2,
                    borderColor: '#fff'
                }];
            } else {
                datasets = config.datasets.map(ds => ({
                    label: ds.label,
                    data: data.data.map(row => ds.parse(row[ds.col])),
                    backgroundColor: ds.color,
                    borderColor: ds.borderColor,
                    borderWidth: ds.type === 'line' ? 2 : 1,
                    type: ds.type || config.type,
                    yAxisID: ds.yAxisID || 'y',
                    tension: 0.3,
                    fill: false,
                    pointRadius: ds.type === 'line' ? 4 : undefined
                }));
            }

            const options = {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: config.indexAxis || 'x',
                plugins: {
                    legend: { display: config.datasets.length > 1 || config.type === 'doughnut' },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                if (config.type === 'doughnut') {
                                    return `${labels[ctx.dataIndex]}: RM ${ctx.parsed.toFixed(2)}`;
                                }
                                return `${ctx.dataset.label}: ${ctx.parsed.y ?? ctx.parsed.x}`;
                            }
                        }
                    }
                }
            };

            if (config.type !== 'doughnut') {
                options.scales = {
                    y: { beginAtZero: true, position: 'left' }
                };
                if (config.dualAxis) {
                    options.scales.y1 = { beginAtZero: true, position: 'right', grid: { drawOnChartArea: false } };
                }
            }

            reportChartInstance = new Chart(canvas.getContext('2d'), {
                type: config.type === 'doughnut' ? 'doughnut' : 'bar',
                data: { labels, datasets },
                options
            });
        },

        renderTable: function(data, tableSection) {
            const noData = !data.data || data.data.length === 0;
            tableSection.innerHTML = noData ? 
                '<div class="alert alert-info"><i class="mdi mdi-information me-2"></i>No data found for the selected criteria.</div>' : 
                `<div class="table-responsive">
                    <table class="table table-sm table-hover table-striped">
                        <thead class="table-dark">
                            <tr>${data.columns.map(c => `<th>${c}</th>`).join('')}</tr>
                        </thead>
                        <tbody>
                            ${data.data.slice(0, 50).map(row => `<tr>${data.columns.map(c => `<td>${row[c] ?? '-'}</td>`).join('')}</tr>`).join('')}
                        </tbody>
                    </table>
                    ${data.data.length > 50 ? `<p class="text-muted small text-center"><i class="mdi mdi-information me-1"></i>Showing 50 of ${data.data.length} rows. Download for complete data.</p>` : ''}
                </div>`;
        },

        renderComparisonTable: function(data, tableSection) {
            const compData = data.comparisonData;
            const columns = data.columns;
            
            let tableHtml = `
                <div class="table-responsive">
                    <table class="table table-sm table-hover table-striped comparison-table">
                        <thead class="table-dark">
                            <tr>
                                <th>Metric</th>
                                <th class="period-a">Period A (Current)</th>
                                <th class="period-b">Period B (Previous)</th>
                                <th class="change-col">Change</th>
                            </tr>
                        </thead>
                        <tbody>`;

            if (compData && compData.rows) {
                compData.rows.forEach(row => {
                    const change = row.change;
                    const changeClass = change > 0 ? 'trend-up' : (change < 0 ? 'trend-down' : '');
                    const changeSymbol = change > 0 ? '↑' : (change < 0 ? '↓' : '→');
                    const changeText = row.isPercentage ? `${changeSymbol} ${Math.abs(change).toFixed(1)}%` : `${changeSymbol} ${Math.abs(change).toFixed(2)}`;

                    tableHtml += `<tr>
                        <td><strong>${row.label}</strong></td>
                        <td>${row.valueA}</td>
                        <td>${row.valueB}</td>
                        <td class="${changeClass}">${changeText}</td>
                    </tr>`;
                });
            }

            tableHtml += `</tbody></table></div>`;
            tableSection.innerHTML = tableHtml;
        },

        downloadReport: function(format) {
            if (!this.validateDateRange()) return;

            if (window.FleetShareAnalytics) {
                FleetShareAnalytics.reportDownload(format, currentReport);
            }

            const params = this.getReportParams();
            const queryString = new URLSearchParams({
                category: params.category,
                reportType: params.reportType,
                duration: params.duration,
                startDate: params.startDate || '',
                endDate: params.endDate || '',
                status: params.status || '',
                vehicleId: params.vehicleId || '',
                ownerId: params.ownerId || '',
                format: format,
                comparisonMode: params.comparisonMode
            }).toString();

            if (typeof showToast === 'function') {
                showToast(`Preparing ${format.toUpperCase()} download...`, 'info', 'Generating Report');
            }

            window.location.href = this.apiEndpoint + '/download?' + queryString;
        }
    };
})();
