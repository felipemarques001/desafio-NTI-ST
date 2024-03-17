function updateCompletedField(element) {
    if (confirm('Tem certeza que quer marcar a tarefa como feita?')) {
        let taskId = element.getAttribute('taskId');
        window.location.href = "/tasks/update_completed_field?id=" + taskId;
    }
}

function deleteJwtTokenCookie() {
    const expirationDate = new Date(0);
    document.cookie = 'JWT_TOKEN=; expires=' + expirationDate.toUTCString() + '; path=/';
}