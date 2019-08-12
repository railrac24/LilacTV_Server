

//----------itemList
function submitForm() {
    document.getElementById("updateList").submit();
}

//----------login

// var rmCheck = document.getElementById("rememberMe"),
//     emailInput = document.getElementById("inputEmail");
//
// if (!(localStorage.checkbox && localStorage.checkbox != "")) {
//     rmCheck.removeAttribute("checked");
//     emailInput.value = "";
// } else {
//     rmCheck.setAttribute("checked", "checked");
//     emailInput.value = localStorage.username;
// }
//
// function lsRememberMe() {
//     if (rmCheck.checked && emailInput.value != "") {
//         localStorage.username = emailInput.value;
//         localStorage.checkbox = rmCheck.value;
//     } else {
//         localStorage.username = "";
//         localStorage.checkbox = "";
//     }
// }

//----------register
function check_pass() {
    if (document.getElementById('inputPassword').value == document.getElementById('confirmPassword').value) {
        document.getElementById('message').style.color = 'green';
        document.getElementById('message').innerHTML = 'matching';
        document.getElementById('submit').disabled = false;
    } else {
        document.getElementById('message').style.color = 'red';
        document.getElementById('message').innerHTML = 'not matching';
        document.getElementById('submit').disabled = true;
    }
}

function getLilacTVChecked() {
    var checkBox = document.getElementById("isLilacTVChecked");
    if (checkBox.checked == true) {
        document.getElementById("productID").style.display = "block";
        document.getElementById('whereID').style.color = 'darkgray';
        document.getElementById('whereID').innerHTML = '라일락TV의 [설정]메뉴 > 서브메뉴(왼쪽메뉴) > [제품정보]에서 확인가능';
        document.getElementById('whereID').style.display = "block";
    }
    else {
        document.getElementById("productID").style.display = "none";
        document.getElementById('whereID').style.display = "none";
    }
}

//----------updateUser
function getPasswordChecked() {
    const checkBox = document.getElementById("isPasswordChecked");
    if (checkBox.checked === true) {
        document.getElementById("inputPassword").style.display = "block";
        document.getElementById("labelPassword").style.display = "block";
        document.getElementById("confirmPassword").style.display = "block";
        document.getElementById("labelConfirmPassword").style.display = "block";
        document.getElementById('submit').disabled = true;
    }
    else {
        document.getElementById("inputPassword").style.display = "none";
        document.getElementById("labelPassword").style.display = "none";
        document.getElementById("confirmPassword").style.display = "none";
        document.getElementById("labelConfirmPassword").style.display = "none";
        document.getElementById("message").style.display = "none";
        document.getElementById('submit').disabled = false;
    }
}

//------------userList
function confirmDelete(id) {
    if (id === 1) {
        alert("관리자 정보는 삭제할 수 없습니다.");
    } else if (confirm("선택된 사용자 정보를 삭제 합니다. 계속합니까?")) {
        // 확인 버튼 클릭 시 동작
        window.location="/admin/"+id+"/delete";
    }
}

function editUser(id) {
    if (id === 1) {
        alert("관리자 정보는 수정할 수 없습니다.");
    } else {
        window.location="/admin/"+id+"/form";
    }
}