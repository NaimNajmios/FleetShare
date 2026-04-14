/**
 * FleetShare Analytics Tracker
 * Lightweight event tracking utility
 * Can be extended to integrate with Google Analytics, Mixpanel, etc.
 */

(function() {
    'use strict';

    const Analytics = {
        track: function(eventName, params) {
            const eventData = {
                event: eventName,
                timestamp: new Date().toISOString(),
                url: window.location.href,
                params: params || {}
            };

            if (window.console && console.log) {
                console.log('[Analytics]', eventData);
            }

            if (typeof this.sendToServer === 'function') {
                this.sendToServer(eventData);
            }
        },

        sendToServer: function(eventData) {
            fetch('/api/analytics/track', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || ''
                },
                body: JSON.stringify(eventData)
            }).catch(function() {});
        },

        pageView: function(pageName) {
            this.track('page_view', { page: pageName });
        },

        reportGenerate: function(category, reportType, format) {
            this.track('report_generate', { category, reportType, format });
        },

        reportDownload: function(format, reportType) {
            this.track('report_download', { format, reportType });
        }
    };

    window.FleetShareAnalytics = Analytics;
})();