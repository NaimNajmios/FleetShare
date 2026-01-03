/**
 * FleetShare Authentication Pages JavaScript
 * Handles interactive features for login and registration
 */

document.addEventListener('DOMContentLoaded', function() {

    // ============================================
    // Toast Notification from URL
    // ============================================
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('error')) {
        showToast('Invalid email or password. Please try again.', 'error');
    }
    if (urlParams.has('logout')) {
        showToast('You have been logged out successfully.', 'success');
    }
    if (urlParams.has('registered')) {
        showToast('Registration successful! Please sign in.', 'success');
    }
    
    // ============================================
    // Password Toggle Functionality
    // ============================================
    
    const togglePasswordButtons = document.querySelectorAll('.toggle-password');
    
    togglePasswordButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const passwordInput = document.getElementById(targetId);
            const icon = this.querySelector('i');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('mdi-eye-outline');
                icon.classList.add('mdi-eye-off-outline');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('mdi-eye-off-outline');
                icon.classList.add('mdi-eye-outline');
            }
        });
    });
    
    // ============================================
    // Email Validation
    // ============================================
    
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    function showError(inputElement, message) {
        inputElement.classList.add('error');
        
        // Remove existing error message if any
        const existingError = inputElement.parentElement.querySelector('.form-error');
        if (existingError) {
            existingError.remove();
        }
        
        // Create and insert error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'form-error';
        errorDiv.innerHTML = `<i class="mdi mdi-alert-circle"></i> ${message}`;
        inputElement.parentElement.appendChild(errorDiv);
    }
    
    function clearError(inputElement) {
        inputElement.classList.remove('error');
        const errorMessage = inputElement.parentElement.querySelector('.form-error');
        if (errorMessage) {
            errorMessage.remove();
        }
    }
    
    // Email validation on blur
    const emailInputs = document.querySelectorAll('input[type="email"]');
    emailInputs.forEach(input => {
        input.addEventListener('blur', function() {
            if (this.value && !validateEmail(this.value)) {
                showError(this, 'Please enter a valid email address');
            } else if (this.value) {
                clearError(this);
            }
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('error') && validateEmail(this.value)) {
                clearError(this);
            }
        });
    });
    
    // ============================================
    // Password Strength Validation
    // ============================================
    
    function validatePassword(password) {
        return password.length >= 8;
    }
    
    const passwordInputs = document.querySelectorAll('input[type="password"][data-validate="true"]');
    passwordInputs.forEach(input => {
        input.addEventListener('blur', function() {
            if (this.value && !validatePassword(this.value)) {
                showError(this, 'Password must be at least 8 characters long');
            } else if (this.value) {
                clearError(this);
            }
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('error') && validatePassword(this.value)) {
                clearError(this);
            }
        });
    });
    
    // ============================================
    // Required Field Validation
    // ============================================
    
    function validateRequired(inputElement) {
        if (!inputElement.value.trim()) {
            showError(inputElement, 'This field is required');
            return false;
        } else {
            clearError(inputElement);
            return true;
        }
    }
    
    // ============================================
    // Form Submission Validation
    // ============================================
    
    const authForms = document.querySelectorAll('.auth-form');
    
    authForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            let isValid = true;
            const requiredInputs = form.querySelectorAll('[required]');
            
            // Validate all required fields
            requiredInputs.forEach(input => {
                if (!validateRequired(input)) {
                    isValid = false;
                }
                
                // Additional validation for email
                if (input.type === 'email' && input.value && !validateEmail(input.value)) {
                    showError(input, 'Please enter a valid email address');
                    isValid = false;
                }
                
                // Additional validation for password
                if (input.type === 'password' && input.hasAttribute('data-validate') && input.value && !validatePassword(input.value)) {
                    showError(input, 'Password must be at least 8 characters long');
                    isValid = false;
                }
            });
            
            // Special validation for Role Cards
            const roleSelect = form.querySelector('#userRole');
            const roleError = form.querySelector('#roleError');
            if (roleSelect && roleSelect.classList.contains('visually-hidden') && !roleSelect.value) {
                isValid = false;
                if (roleError) {
                    roleError.classList.remove('d-none');
                }
            }

            if (isValid) {
                // Show loading state
                const submitButton = form.querySelector('button[type="submit"]');
                if (submitButton) {
                    // Check for inline loading style
                    if (submitButton.classList.contains('btn-loading-inline')) {
                        submitButton.disabled = true;
                        const spinner = submitButton.querySelector('.spinner-border');
                        if (spinner) {
                            spinner.classList.remove('d-none');
                        }
                        // Maintain width
                        submitButton.style.width = submitButton.offsetWidth + 'px';
                    } else {
                        // Default loading style
                        submitButton.classList.add('btn-loading');
                        submitButton.disabled = true;
                    }
                }
                
                // Submit the form
                // In a real Spring Boot app, we let the form submit naturally
                // providing the 'action' attribute is set.
                // However, for demo/simulation purposes or if we want to show the spinner for a bit:
                
                if (form.getAttribute('action')) {
                   form.submit();
                } else {
                    console.log('Form is valid, simulating submission...');
                    // Remove loading state after 2 seconds (for demo)
                     setTimeout(() => {
                        if (submitButton) {
                            if (submitButton.classList.contains('btn-loading-inline')) {
                                submitButton.disabled = false;
                                const spinner = submitButton.querySelector('.spinner-border');
                                if (spinner) spinner.classList.add('d-none');
                                submitButton.style.width = '';
                            } else {
                                submitButton.classList.remove('btn-loading');
                                submitButton.disabled = false;
                            }
                        }
                    }, 2000);
                }
            } else {
                // Scroll to first error
                const firstError = form.querySelector('.error');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });
    });
    
    // ============================================
    // Real-time Input Formatting
    // ============================================
    
    // Postal code formatting (for US zip codes)
    const postalCodeInputs = document.querySelectorAll('input[name="postalCode"]');
    postalCodeInputs.forEach(input => {
        input.addEventListener('input', function() {
            // Remove non-numeric characters
            this.value = this.value.replace(/\D/g, '');
            
            // Limit to 5 or 9 digits (ZIP or ZIP+4)
            if (this.value.length > 5 && this.value.length < 9) {
                this.value = this.value.slice(0, 5);
            } else if (this.value.length > 9) {
                this.value = this.value.slice(0, 9);
            }
        });
    });
    
    // ============================================
    // Smart Form Field Focus
    // ============================================
    
    // Auto-focus first input field
    const firstInput = document.querySelector('.auth-form input:not([type="hidden"])');
    if (firstInput) {
        firstInput.focus();
    }
    
    // ============================================
    // Dropdown Enhancement
    // ============================================
    
    const selectElements = document.querySelectorAll('.form-select');
    selectElements.forEach(select => {
        // Add change event to clear any errors when user makes a selection
        select.addEventListener('change', function() {
            if (this.value) {
                clearError(this);
            }
        });
    });

    // ============================================
    // Role Selection Cards
    // ============================================

    const roleCards = document.querySelectorAll('.role-card');
    const roleSelect = document.getElementById('userRole');
    const roleError = document.getElementById('roleError');

    if (roleCards.length > 0 && roleSelect) {

        function selectRole(card) {
            // Deselect all cards
            roleCards.forEach(c => {
                c.classList.remove('selected');
                c.setAttribute('aria-checked', 'false');
            });

            // Select clicked card
            card.classList.add('selected');
            card.setAttribute('aria-checked', 'true');

            // Update hidden select
            const value = card.getAttribute('data-value');
            roleSelect.value = value;

            // Clear error if present
            if (roleError) {
                roleError.classList.add('d-none');
            }
        }

        roleCards.forEach(card => {
            // Mouse interaction
            card.addEventListener('click', function() {
                selectRole(this);
            });

            // Keyboard interaction
            card.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    selectRole(this);
                }
            });
        });
    }
    
});

// ============================================
// Utility Functions
// ============================================

/**
 * Display a toast notification
 * @param {string} message - The message to display
 * @param {string} type - 'success' or 'error'
 */
function showToast(message, type = 'success') {
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <i class="mdi ${type === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Add to body
    document.body.appendChild(toast);
    
    // Trigger animation
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Remove after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
