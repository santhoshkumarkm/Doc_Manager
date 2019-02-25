var prevDir;

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
	user.appendChild(folderImgDiv);
	user.appendChild(textDiv);
	document.getElementById("topbar").appendChild(user);
}

function viewFiles(user, privilege="default") {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			prevDir = user;
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
	var button = document.createElement('button');
	if (file.slice(-4) == ".txt") {
		var fileImg = document.createElement('img');
		fileImg.setAttribute('src', '../images/file.png');
		fileImg.setAttribute('alt', 'File Image');
		fileImg.setAttribute('class', 'file');
		button.appendChild(fileImg);
	} else {
		var folderImg = document.createElement('img');
		folderImg.setAttribute('src', '../images/folder.png');
		folderImg.setAttribute('alt', 'Folder Image');
		folderImg.setAttribute('class', 'folder');
		button.appendChild(folderImg);
		item.setAttribute('ondblclick', "viewFiles('"+file+"','"+privilege+"')");
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

function goBack(rootUser){
	if(!prevDir.startsWith(rootUser)){
		validateURL(prevDir.substring(0,prevDir.lastIndexOf('/')), rootUser);
	}
	else if(prevDir.length > rootUser.length){
		viewFiles(prevDir.substring(0,prevDir.lastIndexOf('/')),'owner');		
	}
}

function validateURL(url, rootUser){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var parsedJsonObj = JSON.parse(this.responseText);
		}
	};
	xmlhttp.open("GET", "../ValidateUrlController?rootuser=" + rootUser, true);
	xmlhttp.send();
}