var owner = null;
var currentDir = null;
var currentPrivilege = null;
var selectedButton = null;

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
	user.addEventListener('click', function(element) {
		viewFiles(user.id, "read");
	});
	user.appendChild(folderImgDiv);
	user.appendChild(textDiv);
	document.getElementById("topbar").appendChild(user);
}

function viewFiles(user, privilege="default") {
	currentDir = user;
	currentPrivilege = privilege;
	location.hash = user;
	
}

function setOwner(user){
	owner = user;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				if(location.hash != ""){					
					onHashChange();
					viewFiles(location.hash);
				}
			} else {
				logout();
			}
		}
	};
	xmlhttp.open("GET", "../ValidateController?user=" + owner, true);
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
	button.setAttribute('type','button');
	button.setAttribute('class', 'openfolderbutton');
	button.setAttribute('id', file);
	if (file.slice(-4) == ".txt") {
		var fileImg = document.createElement('img');
		fileImg.setAttribute('src', '../images/file.png');
		fileImg.setAttribute('alt', 'File Image');
		fileImg.setAttribute('width', '25px');
		fileImg.setAttribute('height', '25px');
		button.appendChild(fileImg);
		button.setAttribute('ondblclick', "openFile('"+file+"','"+privilege+"')");
	} else {
		var folderImg = document.createElement('img');
		folderImg.setAttribute('src', '../images/folder.png');
		folderImg.setAttribute('alt', 'Folder Image');
		folderImg.setAttribute('width', '25px');
		folderImg.setAttribute('height', '25px');
		button.appendChild(folderImg);
		button.setAttribute('ondblclick', "viewFiles('"+file+"','"+privilege+"')");
	}
	var btnText;
	if(file.indexOf('/') != -1){
		btnText = file.substring(file.lastIndexOf('/')+1);
	} else {
		btnText = file;
	}
	if (privilege != "default") {
		button.appendChild(document.createTextNode(" " + btnText + " (" + privilege
				+ ")"));
	} else {
		button.appendChild(document.createTextNode(" " + btnText));
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
	if (currentPrivilege != "read" && currentPrivilege != "write"){
		document.getElementById("selected").innerHTML = id.substring(id.lastIndexOf('/')+1);
		selectedButton = id;
		document.getElementById("buttonForm").style.display = "block";
		document.getElementById("buttonForm").style.left = event.clientX  + "px";
		document.getElementById("buttonForm").style.top = event.clientY  + "px";
	}
}

function windowRightClick(event){
	if(event.target.nodeName=="DIV" && (currentPrivilege=="write" || currentDir.startsWith(owner))){
		document.getElementById("containerForm").style.display = "block";
		document.getElementById("containerForm").style.left = event.clientX  + "px";
		document.getElementById("containerForm").style.top = event.clientY  + "px";
	}
}

function closeAll(){
	closeButtonForm();
	closeBoxForm();
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

function closeNewFileForm(){
	document.getElementById("newfileForm").style.display = "none";
}

function closeEditor(){
	document.getElementById("textarea").value = '';
	document.getElementById("editor").style.display = "none";
}

function closeShareFileForm(){
	document.getElementById("shareFileForm").style.display = "none";
}

function closeViewShareForm(){
	document.getElementById("viewShareForm").style.display = "none";
}

function deleteFile(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeButtonForm();
				onHashChange();
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../DeleteFileController?location=" + selectedButton, true);
	xmlhttp.send();
}

function newFolder(){
	closeBoxForm();
	document.getElementById("newfolderForm").style.display = "block";
}

function newFolderHandler(){
	var folderName = document.getElementById("foldername").value;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeNewFolderForm();
				onHashChange();
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../NewFolderController?location=" + currentDir +"&foldername=" + folderName, true);
	xmlhttp.send();
}

function newFile(){
	closeBoxForm();
	document.getElementById("newfileForm").style.display = "block";
}

var fileName;
function newFileHandler(){
	closeNewFileForm();
	fileName = document.getElementById("filename").value;
	document.getElementById("displayfilename").innerHTML = fileName+".txt";
	document.getElementById("editor").style.display = "block";
	document.getElementById("savebutton").style.display = "inline-block";
	document.getElementById("editbutton").style.display = "none";
	document.getElementById("textarea").disabled = false;
}

function submitFile(){
	var text = document.getElementById("textarea").value;
	var xmlhttp = new XMLHttpRequest();
	fileName = document.getElementById("displayfilename").innerHTML;
	fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.length);
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeEditor();
				onHashChange();
			} else {
				alert(successCheck.success);
			}
		}
	};
	if(document.getElementById("editbutton").style.display == "inline-block"){
		xmlhttp.open("POST", "../NewFileController?location=" + currentDir + "&filename=" + fileName + "&mode=edit", true);
	} else {		
		xmlhttp.open("POST", "../NewFileController?location=" + currentDir + "&filename=" + fileName + "&mode=new", true);
	}
	xmlhttp.send(text);
}

function openFile(fileName, privilege){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				document.getElementById("textarea").value = successCheck.content;
				document.getElementById("textarea").disabled = true;
				document.getElementById("savebutton").style.display = "none";
				if(privilege=="read"){
					fileName = fileName + " (Read only mode)";
				} else {
					document.getElementById("editbutton").style.display = "inline-block";
				}
				document.getElementById("displayfilename").innerHTML = fileName;
				document.getElementById("editor").style.display = "block";
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../OpenFileController?location=" + currentDir + "&filename=" + fileName, true);
	xmlhttp.send();
}

