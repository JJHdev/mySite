$(document).ready(function() {
    $('#mail-Send-Btn').click(function(){
        const email = $('#email1').val() + $('#email2').val();
        let userId = $('#userId').val();
        let userName = $('#userName').val();

        $.ajax({
            type : 'post',
            url : contextPath + '/email/findSend',
            data: JSON.stringify({ email: email , userId: userId , userName: userName}),
            contentType: 'application/json',
            success: function (data){
                $('#email_number').attr('disabled', false);
                $('#mail-Check-Btn').attr('disabled', false).show(); // 버튼 활성화 및 표시
                alert(data.message);
            },
            error: function (xhr, status, error) {
                alert('이메일 전송에 실패했습니다: ' + error);
            }
        });
    });


    $('#mail-Check-Btn').click(function(){
        let email1 = $('#email1').val();
        let email2 = $('#email2').val();
        let email = email1 + email2;
        let userId = $('#userId').val();
        let userName = $('#userName').val();

        const code  = $('#email_number').val();

        $.ajax({
            type : 'post',
            url : contextPath + '/email/findVerify',
            data: { email: email, code :code , userId: userId , userName: userName},
            success: function (data){
                if (data.success) {
                    $('#email_number').attr('disabled', true);
                    $('#mail-Check-Btn').attr('disabled', true).hide(); // 버튼 비활성화 및 숨기기
                    $('#email1').attr('readonly', true);
                    $('#email2').attr('disabled', true); // select 요소 비활성화
                    $('#mail-Send-Btn').attr('disabled', true).text('인증완료');

                    alert('초기화된 비밀번호는 ',data.message);
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