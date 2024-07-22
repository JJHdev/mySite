document.addEventListener('DOMContentLoaded', function () {
    let accessToken  = localStorage.getItem('accessToken');
    console.log(accessToken);

    if (!accessToken) {
        // Access Token이 없는 경우 Refresh Token을 사용하여 새로운 Access Token 발급 시도
        refreshAccessToken()
            .then(newAccessToken => {
                accessToken = newAccessToken;
                // 새로운 Access Token을 사용하여 필요한 API 요청 수행
                //fetchUserProfile(accessToken);
            })
            .catch(error => {
                console.error('Failed to refresh access token:', error);
                alert('No valid token found, redirecting to login page.');
            });
    } else {
        // Access Token이 있는 경우 API 요청 수행
        //fetchUserProfile(accessToken);
    }


    function refreshAccessToken() {
        return fetch('/refresh-token', {
            method: 'POST',
            credentials: 'include' // 쿠키를 전송하기 위해 설정
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to refresh access token');
                }
                return response.json();
            })
            .then(data => {
                const newAccessToken = data.accessToken;
                // 새로운 Access Token을 로컬 스토리지에 저장
                localStorage.setItem('accessToken', newAccessToken);
                return newAccessToken;
            })
            .catch(error => {
                console.error('Error:', error);
                // 필요 시 추가 처리 (예: 로그인 페이지로 리다이렉트)
                throw error;
            });
    }

    /*if(true) {
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
    }*/
});