function editFile(){
	document.getElementById("textarea").disabled = false;
	document.getElementById("savebutton").style.display = "inline-block";
	document.getElementById("editbutton").style.opacity = "0.5";
}

function shareFile(){
	var readSelect = document.getElementById("readselect");
	while (readSelect.firstChild) {
		readSelect.removeChild(readSelect.firstChild);
	}
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var parcedJsonObject = JSON.parse(this.responseText);
			if(parcedJsonObject.success == "true"){
				closeButtonForm();
				document.getElementById("selectedFile").innerHTML = selectedButton.substring(selectedButton.lastIndexOf('/')+1);
				var option = document.createElement('option');
				parcedJsonObject.users.forEach(addOption);
				document.getElementById("shareFileForm").style.display = "block";
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../AllUserListController?location=" + selectedButton, true);
	xmlhttp.send();
}

function addOption(value){
	var option = document.createElement('option');
	option.appendChild(document.createTextNode(value));
	document.getElementById("readselect").appendChild(option);
}

function shareFileHandler(){
	var readSelect = document.getElementById("readselect");
	var getPrivilege;
	if (document.getElementById('read').checked) {
		getPrivilege = "read"
	} else {
		getPrivilege = "write"
	}
	var opt = [];
	for (var i=0, len=readSelect.options.length; i<len; i++) {
		if(readSelect.options[i].selected)
			opt.push(readSelect.options[i].value);
	}
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeShareFileForm();
			} else {
				alert(successCheck.success);
			}
		}
	};
	if(opt != ""){		
		xmlhttp.open("POST", "../ShareFileController?location=" + selectedButton + "&privilege=" + getPrivilege, true);
		xmlhttp.send(opt);
	} else {
		alert("No user selected");
	}
}

function viewShare(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var parsedJsonObj = JSON.parse(this.responseText);
			closeButtonForm();
			if(Object.keys(parsedJsonObj)[0] == "notshared"){
				alert("No Shared users");
				return;
			}
			document.getElementById("viewFile").innerHTML = selectedButton.substring(selectedButton.lastIndexOf('/')+1);
			var table = document.getElementById("table");
			while (table.firstChild) {
				table.removeChild(table.firstChild);
			}
			var trHeader = document.createElement('tr');
			var th1 = document.createElement('th');
			th1.appendChild(document.createTextNode("User"));
			var th2 = document.createElement('th');
			th2.appendChild(document.createTextNode("Privilege"));
			trHeader.appendChild(th1);
			trHeader.appendChild(th2);
			table.appendChild(trHeader);
			for (x in parsedJsonObj) {
				var editOption = document.createElement('button');
				var p = document.createElement('span');
				p.innerHTML = "&#8644;";
				editOption.appendChild(p);
				editOption.setAttribute('id',x);
				editOption.setAttribute('onclick', 'change(this.id)');
				editOption.setAttribute('type','button');
				editOption.setAttribute('class','editshare');
				var removeOption = document.createElement('button');
				var span = document.createElement('span');
				span.innerHTML = "&#10005;";
				removeOption.appendChild(span);
				removeOption.setAttribute('id',x);
				removeOption.setAttribute('onclick', 'removeShare(this.id)');
				removeOption.setAttribute('type','button');
				removeOption.setAttribute('class','removeshare');
				var tr = document.createElement('tr');
				var td1 = document.createElement('td');
				td1.appendChild(document.createTextNode(x));
				td1.setAttribute('align','center');
				tr.appendChild(td1);
				var td2 = document.createElement('td');
				td2.appendChild(document.createTextNode(parsedJsonObj[x] + " "));
				td2.appendChild(editOption);
				td2.appendChild(removeOption);
				td2.setAttribute('align','right');
				tr.appendChild(td2);
				table.appendChild(tr);
				document.getElementById("viewShareForm").style.display = "block";
			}
		}
	};
	xmlhttp.open("GET", "../ViewShareController?location=" + selectedButton, true);
	xmlhttp.send();
}

function removeShare(sharedUser){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeViewShareForm();
				viewShare();
			} else {
					alert(successCheck.success);
				}
		}
	};
	xmlhttp.open("GET", "../RemoveShareController?user=" + sharedUser +"&location=" + selectedButton, true);
	xmlhttp.send();
}

function change(sharedUser){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				closeViewShareForm();
				viewShare();
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("POST", "../ChangePrivilegeController?user=" + sharedUser +"&location=" + selectedButton, true);
	xmlhttp.send();
}

function logout(){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			window.location.replace("../index.jsp");
		}
	};
	xmlhttp.open("GET", "../LogoutController", true);
	xmlhttp.send();
}

function onHashChange(){
	window.scrollTo(0, 0);
	var xmlhttp = new XMLHttpRequest();
	var hashValue = window.location.hash.substring(1);
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("dispdir").innerHTML = hashValue;
			var myfolderlist = document.getElementById("myfolderlist");
			while (myfolderlist.firstChild) {
				myfolderlist.removeChild(myfolderlist.firstChild);
			}
			var parsedJsonObj = JSON.parse(this.responseText);
			if(parsedJsonObj.success != null){
				logout();
			}
			for (x in parsedJsonObj) {
				openFolder(x, parsedJsonObj[x]);
			}
		}
	};
	if (hashValue.indexOf('/') == -1){
		console.log('user');
		xmlhttp.open("GET", "../ViewFolderController?shareduser=" + hashValue, true);
	} else {
		console.log('location');
		xmlhttp.open("GET", "../ViewFolderForLocationController?location=" + hashValue, true);
	}
	xmlhttp.send();
}