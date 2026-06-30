// User Profile - View/Edit Mode Toggle
(function () {
    'use strict';

    // Container is the row element with id viewMode/editMode
    const container = document.getElementById('viewMode') || document.getElementById('editMode');
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
            
            // Enable map editing
            if (typeof enableMapEditing === 'function') {
                enableMapEditing();
            }
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
            
            // Disable map editing and restore original coordinates
            if (typeof disableMapEditing === 'function' && typeof leafletMarker !== 'undefined' && leafletMarker) {
                disableMapEditing();
                let origLat = parseFloat(originalValues[Array.from(formControls).findIndex(el => el.id === 'latitude')]);
                let origLng = parseFloat(originalValues[Array.from(formControls).findIndex(el => el.id === 'longitude')]);
                if (!isNaN(origLat) && !isNaN(origLng)) {
                    leafletMarker.setLatLng([origLat, origLng]);
                    if (leafletMap) leafletMap.setView([origLat, origLng]);
                }
            }
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
                postalCode: document.getElementById('postalCode')?.value || null,
                latitude: document.getElementById('latitude')?.value ? parseFloat(document.getElementById('latitude').value) : null,
                longitude: document.getElementById('longitude')?.value ? parseFloat(document.getElementById('longitude').value) : null,
                bankName: document.getElementById('bankName')?.value || null,
                bankAccountNumber: document.getElementById('bankAccountNumber')?.value || null,
                bankAccountHolder: document.getElementById('bankAccountHolder')?.value || null,
                paymentQrUrl: document.getElementById('paymentQrUrl')?.value || null,
                toyyibpaySecretKey: document.getElementById('toyyibpaySecretKey')?.value || null,
                toyyibpayCategoryCode: document.getElementById('toyyibpayCategoryCode')?.value || null,
                toyyibpayUsername: document.getElementById('toyyibpayUsername')?.value || null
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

    // Leaflet Map Initialization
    var leafletMap = null;
    var leafletMarker = null;

    (function initMap() {
        var mapEl = document.getElementById('user-map');
        if (!mapEl) return;

        var lat = mapEl.dataset.lat ? parseFloat(mapEl.dataset.lat) : null;
        var lng = mapEl.dataset.lng ? parseFloat(mapEl.dataset.lng) : null;
        var businessName = mapEl.dataset.name || 'User Location';

        // Load Leaflet JS dynamically
        var leafletJS = document.createElement('script');
        leafletJS.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
        leafletJS.onload = function () {
            // Fix marker icon issue with Leaflet and Webpack/dynamic load
            delete L.Icon.Default.prototype._getIconUrl;
            L.Icon.Default.mergeOptions({
                iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
                iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
                shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
            });

            var centerLat = lat !== null ? lat : 4.2105;
            var centerLng = lng !== null ? lng : 101.9758;
            var zoom = lat !== null ? 15 : 6;

            leafletMap = L.map('user-map').setView([centerLat, centerLng], zoom);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap contributors'
            }).addTo(leafletMap);

            if (lat !== null && lng !== null) {
                leafletMarker = L.marker([lat, lng])
                    .addTo(leafletMap)
                    .bindPopup('<strong>' + businessName + '</strong>')
                    .openPopup();
            }

            setTimeout(function () { leafletMap.invalidateSize(); }, 200);
        };
        document.head.appendChild(leafletJS);
    })();

    // Enable/Disable Map Editing
    function enableMapEditing() {
        if (!leafletMap) return;

        var mapEditHint = document.getElementById('map-edit-hint');
        if (mapEditHint) mapEditHint.style.display = 'block';

        if (leafletMarker) {
            leafletMarker.dragging.enable();
            leafletMarker.closePopup();
            leafletMarker.bindPopup('<strong>Drag me or click elsewhere</strong>');
        } else {
            var center = leafletMap.getCenter();
            leafletMarker = L.marker([center.lat, center.lng], { draggable: true })
                .addTo(leafletMap)
                .bindPopup('<strong>Drag me or click elsewhere</strong>');
            
            // Update fields immediately
            var latInput = document.getElementById('latitude');
            var lngInput = document.getElementById('longitude');
            if (latInput) latInput.value = center.lat.toFixed(6);
            if (lngInput) lngInput.value = center.lng.toFixed(6);
        }

        leafletMarker.on('dragend', function (e) {
            var position = leafletMarker.getLatLng();
            var latInput = document.getElementById('latitude');
            var lngInput = document.getElementById('longitude');
            if (latInput) latInput.value = position.lat.toFixed(6);
            if (lngInput) lngInput.value = position.lng.toFixed(6);
        });

        leafletMap.on('click', function (e) {
            if (leafletMarker) {
                leafletMarker.setLatLng(e.latlng);
            } else {
                leafletMarker = L.marker(e.latlng, { draggable: true }).addTo(leafletMap);
            }
            
            var latInput = document.getElementById('latitude');
            var lngInput = document.getElementById('longitude');
            if (latInput) latInput.value = e.latlng.lat.toFixed(6);
            if (lngInput) lngInput.value = e.latlng.lng.toFixed(6);
        });
    }

    function disableMapEditing() {
        if (!leafletMap) return;
        
        var mapEditHint = document.getElementById('map-edit-hint');
        if (mapEditHint) mapEditHint.style.display = 'none';

        leafletMap.off('click');
        if (leafletMarker) {
            leafletMarker.dragging.disable();
            
            var mapEl = document.getElementById('user-map');
            var businessName = mapEl ? (mapEl.dataset.name || 'User Location') : 'User Location';
            leafletMarker.bindPopup('<strong>' + businessName + '</strong>');
        }
    }

})();
