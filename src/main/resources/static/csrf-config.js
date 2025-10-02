/**
 * Global CSRF protection configuration for HTMX requests
 * Automatically includes CSRF tokens in all AJAX requests
 */
document.addEventListener('DOMContentLoaded', function() {
    // Get CSRF token and header name from meta tags
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

    if (csrfToken && csrfHeader) {
        // Configure HTMX to include CSRF token in all requests
        document.body.addEventListener('htmx:configRequest', function(evt) {
            evt.detail.headers[csrfHeader] = csrfToken;
        });

        console.log('CSRF protection configured for HTMX requests');
    } else {
        console.warn('CSRF meta tags not found - CSRF protection may not work correctly');
    }

    // Also configure for regular form submissions (non-HTMX)
    document.addEventListener('submit', function(evt) {
        const form = evt.target;

        // Skip if form already has CSRF token or is a GET request
        if (form.method.toLowerCase() === 'get' ||
            form.querySelector('input[name="_csrf"]') ||
            form.querySelector('input[name="' + (csrfHeader || '').replace('X-', '').toLowerCase() + '"]')) {
            return;
        }

        // Add CSRF token to form if missing
        if (csrfToken && csrfHeader) {
            const csrfInput = document.createElement('input');
            csrfInput.type = 'hidden';
            csrfInput.name = '_csrf';
            csrfInput.value = csrfToken;
            form.appendChild(csrfInput);
        }
    });
});