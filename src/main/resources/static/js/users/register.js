
$(document).ready(function() {
    $('.userForm').submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var formData = $(this).serialize(); // 폼 데이터 직렬화

        $.ajax({
            type: 'POST',
            url: $(this).attr('action'),
            data: formData,
            success: function(data) {

                if (data.success) {
                    window.location.href = data.redirect; // 페이지 이동
                } else {
                    alert(data.message); // 실패 메시지 표시
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert('Registration failed. Please try again.');
                console.error('Error:', errorThrown);
            }
        });
    });
});