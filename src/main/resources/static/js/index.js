document.addEventListener('DOMContentLoaded', function () {

    if(false) {
        $.ajax({
            type: 'GET',
            url: contextPath + '/charts', // 예시: 사용자 프로필 요청
            headers: {
                'Authorization': 'Bearer ' + token // JWT 토큰 포함
            },
            success: function (data) {
                console.log('User profile:', data);
                // 사용자 프로필 정보 표시 로직 추가
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert('Failed to fetch user profile. Please try again.');
                console.error('Error:', errorThrown);
                window.location.href = '/user/login'; // 인증 실패 시 로그인 페이지로 리다이렉트
            }
        });
    }

});