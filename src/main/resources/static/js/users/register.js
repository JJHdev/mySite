
$(document).ready(function() {
    $('.userForm').submit(function(event) {
        event.preventDefault(); // 폼 기본 제출 방지

        var formData = $(this).serialize(); // 폼 데이터 직렬화

        $.ajax({
            type: 'POST',
            url: $(this).attr('action'),
            data: formData,
            success: function(data) {
                console.log(data);
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

    $('#mail-Send-Btn').click(function(){
        const email = $('#email1').val() + $('#email2').val();

        $.ajax({
            type : 'post',
            url : contextPath + '/email/send',
            data: { email: email },
            success: function (data){
                $('#email_number').attr('disabled', false);
                $('#mail-Check-Btn').attr('disabled', false).show(); // 버튼 활성화 및 표시
                alert('인증번호가 전송되었습니다.');
            },
            error: function (xhr, status, error) {
                alert('이메일 전송에 실패했습니다: ' + error);
            }
        });
    });

    $('#mail-Check-Btn').click(function(){
        var email1 = $('#email1').val();
        var email2 = $('#email2').val();
        var email = email1 + email2;

        const code  = $('#email_number').val();

        $.ajax({
            type : 'post',
            url : contextPath + '/email/verify',
            data: { email: email, code :code},
            success: function (data){
                console.log(data);
                if (data.success) {

                    $('#email2_hidden').val(email2); // 선택한 값을 숨겨진 입력 필드에 설정
                    $('#email_number').attr('disabled', true);
                    $('#mail-Check-Btn').attr('disabled', true).hide(); // 버튼 비활성화 및 숨기기
                    $('#email1').attr('readonly', true);
                    $('#email2').attr('disabled', true); // select 요소 비활성화
                    $('#mail-Send-Btn').attr('disabled', true).text('인증완료');

                    alert('인증이 완료되었습니다.');
                } else {
                    alert(data.message); // 실패 메시지 표시
                }
            },
            error: function (xhr, status, error) {
                alert('이메일 전송에 실패했습니다: ' + error);
            }
        });
    });

});