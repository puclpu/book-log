var pw_checked = 0;
var nickname_checked = 1;

/* password check */
function pw_check() {
	var pw = document.getElementById("password").value;

	if (pw.length === 0) {
		document.getElementById("password_rule_span").innerHTML = "";
		pw_checked = 1;
	} else if (pw.length < 8 || pw.length > 16) {
		document.getElementById("password_rule_span").innerHTML = "8~16자 영문/숫자/특수문자 조합만 가능합니다.";
		pw_checked = 0;
	} else {
		document.getElementById("password_rule_span").innerHTML = "";
	}
}

function pw_same() {
	var pw = document.getElementById("password").value;
	var pw2 = document.getElementById("password_check").value;
	if (pw == pw2) {
		document.getElementById("password_same_span").innerHTML = "";
		pw_checked = 1;
	} else {
		document.getElementById("password_same_span").innerHTML = "비밀번호가 일치하지 않습니다.";
		pw_checked = 0;
	}
}

/* nickname check */
function nickname_check() {
	var nickname = document.getElementById("nickname").value;

	if (nickname)

		if (nickname.trim() === "") { /* 입력값이 없으면 return */
			document.getElementById("nickname_check_span").innerHTML = "";
			nickname_checked = 0;
			return;
		}

	if (nickname.length < 2) { /* 2글자 이하면 return */
		document.getElementById("nickname_check_span").innerHTML = "2글자 이상의 닉네임을 입력하세요.";
		nickname_checked = 0;
		return;
	}

	$.ajax({
		type: 'POST',
		url: "/signup/nicknamecheck",
		data: { nickname: nickname },
		success: function(response) {
			if (response.data < 0) {
				document.getElementById("nickname_check_span").innerHTML = "사용 가능한 닉네임입니다.";
				nickname_checked = 1;
			} else {
				document.getElementById("nickname_check_span").innerHTML = "이미 사용 중인 닉네임입니다.";
				nickname_checked = 0;
			}
		},
		error: function(request, error) {
			alert("fail");
			alert("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
		}
	})/* ajax */
}

/* check before form submit */
function check() {
	if (pw_checked == 0) {
		alert("비밀번호를 입력해주세요.");
		return false;
	} else if (nickname_checked == 0) {
		alert("닉네임을 입력해주세요.");
		return false;
	} else if (email_checked == "") {
		alert("이메일을 입력해주세요.");
		return false;
	} else {
		return true;
	}
}