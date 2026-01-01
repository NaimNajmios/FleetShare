// User Profile - View/Edit Mode Toggle
(function () {
    'use strict';

    const container = document.querySelector('.profile-container');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const saveBtn = document.getElementById('saveBtn');
    const formControls = document.querySelectorAll('.form-control');
    const selectControls = document.querySelectorAll('select.form-control');

    // Store original values
    const originalValues = {};
    formControls.forEach((input, index) => {
        originalValues[index] = input.value;
    });
    selectControls.forEach((select, index) => {
        originalValues['select_' + index] = select.value;
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
            // Enable select elements
            selectControls.forEach(select => {
                select.removeAttribute('disabled');
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
            selectControls.forEach((select, index) => {
                select.value = originalValues['select_' + index];
                select.setAttribute('disabled', true);
            });
        });
    }

    // Save Changes - Make API call
    if (saveBtn) {
        saveBtn.addEventListener('click', async function () {
            const userId = document.getElementById('userId')?.value;
            const userType = document.getElementById('userType')?.value;

            if (!userId || !userType) {
                showNotification('Error: Missing user information', 'error');
                return;
            }

            // Collect form data
            const userData = {
                email: document.getElementById('email')?.value || null,
                phoneNumber: document.getElementById('phoneNumber')?.value || null,
                fullName: document.getElementById('fullName')?.value || null,
                businessName: document.getElementById('fullName')?.value || null, // For owners
                isVerified: document.getElementById('isVerified')?.value === 'true',
                addressLine1: document.getElementById('addressLine1')?.value || null,
                addressLine2: document.getElementById('addressLine2')?.value || null,
                city: document.getElementById('city')?.value || null,
                state: document.getElementById('state')?.value || null,
                postalCode: document.getElementById('postalCode')?.value || null
            };

            try {
                saveBtn.disabled = true;
                saveBtn.innerHTML = '<i class="mdi mdi-loading mdi-spin"></i> Saving...';

                // CSRF Protection
                const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                const response = await fetch(`/admin/users/update/${userId}?type=${userType}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify(userData)
                });

                const result = await response.json();

                if (response.ok && result.success) {
                    showNotification(result.message || 'User updated successfully!', 'success');

                    // Switch back to view mode
                    container.id = 'viewMode';
                    formControls.forEach((input, index) => {
                        input.setAttribute('readonly', true);
                        originalValues[index] = input.value;
                    });
                    selectControls.forEach((select, index) => {
                        select.setAttribute('disabled', true);
                        originalValues['select_' + index] = select.value;
                    });
                } else {
                    showNotification(result.error || 'Failed to update user', 'error');
                }
            } catch (error) {
                console.error('Error updating user:', error);
                showNotification('An error occurred while updating', 'error');
            } finally {
                saveBtn.disabled = false;
                saveBtn.innerHTML = 'Save Changes';
            }
        });
    }

    // Notification helper function
    function showNotification(message, type) {
        // Check if toastr is available (common notification library)
        if (typeof toastr !== 'undefined') {
            if (type === 'success') {
                toastr.success(message);
            } else if (type === 'error') {
                toastr.error(message);
            } else {
                toastr.info(message);
            }
        } else {
            // Fallback to alert
            alert(message);
        }
    }

})();
