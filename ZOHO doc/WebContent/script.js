var currentDir=null;
var currentPrivilege=null;
var selectedButton=null;

var folderImg = document.createElement('img');
folderImg.setAttribute('src', '../images/folder.png');
folderImg.setAttribute('alt', 'Folder Image');
folderImg.setAttribute('class', 'icon');
var folderImgDiv = document.createElement('div');
folderImgDiv.appendChild(folderImg);

var fileImg = document.createElement('img');
fileImg.setAttribute('src', '../images/file.png');
fileImg.setAttribute('alt', 'File Image');
fileImg.setAttribute('class', 'icon');
var fileImgDiv = document.createElement('div');
fileImgDiv.appendChild(fileImg);

function getSharedUsers() {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var userListObj = JSON.parse(this.responseText);
			var userList = userListObj.userList;
			if (userList.length == 0) {
				alert("no shared files");
			}
			userList.forEach(userListTraverse);
		}
	};
	xmlhttp.open("GET", "../SharedUserListController", true);
	xmlhttp.send();
}

function userListTraverse(value) {
	var textDiv = document.createElement('div');
	textDiv.setAttribute('class', 'text');
	textDiv.appendChild(document.createTextNode(value + " " + "(shared)"));
	var user = document.createElement('div');
	user.setAttribute('class', 'container');
	user.setAttribute('id', value);
	user.setAttribute('onclick', 'viewFiles(id)');
	user.addEventListener('click', function(element) {
		viewFiles(id);
	});
	user.appendChild(folderImgDiv);
	user.appendChild(textDiv);
	document.getElementById("topbar").appendChild(user);
}

function viewFiles(user, privilege="default") {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
	currentDir = user;
	currentPrivilege = privilege;
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("dispdir").innerHTML = user;
			var myfolderlist = document.getElementById("myfolderlist");
			while (myfolderlist.firstChild) {
				myfolderlist.removeChild(myfolderlist.firstChild);
			}
			var parsedJsonObj = JSON.parse(this.responseText);
			for (x in parsedJsonObj) {
				openFolder(x, parsedJsonObj[x]);
			}
		}
	};
	if(privilege=="default"){
		xmlhttp.open("GET", "../ViewFolderController?shareduser=" + user, true);
	} else {
		xmlhttp.open("GET", "../ViewFolderForLocationController?location=" + user + "&privilege=" + privilege, true);
	}
	xmlhttp.send();
}

function openFolder(file, privilege) {
	var item = document.createElement('li');
	item.setAttribute('class', 'list');
	var button = document.createElement('button');
	button.addEventListener('contextmenu', function(element) {
		element.preventDefault();
	    buttonRightClick(event, this.id);
	});
	button.setAttribute('class', 'openfolderbutton');
	button.setAttribute('id', file);
	if (file.slice(-4) == ".txt") {
		var fileImg = document.createElement('img');
		fileImg.setAttribute('src', '../images/file.png');
		fileImg.setAttribute('alt', 'File Image');
		fileImg.setAttribute('width', '25px');
		fileImg.setAttribute('height', '25px');
		button.appendChild(fileImg);
	} else {
		var folderImg = document.createElement('img');
		folderImg.setAttribute('src', '../images/folder.png');
		folderImg.setAttribute('alt', 'Folder Image');
		folderImg.setAttribute('width', '25px');
		folderImg.setAttribute('height', '25px');
		button.appendChild(folderImg);
		button.setAttribute('ondblclick', "viewFiles('"+file+"','"+privilege+"')");
	}
	if (privilege != "owner") {
		button.appendChild(document.createTextNode(" " + file + " (" + privilege
				+ ")"));
	} else {
		button.appendChild(document.createTextNode(" " + file));
	}
	item.appendChild(button);
	document.getElementById("myfolderlist").appendChild(item);
}

function goBack(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var accessCheck = JSON.parse(this.responseText);
			if(accessCheck.access != "denied"){		
				viewFiles(accessCheck.prevLocation, accessCheck.access);
			}
		}
	};
	xmlhttp.open("GET", "../GoBackController?location=" + currentDir, true);
	xmlhttp.send();
}

function setBoxRightClick(){
	document.getElementById("viewbox").addEventListener('contextmenu', function(element) {
		element.preventDefault();
	    windowRightClick(event);
	});
}

function buttonRightClick(event, id){
	if (currentPrivilege=="owner"){
		document.getElementById("selected").innerHTML = id;
		selectedButton = id;
		document.getElementById("buttonForm").style.display = "block";
		document.getElementById("buttonForm").style.left = event.clientX  + "px";
		document.getElementById("buttonForm").style.top = event.clientY  + "px";
	}
}

function windowRightClick(event){
	if(event.target.nodeName=="DIV" && (currentPrivilege=="owner" || currentPrivilege=="write")){
		document.getElementById("containerForm").style.display = "block";
		document.getElementById("containerForm").style.left = event.clientX  + "px";
		document.getElementById("containerForm").style.top = event.clientY  + "px";
	}
}

function closeButtonForm(){
	document.getElementById("buttonForm").style.display = "none";
}

function closeBoxForm(){
	document.getElementById("containerForm").style.display = "none";
}

function closeNewFolderForm(){
	document.getElementById("newfolderForm").style.display = "none";
}

function newFolder(){
	closeBoxForm();
	document.getElementById("newfolderForm").style.display = "block";
}

function deleteFile(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeButtonForm();
				viewFiles(currentDir, currentPrivilege);
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../DeleteFileController?location=" + selectedButton, true);
	xmlhttp.send();
}

function shareFile(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeButtonForm();
				viewFiles(currentDir, currentPrivilege);
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../ShareFileController?location=" + selectedButton, true);
	xmlhttp.send();
}

function newFolderHandler(){
	var folderName = document.getElementById("foldername").value;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeNewFolderForm();
				viewFiles(currentDir, currentPrivilege);
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../NewFolderController?location=" + currentDir +"&foldername=" + folderName, true);
	xmlhttp.send();
}

function logout(){
	var folderName = document.getElementById("foldername").value;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			alert("Logout successful");
			window.location.replace("../index.jsp")
		}
	};
	xmlhttp.open("GET", "../LogoutController", true);
	xmlhttp.send();
}