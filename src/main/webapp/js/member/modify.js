
$("#inputPassword,#inputPasswordCheck").keyup(function () {
	const pw1 = $("#inputPassword").val();
	const pw2 = $("#inputPasswordCheck").val();
	if(pw1===pw2){
		$("#updateSubmit").removeClass("disabled");
		$("#passwordCollect").removeClass("d-none");
		$("#passwordWrong").addClass("d-none");
	}else{
		$("#updateSubmit").addClass("disabled");
		$("#passwordCollect").addClass("d-none");
		$("#passwordWrong").removeClass("d-none");
	}
});