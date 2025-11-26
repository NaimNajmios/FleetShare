// Vehicle Profile - View/Edit Mode Toggle
(function () {
    'use strict';

    const container = document.querySelector('.profile-container');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const saveBtn = document.getElementById('saveBtn');
    const formControls = document.querySelectorAll('.form-control');
    const formSelects = document.querySelectorAll('.form-select');
    const manageRateBtn = document.querySelector('.manage-rate-btn');

    // Store original values
    const originalValues = {};
    const originalSelectValues = {};

    formControls.forEach((input, index) => {
        originalValues[index] = input.value;
    });

    formSelects.forEach((select, index) => {
        originalSelectValues[index] = select.value;
    });

    // Switch to Edit Mode
    if (editBtn) {
        editBtn.addEventListener('click', function () {
            container.id = 'editMode';

            // Enable inputs
            formControls.forEach(input => {
                if (!input.classList.contains('form-control-plaintext')) {
                    input.removeAttribute('readonly');
                }
            });

            // Enable selects
            formSelects.forEach(select => {
                select.removeAttribute('disabled');
            });
        });
    }

    // Cancel Edit Mode
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            container.id = 'viewMode';

            // Restore original input values
            formControls.forEach((input, index) => {
                input.value = originalValues[index];
                input.setAttribute('readonly', true);
            });

            // Restore original select values
            formSelects.forEach((select, index) => {
                select.value = originalSelectValues[index];
                select.setAttribute('disabled', true);
            });
        });
    }

    // Save Changes (UI only)
    if (saveBtn) {
        saveBtn.addEventListener('click', function () {
            // Show confirmation message
            alert('Update functionality is not yet implemented. This is UI only.');

            // Switch back to view mode for demo
            container.id = 'viewMode';

            formControls.forEach(input => {
                input.setAttribute('readonly', true);
                // Update stored values
                const index = Array.from(formControls).indexOf(input);
                originalValues[index] = input.value;
            });

            formSelects.forEach(select => {
                select.setAttribute('disabled', true);
                // Update stored values
                const index = Array.from(formSelects).indexOf(select);
                originalSelectValues[index] = select.value;
            });
        });
    }

    // Manage Rate Button
    if (manageRateBtn) {
        manageRateBtn.addEventListener('click', function (e) {
            // Only active in edit mode (though CSS handles pointer-events)
            if (container.id === 'editMode') {
                alert('Manage Rate modal will be implemented later.');
            }
        });
    }

})();
