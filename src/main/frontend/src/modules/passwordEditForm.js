let hidePasswordTimeout;

export function togglePasswordVisibility() {
    const passwordInput = document.getElementById('password');
    const eyeIcon = document.getElementById('eye-icon');
    const eyeSlashIcon = document.getElementById('eye-slash-icon');

    if (passwordInput.type === 'password') {
        // Show password
        passwordInput.type = 'text';
        eyeIcon.classList.add('hidden');
        eyeSlashIcon.classList.remove('hidden');

        if (hidePasswordTimeout) {
            clearTimeout(hidePasswordTimeout);
        }

        hidePasswordTimeout = setTimeout(() => {
            passwordInput.type = 'password';
            eyeIcon.classList.remove('hidden');
            eyeSlashIcon.classList.add('hidden');
        }, 5000);
    } else {
        if (hidePasswordTimeout) {
            clearTimeout(hidePasswordTimeout);
        }
        passwordInput.type = 'password';
        eyeIcon.classList.remove('hidden');
        eyeSlashIcon.classList.add('hidden');
    }
}
