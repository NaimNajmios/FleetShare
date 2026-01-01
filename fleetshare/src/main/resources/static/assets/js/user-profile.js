// User Profile - View/Edit Mode Toggle
(function () {
    'use strict';

    const container = document.querySelector('.profile-container');
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const saveBtn = document.getElementById('saveBtn');
    const formControls = document.querySelectorAll('.form-control');
    const selectControls = document.querySelectorAll('select.form-control');

    // Profile image file to be uploaded on save
    let selectedImageFile = null;

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

                // Upload profile image first if one was selected
                if (selectedImageFile) {
                    const imageUploaded = await uploadProfileImage(userId, csrfToken, csrfHeader);
                    if (!imageUploaded) {
                        showNotification('Failed to upload profile image', 'error');
                        return;
                    }
                    selectedImageFile = null; // Clear after successful upload
                }

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
                    // Reload page after short delay to show updated data
                    setTimeout(() => location.reload(), 1000);

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

    // Notification helper function using toast (matches admin profile.html pattern)
    function showNotification(message, type) {
        // Remove any existing toast
        const existing = document.querySelector('.toast-notification');
        if (existing) existing.remove();

        // Create toast element
        const toast = document.createElement('div');
        toast.className = 'toast-notification ' + (type === 'success' ? 'toast-success' : 'toast-error');
        toast.style.cssText = 'position:fixed;top:20px;right:20px;padding:1rem 1.5rem;border-radius:0.75rem;color:white;font-weight:500;z-index:9999;opacity:0;transform:translateX(100px);transition:all 0.3s;box-shadow:0 10px 25px rgba(0,0,0,0.2);background:' + (type === 'success' ? 'linear-gradient(135deg,#10b981,#059669)' : 'linear-gradient(135deg,#ef4444,#dc2626)');
        toast.innerHTML = '<i class="mdi ' + (type === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle') + ' mr-2"></i>' + message;
        document.body.appendChild(toast);

        // Animate in
        setTimeout(() => { toast.style.opacity = '1'; toast.style.transform = 'translateX(0)'; }, 10);
        // Remove after 3 seconds
        setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 300); }, 3000);
    }

    // Profile Image - Preview and upload on save
    const changeImageBtn = document.getElementById('changeImageBtn');
    const profileImageInput = document.getElementById('profileImageInput');
    // Find avatar by class since it doesn't have an ID
    const avatarCircle = document.querySelector('.avatar-circle');
    const profileAvatar = document.querySelector('.profile-avatar');

    if (changeImageBtn && profileImageInput) {
        changeImageBtn.addEventListener('click', function () {
            profileImageInput.click();
        });

        profileImageInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (!file) return;

            // Validate file type
            if (!file.type.startsWith('image/')) {
                showNotification('Please select an image file', 'error');
                return;
            }

            // Validate file size (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showNotification('Image size must be less than 5MB', 'error');
                return;
            }

            // Store the file for later upload
            selectedImageFile = file;

            // Show preview in avatar
            const reader = new FileReader();
            reader.onload = function (event) {
                // Hide the letter avatar if exists
                if (avatarCircle) {
                    avatarCircle.style.display = 'none';
                }

                // Also hide existing profile image if any
                const existingImg = document.querySelector('.profile-avatar > img:not(#avatarPreview)');
                if (existingImg) {
                    existingImg.style.display = 'none';
                }

                // Check if preview image already exists
                let previewImg = document.getElementById('avatarPreview');
                if (!previewImg) {
                    previewImg = document.createElement('img');
                    previewImg.id = 'avatarPreview';
                    previewImg.style.cssText = 'width: 120px; height: 120px; border-radius: 50%; object-fit: cover;';
                    // Insert at start of profile-avatar div
                    if (profileAvatar) {
                        profileAvatar.insertBefore(previewImg, profileAvatar.firstChild);
                    }
                }
                previewImg.src = event.target.result;
            };
            reader.readAsDataURL(file);

            showNotification('Image selected. Click "Save Changes" to apply.', 'success');
        });
    }

    // Helper function to upload profile image
    async function uploadProfileImage(userId, csrfToken, csrfHeader) {
        if (!selectedImageFile) return true; // No image to upload

        const formData = new FormData();
        formData.append('image', selectedImageFile);

        const response = await fetch(`/admin/users/${userId}/profile-image`, {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            },
            body: formData
        });

        const result = await response.json();
        return response.ok && result.success;
    }

})();
