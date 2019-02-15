function navigate(element) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("mySpace").innerHTML = this.responseText;
		}
	};
	xmlhttp.open("GET", "../navigationcontroller.jsp?option=" + element.value,
			true);
	xmlhttp.send();
}