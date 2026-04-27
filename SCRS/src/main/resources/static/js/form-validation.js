document.addEventListener('DOMContentLoaded', () => {
    const invalidClass = 'invalid';
    const errorClass = 'field-error';

    function createError(field, message) {
        let error = field.nextElementSibling;
        if (!error || !error.classList.contains(errorClass)) {
            error = document.createElement('div');
            error.className = errorClass;
            field.insertAdjacentElement('afterend', error);
        }
        error.textContent = message;
        field.classList.add(invalidClass);
        field.setAttribute('aria-invalid', 'true');
    }

    function removeError(field) {
        const error = field.nextElementSibling;
        if (error && error.classList.contains(errorClass)) {
            error.remove();
        }
        field.classList.remove(invalidClass);
        field.removeAttribute('aria-invalid');
    }

    function sanitizeNumberValue(value) {
        let sanitized = value.replace(/[^0-9.-]/g, '');
        const hasMinus = sanitized.startsWith('-');
        sanitized = sanitized.replace(/-/g, '');
        if (hasMinus) {
            sanitized = '-' + sanitized;
        }
        const parts = sanitized.split('.');
        if (parts.length > 2) {
            sanitized = parts.shift() + '.' + parts.join('');
        }
        if (sanitized === '-.' ) {
            sanitized = '-0.';
        }
        if (sanitized.startsWith('.') ) {
            sanitized = '0' + sanitized;
        }
        return sanitized;
    }

    function sanitizeDigitsOnly(value) {
        return value.replace(/[^0-9]/g, '');
    }

    function isStrictDigitsField(field) {
        if (field.type !== 'text') {
            return false;
        }
        const name = (field.name || field.id || '').toLowerCase();
        if (/mobile|phone|tel/.test(name)) {
            return true;
        }
        if (!field.pattern) {
            return false;
        }
        let pattern = field.pattern.trim();
        if (pattern.startsWith('^') && pattern.endsWith('$')) {
            pattern = pattern.slice(1, -1);
        }
        const digitsPattern = /^\[0-9\](?:\{\d+(?:,\d*)?\}|\*|\+|\?)?$/;
        const dPattern = /^\\d(?:\{\d+(?:,\d*)?\}|\*|\+|\?)?$/;
        return digitsPattern.test(pattern) || dPattern.test(pattern);
    }

    function handleNumericTextInput(event) {
        const field = event.target;
        if (!isStrictDigitsField(field)) {
            return;
        }
        const sanitized = sanitizeDigitsOnly(field.value);
        if (sanitized !== field.value) {
            field.value = sanitized;
        }
    }

    function handleNumericTextBeforeInput(event) {
        const field = event.target;
        if (!isStrictDigitsField(field)) {
            return;
        }
        if (!event.data) {
            return;
        }
        if (!/^[0-9]$/.test(event.data)) {
            event.preventDefault();
        }
    }

    function validateField(field) {
        if (field.disabled || field.type === 'hidden') {
            removeError(field);
            return true;
        }

        let message = '';
        const value = field.value.trim();

        if (field.required && field.type !== 'radio' && field.type !== 'checkbox') {
            if (!value) {
                message = 'This field is required.';
            }
        }

        if (!message && field.type === 'email' && value) {
            const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRe.test(value)) {
                message = 'Enter a valid email address.';
            }
        }

        if (!message && field.type === 'password' && value) {
            const passwordRe = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
            if (!passwordRe.test(value)) {
                message = 'Password must be at least 8 characters and include upper and lower case letters plus a number.';
            }
            if (!message && field.name === 'confirmPassword') {
                const passwordField = field.form ? field.form.querySelector('input[name="password"]') : null;
                if (passwordField && passwordField.value !== value) {
                    message = 'Passwords do not match.';
                }
            }
            if (!message && field.name === 'password') {
                const confirmField = field.form ? field.form.querySelector('input[name="confirmPassword"]') : null;
                if (confirmField && confirmField.value && confirmField.value !== value) {
                    createError(confirmField, 'Passwords do not match.');
                }
            }
        }

        if (!message && field.type === 'number' && value) {
            const numberRe = /^-?\d*(\.\d*)?$/;
            if (!numberRe.test(value)) {
                message = 'Only numbers are allowed.';
            }
            if (!message) {
                const numericValue = Number(value);
                if (field.min !== '' && !Number.isNaN(numericValue) && numericValue < Number(field.min)) {
                    message = `Minimum value is ${field.min}.`;
                }
                if (field.max !== '' && !Number.isNaN(numericValue) && numericValue > Number(field.max)) {
                    message = `Maximum value is ${field.max}.`;
                }
            }
        }

        if (!message && field.pattern && value) {
            const regex = new RegExp(`^(?:${field.pattern})$`);
            if (!regex.test(value)) {
                message = 'Please match the requested format.';
            }
        }

        if (!message && field.type === 'checkbox' && field.required) {
            if (!field.checked) {
                message = 'This option is required.';
            }
        }

        if (!message && field.type === 'radio' && field.required) {
            const group = field.form ? field.form.querySelectorAll(`input[name="${field.name}"]`) : [];
            if (group.length && !Array.from(group).some((radio) => radio.checked)) {
                message = 'Please select an option.';
            }
        }

        if (message) {
            createError(field.type === 'radio' ? field.form.querySelector(`input[name="${field.name}"]`) || field : field, message);
            return false;
        }

        removeError(field.type === 'radio' ? field.form.querySelector(`input[name="${field.name}"]`) || field : field);
        return true;
    }

    function handleNumberInput(event) {
        const field = event.target;
        if (field.type !== 'number') {
            return;
        }
        const sanitized = sanitizeNumberValue(field.value);
        if (sanitized !== field.value) {
            field.value = sanitized;
        }
    }

    function handleNumberBeforeInput(event) {
        const field = event.target;
        if (field.type !== 'number') {
            return;
        }
        if (!event.data) {
            return;
        }
        const value = field.value;
        const start = field.selectionStart ?? value.length;
        const end = field.selectionEnd ?? value.length;
        const newValue = value.slice(0, start) + event.data + value.slice(end);
        const isDigit = /^[0-9]$/.test(event.data);
        const isDot = event.data === '.';
        const isMinus = event.data === '-';

        if (isDigit) {
            return;
        }
        if (isDot) {
            if (newValue.split('.').length > 2) {
                event.preventDefault();
            }
            return;
        }
        if (isMinus) {
            if (start !== 0 || value.includes('-')) {
                event.preventDefault();
            }
            return;
        }
        event.preventDefault();
    }

    function handleNumberKeyDown(event) {
        const field = event.target;
        if (field.type !== 'number') {
            return;
        }
        const allowedKeys = [
            'Backspace', 'Tab', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown',
            'Home', 'End', 'Delete', 'Enter', 'Escape', 'Shift', 'Control', 'Meta', 'Alt'
        ];
        if (allowedKeys.includes(event.key) || event.ctrlKey || event.metaKey) {
            return;
        }
        const value = field.value;
        const isNumber = /^[0-9]$/.test(event.key);
        const isDot = event.key === '.';
        const isMinus = event.key === '-';
        if (isNumber) {
            return;
        }
        if (isDot) {
            if (value.includes('.')) {
                event.preventDefault();
            }
            return;
        }
        if (isMinus) {
            if (value.includes('-') || field.selectionStart !== 0) {
                event.preventDefault();
            }
            return;
        }
        event.preventDefault();
    }

    function attachFieldListeners(form) {
        const fields = form.querySelectorAll('input, textarea, select');
        fields.forEach((field) => {
            if (field.type === 'number') {
                field.addEventListener('beforeinput', handleNumberBeforeInput);
                field.addEventListener('keydown', handleNumberKeyDown);
                field.addEventListener('input', handleNumberInput);
            }
            if (isStrictDigitsField(field)) {
                field.addEventListener('beforeinput', handleNumericTextBeforeInput);
                field.addEventListener('input', handleNumericTextInput);
            }
            field.addEventListener('input', () => {
                validateField(field);
                if (field.type === 'password' && field.name === 'password' && field.form) {
                    const confirmField = field.form.querySelector('input[name="confirmPassword"]');
                    if (confirmField && confirmField.value) {
                        validateField(confirmField);
                    }
                }
            });
            field.addEventListener('change', () => validateField(field));
            field.addEventListener('blur', () => validateField(field));
        });

        form.addEventListener('submit', (event) => {
            const invalid = Array.from(fields).some((field) => !validateField(field));
            if (invalid) {
                event.preventDefault();
                const firstInvalid = form.querySelector('.' + invalidClass);
                if (firstInvalid) {
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstInvalid.focus({ preventScroll: true });
                }
            }
        });
    }

    document.querySelectorAll('form').forEach((form) => attachFieldListeners(form));
});
