document.addEventListener('DOMContentLoaded', function() {
    const jwtToken = getJwtTokenCookie();
    if (jwtToken) {
        window.location.href = "/tasks";
    }
});

function getJwtTokenCookie() {
    let cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.startsWith('JWT_TOKEN' + '=')) {
            return cookie.substring('JWT_TOKEN'.length + 1);
        }
    }
    return null;
}