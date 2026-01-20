/**
 * FleetShare Authentication Logic
 * Handles password toggles, strength metering, multi-step forms, role selection,
 * and Malaysian location dropdowns.
 */

document.addEventListener('DOMContentLoaded', function () {
    'use strict';

    // ==========================================
    // Malaysian Location Data
    // ==========================================
    const malaysiaLocationData = {
        'Johor': {
            cities: ['Johor Bahru', 'Iskandar Puteri', 'Kulai', 'Kluang', 'Batu Pahat', 'Muar', 'Segamat', 'Pontian', 'Kota Tinggi', 'Mersing', 'Pasir Gudang', 'Skudai', 'Senai', 'Tangkak', 'Yong Peng'],
            postalCodes: {
                'Johor Bahru': ['80000', '80100', '80150', '80200', '80250', '80300', '80350', '80400', '81100', '81200'],
                'Iskandar Puteri': ['79000', '79100', '79150', '79200', '79250'],
                'Kulai': ['81000', '81100'],
                'Kluang': ['86000', '86100'],
                'Batu Pahat': ['83000', '83100', '83200'],
                'Muar': ['84000', '84100', '84200'],
                'Segamat': ['85000', '85100'],
                'Pontian': ['82000', '82100'],
                'Kota Tinggi': ['81900'],
                'Mersing': ['86800'],
                'Pasir Gudang': ['81700'],
                'Skudai': ['81300'],
                'Senai': ['81400'],
                'Tangkak': ['84900'],
                'Yong Peng': ['83700']
            }
        },
        'Kedah': {
            cities: ['Alor Setar', 'Sungai Petani', 'Kulim', 'Langkawi', 'Jitra', 'Baling', 'Yan', 'Pendang', 'Pokok Sena', 'Kuala Kedah'],
            postalCodes: {
                'Alor Setar': ['05000', '05050', '05100', '05150', '05200', '05250', '05300', '05350', '05400'],
                'Sungai Petani': ['08000', '08100', '08200', '08300', '08400', '08500'],
                'Kulim': ['09000', '09100'],
                'Langkawi': ['07000', '07100'],
                'Jitra': ['06000', '06100'],
                'Baling': ['09100'],
                'Yan': ['06300'],
                'Pendang': ['06700'],
                'Pokok Sena': ['06400'],
                'Kuala Kedah': ['06600']
            }
        },
        'Kelantan': {
            cities: ['Kota Bharu', 'Pasir Mas', 'Tanah Merah', 'Machang', 'Kuala Krai', 'Pasir Puteh', 'Bachok', 'Tumpat', 'Gua Musang', 'Jeli'],
            postalCodes: {
                'Kota Bharu': ['15000', '15050', '15100', '15150', '15200', '15300', '15350', '15400'],
                'Pasir Mas': ['17000', '17010'],
                'Tanah Merah': ['17500', '17510'],
                'Machang': ['18500'],
                'Kuala Krai': ['18000'],
                'Pasir Puteh': ['16800'],
                'Bachok': ['16020', '16030'],
                'Tumpat': ['16200'],
                'Gua Musang': ['18300'],
                'Jeli': ['17600']
            }
        },
        'Kuala Lumpur': {
            cities: ['Kuala Lumpur City Centre', 'Bangsar', 'Cheras', 'Kepong', 'Setapak', 'Wangsa Maju', 'Titiwangsa', 'Bukit Bintang', 'Sentul', 'Setiawangsa'],
            postalCodes: {
                'Kuala Lumpur City Centre': ['50000', '50050', '50088', '50100', '50150', '50200', '50250', '50300', '50350', '50400', '50450', '50460', '50470', '50480', '50490'],
                'Bangsar': ['59000', '59100', '59200'],
                'Cheras': ['56000', '56100', '56200'],
                'Kepong': ['52000', '52100', '52200'],
                'Setapak': ['53000', '53100', '53200', '53300'],
                'Wangsa Maju': ['53300'],
                'Titiwangsa': ['53200'],
                'Bukit Bintang': ['55100', '55200'],
                'Sentul': ['51000', '51100'],
                'Setiawangsa': ['54200']
            }
        },
        'Labuan': {
            cities: ['Labuan'],
            postalCodes: {
                'Labuan': ['87000', '87008', '87009', '87010', '87011', '87012', '87013', '87014', '87015', '87016', '87017', '87018', '87019', '87020']
            }
        },
        'Melaka': {
            cities: ['Melaka City', 'Ayer Keroh', 'Alor Gajah', 'Jasin', 'Masjid Tanah', 'Merlimau', 'Sungai Udang', 'Durian Tunggal', 'Batu Berendam'],
            postalCodes: {
                'Melaka City': ['75000', '75050', '75100', '75150', '75200', '75250', '75260', '75300', '75350', '75400', '75450', '75460'],
                'Ayer Keroh': ['75450'],
                'Alor Gajah': ['78000'],
                'Jasin': ['77000'],
                'Masjid Tanah': ['78300'],
                'Merlimau': ['77300'],
                'Sungai Udang': ['76300'],
                'Durian Tunggal': ['76100'],
                'Batu Berendam': ['75350']
            }
        },
        'Negeri Sembilan': {
            cities: ['Seremban', 'Port Dickson', 'Nilai', 'Rembau', 'Kuala Pilah', 'Bahau', 'Tampin', 'Senawang', 'Rantau', 'Jelebu'],
            postalCodes: {
                'Seremban': ['70000', '70050', '70100', '70200', '70300', '70400', '70450', '70500', '70502', '70503', '70504', '70505', '70506'],
                'Port Dickson': ['71000', '71009', '71010'],
                'Nilai': ['71800'],
                'Rembau': ['71300'],
                'Kuala Pilah': ['72000'],
                'Bahau': ['72100'],
                'Tampin': ['73000'],
                'Senawang': ['70450'],
                'Rantau': ['71100'],
                'Jelebu': ['71600']
            }
        },
        'Pahang': {
            cities: ['Kuantan', 'Temerloh', 'Bentong', 'Raub', 'Pekan', 'Jerantut', 'Kuala Lipis', 'Cameron Highlands', 'Rompin', 'Maran', 'Genting Highlands'],
            postalCodes: {
                'Kuantan': ['25000', '25050', '25100', '25150', '25200', '25250', '25300', '25350'],
                'Temerloh': ['28000', '28020'],
                'Bentong': ['28700'],
                'Raub': ['27600'],
                'Pekan': ['26600'],
                'Jerantut': ['27000'],
                'Kuala Lipis': ['27200'],
                'Cameron Highlands': ['39000', '39007', '39010'],
                'Rompin': ['26800'],
                'Maran': ['26500'],
                'Genting Highlands': ['69000']
            }
        },
        'Penang': {
            cities: ['George Town', 'Butterworth', 'Bayan Lepas', 'Bukit Mertajam', 'Nibong Tebal', 'Tanjung Bungah', 'Jelutong', 'Air Itam', 'Gelugor', 'Kepala Batas', 'Seberang Jaya'],
            postalCodes: {
                'George Town': ['10000', '10050', '10100', '10150', '10200', '10250', '10300', '10350', '10400', '10450', '10460', '10470'],
                'Butterworth': ['12000', '12100', '12200', '12300'],
                'Bayan Lepas': ['11900'],
                'Bukit Mertajam': ['14000', '14020'],
                'Nibong Tebal': ['14300'],
                'Tanjung Bungah': ['11200'],
                'Jelutong': ['11600'],
                'Air Itam': ['11500'],
                'Gelugor': ['11700'],
                'Kepala Batas': ['13200'],
                'Seberang Jaya': ['13700']
            }
        },
        'Perak': {
            cities: ['Ipoh', 'Taiping', 'Teluk Intan', 'Lumut', 'Sitiawan', 'Kuala Kangsar', 'Kampar', 'Batu Gajah', 'Sungai Siput', 'Tanjung Malim', 'Slim River', 'Gerik'],
            postalCodes: {
                'Ipoh': ['30000', '30010', '30020', '30100', '30200', '30250', '30300', '30350', '30450', '30500', '30502', '30504', '30505', '30506'],
                'Taiping': ['34000', '34007', '34008', '34009', '34010'],
                'Teluk Intan': ['36000', '36007', '36008', '36009', '36010'],
                'Lumut': ['32200'],
                'Sitiawan': ['32000'],
                'Kuala Kangsar': ['33000'],
                'Kampar': ['31900'],
                'Batu Gajah': ['31000'],
                'Sungai Siput': ['31100'],
                'Tanjung Malim': ['35900'],
                'Slim River': ['35800'],
                'Gerik': ['33300']
            }
        },
        'Perlis': {
            cities: ['Kangar', 'Arau', 'Padang Besar', 'Kuala Perlis'],
            postalCodes: {
                'Kangar': ['01000', '01500', '01502', '01503', '01504', '01505', '01506'],
                'Arau': ['02600'],
                'Padang Besar': ['02100'],
                'Kuala Perlis': ['02000']
            }
        },
        'Putrajaya': {
            cities: ['Putrajaya'],
            postalCodes: {
                'Putrajaya': ['62000', '62007', '62050', '62100', '62150', '62200', '62250', '62300', '62502', '62504', '62505', '62506', '62510', '62512', '62514', '62516', '62517', '62518', '62519', '62520', '62522', '62524', '62526', '62527', '62530', '62532', '62536', '62540', '62542', '62546', '62550', '62551', '62570', '62574', '62576', '62582', '62584', '62590', '62592', '62594', '62596', '62598', '62602', '62604', '62616', '62618', '62620', '62622', '62624', '62628', '62630', '62632', '62650', '62652', '62654', '62656', '62658', '62662', '62668', '62670', '62674', '62676', '62677', '62686', '62688', '62692', '62694', '62696', '62988']
            }
        },
        'Sabah': {
            cities: ['Kota Kinabalu', 'Sandakan', 'Tawau', 'Lahad Datu', 'Keningau', 'Semporna', 'Kudat', 'Ranau', 'Beaufort', 'Papar', 'Kota Belud', 'Kinabatangan'],
            postalCodes: {
                'Kota Kinabalu': ['88000', '88100', '88200', '88300', '88400', '88450', '88460', '88500', '88502', '88503', '88504', '88505', '88506'],
                'Sandakan': ['90000', '90007', '90008', '90009', '90010'],
                'Tawau': ['91000', '91007', '91008', '91009', '91010'],
                'Lahad Datu': ['91100'],
                'Keningau': ['89000', '89007', '89008', '89009'],
                'Semporna': ['91300'],
                'Kudat': ['89050', '89057', '89058'],
                'Ranau': ['89300'],
                'Beaufort': ['89800', '89807', '89808'],
                'Papar': ['89600', '89607', '89608'],
                'Kota Belud': ['89150', '89157', '89158'],
                'Kinabatangan': ['90200']
            }
        },
        'Sarawak': {
            cities: ['Kuching', 'Miri', 'Sibu', 'Bintulu', 'Limbang', 'Sarikei', 'Sri Aman', 'Kapit', 'Mukah', 'Lawas', 'Betong', 'Marudi'],
            postalCodes: {
                'Kuching': ['93000', '93010', '93050', '93100', '93150', '93200', '93250', '93300', '93350', '93400', '93450', '93502', '93503', '93504', '93505', '93506'],
                'Miri': ['98000', '98007', '98008', '98009', '98010'],
                'Sibu': ['96000', '96007', '96008', '96009', '96010'],
                'Bintulu': ['97000', '97007', '97008', '97009', '97010'],
                'Limbang': ['98700', '98707', '98708'],
                'Sarikei': ['96100'],
                'Sri Aman': ['95000'],
                'Kapit': ['96800'],
                'Mukah': ['96400'],
                'Lawas': ['98850', '98857', '98858'],
                'Betong': ['95700'],
                'Marudi': ['98050']
            }
        },
        'Selangor': {
            cities: ['Shah Alam', 'Petaling Jaya', 'Subang Jaya', 'Klang', 'Kajang', 'Ampang', 'Selayang', 'Rawang', 'Sepang', 'Cyberjaya', 'Puchong', 'Seri Kembangan', 'Bangi', 'Damansara', 'Kuala Selangor', 'Banting', 'Semenyih'],
            postalCodes: {
                'Shah Alam': ['40000', '40100', '40150', '40160', '40170', '40200', '40300', '40400', '40450', '40460', '40470'],
                'Petaling Jaya': ['46000', '46050', '46100', '46150', '46200', '46300', '46350', '46400', '46506', '46547', '46549', '46551', '46564', '46582', '46598', '46662', '46667', '46668', '46675', '46700', '46710', '46720', '46730', '46740', '46750', '46760', '46770', '46780', '46781', '46782', '46783', '46784', '46785', '46786', '46787', '46788', '46789', '46790', '46791'],
                'Subang Jaya': ['47500', '47600', '47610', '47620', '47630', '47640', '47650'],
                'Klang': ['41000', '41050', '41100', '41150', '41200', '41250', '41300', '41400', '41506', '41560', '41586'],
                'Kajang': ['43000', '43300', '43500', '43700', '43800', '43900'],
                'Ampang': ['68000'],
                'Selayang': ['68100'],
                'Rawang': ['48000', '48010', '48020', '48050', '48100', '48200', '48300'],
                'Sepang': ['43900'],
                'Cyberjaya': ['63000'],
                'Puchong': ['47100', '47110', '47120', '47130', '47140', '47150', '47160', '47170', '47180', '47190'],
                'Seri Kembangan': ['43300'],
                'Bangi': ['43600', '43650'],
                'Damansara': ['47400', '47410', '47800', '47810', '47820', '47830'],
                'Kuala Selangor': ['45000'],
                'Banting': ['42700'],
                'Semenyih': ['43500']
            }
        },
        'Terengganu': {
            cities: ['Kuala Terengganu', 'Kemaman', 'Dungun', 'Besut', 'Marang', 'Hulu Terengganu', 'Setiu', 'Kuala Nerus'],
            postalCodes: {
                'Kuala Terengganu': ['20000', '20050', '20100', '20200', '20300', '20400', '20500', '20502', '20503', '20504', '20505', '20506'],
                'Kemaman': ['24000', '24007', '24009'],
                'Dungun': ['23000'],
                'Besut': ['22000', '22010', '22020'],
                'Marang': ['21600'],
                'Hulu Terengganu': ['21700'],
                'Setiu': ['22100'],
                'Kuala Nerus': ['21060']
            }
        }
    };

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
    // Malaysian Location Cascading Dropdowns
    // ==========================================
    const stateInput = document.getElementById('state');
    const cityInput = document.getElementById('city');
    const postalCodeInput = document.getElementById('postalCode');
    const cityList = document.getElementById('cityList');
    const postalCodeList = document.getElementById('postalCodeList');

    if (stateInput && cityInput && postalCodeInput && cityList && postalCodeList) {
        // Update cities when state changes
        function updateCities() {
            const state = stateInput.value.trim();
            cityList.innerHTML = '';
            postalCodeList.innerHTML = '';

            if (malaysiaLocationData[state]) {
                malaysiaLocationData[state].cities.forEach(city => {
                    const option = document.createElement('option');
                    option.value = city;
                    cityList.appendChild(option);
                });
            }
        }

        // Update postal codes when city changes
        function updatePostalCodes() {
            const state = stateInput.value.trim();
            const city = cityInput.value.trim();
            postalCodeList.innerHTML = '';

            if (malaysiaLocationData[state] && malaysiaLocationData[state].postalCodes[city]) {
                malaysiaLocationData[state].postalCodes[city].forEach(postalCode => {
                    const option = document.createElement('option');
                    option.value = postalCode;
                    postalCodeList.appendChild(option);
                });
            }
        }

        stateInput.addEventListener('input', updateCities);
        stateInput.addEventListener('change', updateCities);
        cityInput.addEventListener('input', updatePostalCodes);
        cityInput.addEventListener('change', updatePostalCodes);
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
