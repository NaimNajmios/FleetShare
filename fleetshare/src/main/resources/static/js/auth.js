/**
 * FleetShare Authentication Logic
 * Handles password toggles, strength metering, multi-step forms, and role selection.
 */

document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // ==========================================
    // Password Visibility Toggle
    // ==========================================
    const toggleButtons = document.querySelectorAll('[data-toggle="password"]');
    
    toggleButtons.forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault(); // Prevent focus loss if possible or form submission
            const targetId = this.dataset.target;
            const input = document.getElementById(targetId);
            const icon = this.querySelector('i');

            if (!input) return;

            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('mdi-eye-outline');
                icon.classList.add('mdi-eye-off-outline');
                this.setAttribute('aria-label', 'Hide password');
            } else {
                input.type = 'password';
                icon.classList.remove('mdi-eye-off-outline');
                icon.classList.add('mdi-eye-outline');
                this.setAttribute('aria-label', 'Show password');
            }
        });
    });

    // ==========================================
    // Password Strength Meter
    // ==========================================
    const passwordInput = document.getElementById('registerPassword');
    const strengthFill = document.getElementById('strengthFill');
    const strengthText = document.getElementById('strengthText');

    if (passwordInput && strengthFill && strengthText) {
        passwordInput.addEventListener('input', function () {
            const password = this.value;
            let strength = 0;

            if (password.length >= 8) strength++;
            if (/[A-Z]/.test(password)) strength++;
            if (/[a-z]/.test(password)) strength++;
            if (/[0-9]/.test(password)) strength++;
            if (/[^A-Za-z0-9]/.test(password)) strength++;

            const levels = [
                { width: '0%', color: '#e5e7eb', text: 'Password strength' },
                { width: '20%', color: '#dc2626', text: 'Very Weak' },
                { width: '40%', color: '#f97316', text: 'Weak' },
                { width: '60%', color: '#eab308', text: 'Fair' },
                { width: '80%', color: '#22c55e', text: 'Strong' },
                { width: '100%', color: '#16a34a', text: 'Very Strong' }
            ];

            const level = levels[strength] || levels[0];
            strengthFill.style.width = level.width;
            strengthFill.style.backgroundColor = level.color;
            strengthText.textContent = level.text;
            strengthText.style.color = level.color;
        });
    }

    // ==========================================
    // Password Match Validation
    // ==========================================
    const confirmInput = document.getElementById('confirmPassword');
    const matchError = document.getElementById('passwordMatchError');

    if (confirmInput && passwordInput && matchError) {
        const checkMatch = () => {
            const password = passwordInput.value;
            const confirm = confirmInput.value;

            if (confirm && password !== confirm) {
                matchError.classList.remove('d-none');
                confirmInput.classList.add('error');
            } else {
                matchError.classList.add('d-none');
                confirmInput.classList.remove('error');
            }
        };

        confirmInput.addEventListener('input', checkMatch);
        passwordInput.addEventListener('input', () => {
            if (confirmInput.value) checkMatch();
        });
    }

    // ==========================================
    // Multi-Step Form Navigation
    // ==========================================
    const steps = document.querySelectorAll('.form-step-content');
    const progressSteps = document.querySelectorAll('.form-steps .step');
    
    // Next Step Buttons
    document.querySelectorAll('[data-action="next-step"]').forEach(btn => {
        btn.addEventListener('click', function () {
            const nextStepNum = parseInt(this.dataset.target);
            const currentStepEl = document.querySelector('.form-step-content.active');

            if (validateStep(currentStepEl)) {
                showStep(nextStepNum);
            }
        });
    });

    // Prev Step Buttons
    document.querySelectorAll('[data-action="prev-step"]').forEach(btn => {
        btn.addEventListener('click', function () {
            const prevStepNum = parseInt(this.dataset.target);
            showStep(prevStepNum);
        });
    });

    function showStep(stepNum) {
        // Hide all steps
        steps.forEach(step => step.classList.remove('active'));

        // Show target step
        const targetStep = document.querySelector(`.form-step-content[data-step="${stepNum}"]`);
        if (targetStep) {
            targetStep.classList.add('active');
        }

        // Update progress indicators
        progressSteps.forEach((s, i) => {
            if (i + 1 <= stepNum) s.classList.add('active');
            else s.classList.remove('active');
        });
    }

    function validateStep(stepEl) {
        if (!stepEl) return true;

        const inputs = stepEl.querySelectorAll('input[required], select[required]');
        let isValid = true;

        inputs.forEach(input => {
            if (!input.value.trim()) {
                input.classList.add('error');
                isValid = false;
            } else {
                input.classList.remove('error');
            }
        });

        // Special check for password match on Step 1
        if (stepEl.dataset.step === '1') {
            const p1 = document.getElementById('registerPassword');
            const p2 = document.getElementById('confirmPassword');
            const err = document.getElementById('passwordMatchError');
            
            if (p1 && p2 && p1.value !== p2.value) {
                isValid = false;
                if (err) err.classList.remove('d-none');
            }
        }

        return isValid;
    }

    // ==========================================
    // Role Selection
    // ==========================================
    const roleCards = document.querySelectorAll('.role-card');
    const userRoleInput = document.getElementById('userRole');
    const roleError = document.getElementById('roleError');

    if (roleCards.length > 0 && userRoleInput) {
        const selectRole = (card) => {
            // Reset all cards
            roleCards.forEach(c => {
                c.classList.remove('selected');
                c.setAttribute('aria-checked', 'false');
            });

            // Select clicked card
            card.classList.add('selected');
            card.setAttribute('aria-checked', 'true');

            // Update hidden input
            userRoleInput.value = card.dataset.value;

            // Hide error if visible
            if (roleError) roleError.classList.add('d-none');
        };

        roleCards.forEach(card => {
            // Click handler
            card.addEventListener('click', () => selectRole(card));

            // Keyboard handler (Enter or Space)
            card.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    selectRole(this);
                }
            });
        });
    }

    // ==========================================
    // Form Submission (Loading State)
    // ==========================================
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function (e) {
            // If it's the register form, validate role and terms
            if (this.id === 'registerForm') {
                const role = document.getElementById('userRole');
                const terms = document.getElementById('agreeTerms');
                const rError = document.getElementById('roleError');

                if (role && !role.value) {
                    e.preventDefault();
                    if (rError) rError.classList.remove('d-none');
                    return;
                }

                if (terms && !terms.checked) {
                    e.preventDefault();
                    return;
                }
            }

            // Show loading state on submit button
            const btn = this.querySelector('button[type="submit"]');
            if (btn) {
                const btnText = btn.querySelector('.btn-text');
                const btnLoader = btn.querySelector('.btn-loader');

                if (btnText && btnLoader) {
                    btnText.classList.add('d-none');
                    btnLoader.classList.remove('d-none');
                    btn.disabled = true;
                }
            }
        });
    });

    // ==========================================
    // Input Focus Effects
    // ==========================================
    const inputs = document.querySelectorAll('.form-input, .form-control');
    inputs.forEach(input => {
        input.addEventListener('focus', function () {
            const wrapper = this.closest('.input-wrapper') || this.parentElement;
            if (wrapper) wrapper.classList.add('focused');
        });

        input.addEventListener('blur', function () {
            const wrapper = this.closest('.input-wrapper') || this.parentElement;
            if (wrapper) wrapper.classList.remove('focused');
        });
    });

    // ==========================================
    // Toast Notifications (Progressive Enhancement)
    // ==========================================
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('error')) {
        showToast('Invalid email or password', 'error');
    } else if (urlParams.has('logout')) {
        showToast('Logged out successfully', 'success');
    } else if (urlParams.has('registered')) {
        showToast('Account created! Please sign in.', 'success');
    }

    function showToast(message, type = 'info') {
        if (typeof window.showToast === 'function') {
            window.showToast(message, type);
        } else {
            // Fallback for when global showToast isn't available
            // We can try to bootstrap a toast or just rely on static alerts
            // For now, logging to console for debug
            console.log(`Toast [${type}]: ${message}`);
        }
    }

});
