// User Profile - View/Edit Mode Toggle
(function () {
    'use strict';

    const container = document.querySelector('.profile-container');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const saveBtn = document.getElementById('saveBtn');
    const formControls = document.querySelectorAll('.form-control');

    // Store original values
    const originalValues = {};
    formControls.forEach((input, index) => {
        originalValues[index] = input.value;
    });

    // Switch to Edit Mode
    if (editBtn) {
        editBtn.addEventListener('click', function () {
            container.id = 'editMode';
            // Remove readonly attribute from editable fields (not plaintext fields)
            formControls.forEach(input => {
                if (!input.classList.contains('form-control-plaintext')) {
                    input.removeAttribute('readonly');
                }
            });
        });
    }

    // Cancel Edit Mode
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            container.id = 'viewMode';
            // Restore original values
            formControls.forEach((input, index) => {
                input.value = originalValues[index];
                input.setAttribute('readonly', true);
            });
        });
    }

    // Save Changes (UI only - no actual save functionality)
    if (saveBtn) {
        saveBtn.addEventListener('click', function () {
            // Show confirmation message
            alert('Save functionality is not yet implemented. This is UI only.');

            // For UI demo purposes, just switch back to view mode
            // In production, this would make an API call to save data
            container.id = 'viewMode';
            formControls.forEach(input => {
                input.setAttribute('readonly', true);
                // Update stored values
                const index = Array.from(formControls).indexOf(input);
                originalValues[index] = input.value;
            });
        });
    }

})();
