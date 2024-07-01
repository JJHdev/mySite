async function sha256(message){
    const msgBuffer = new TextEncoder().encode(message);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

$(document).ready(function() {
    $('.userForm').submit(async function (event) {
        event.preventDefault(); // 폼 기본 제출 방지

        const passwordField = $('#password');
        const password = passwordField.val();
        const hashedPassword = await sha256(password);
        passwordField.val(hashedPassword); // 비밀번호 필드에 해시된 비밀번호 설정

        let formData = $(this).serialize(); // 폼 데이터 직렬화

        console.log(formData);

        $.ajax({
            type: 'POST',
            url: $(this).attr('action'),
            data: formData,
            success: function (data) {
                if (data.success) {
                    localStorage.setItem('jwtToken', data.jwtToken); // JWT 토큰 저장
                    window.location.href = data.redirect; // 페이지 이동
                } else {
                    alert(data.message); // 실패 메시지 표시
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('Registration failed. Please try again.');
                console.error('Error:', errorThrown);
            }
        });
    });


});