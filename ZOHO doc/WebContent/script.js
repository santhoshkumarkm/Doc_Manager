var owner = null;
var currentDir = null;
var currentPrivilege = null;
var selectedButton = null;
var allUserList = [];

var commonWords = ["the", "and", "that", "have", "for", "not", "with", "you", "this", "but", "his", "from", "they", "her", "she", "will", "would", "there", "their", "your", "could", "also"];

function checkUser(){
	var userName = document.getElementById("userName").value;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var responseObj = JSON.parse(this.responseText);
			if(responseObj.success == "true"){
				document.getElementById("successCheck").innerHTML = " (Available)"
			} else {
				document.getElementById("successCheck").innerHTML = " (User Name not available)"
			}
		}
	};
	xmlhttp.open("GET", "../DuplicateUserController?username="+userName, true);
	xmlhttp.send();
}

function submitSignUpForm(){
	var password = document.getElementById("password").value;
	var confirmPassword = document.getElementById("confirmPassword").value;
	if(document.getElementById("userName").value.length >=3 && document.getElementById("successCheck").innerHTML== " (Available)" && password.length >= 6 && password == confirmPassword){		
		document.getElementById("signUpForm").submit();
	} else{
		alert("Please fill details correctly");
	}
}

function submitLoginForm(){
	var password = document.getElementById("password").value;
	if(document.getElementById("username").value.length >=3 && password.length >= 6 ){		
		document.getElementById("loginForm").submit();
	} else{
		alert("Please fill details correctly");
	}
}

function getSharedUsers(rootUser) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var userListObj = JSON.parse(this.responseText);
			var userList = userListObj.userList;
			allUserList.push(rootUser);
			userList.forEach(userListTraverse);
		}
	};
	xmlhttp.open("GET", "../SharedUserListController", true);
	xmlhttp.send();
}

function userListTraverse(value) {
	allUserList.push(value);
	var textDiv = document.createElement('div');
	textDiv.setAttribute('class', 'text');
	textDiv.appendChild(document.createTextNode(value + " " + "(shared)"));
	var user = document.createElement('div');
	user.setAttribute('class', 'container');
	user.setAttribute('id', value);
	user.addEventListener('click', function(element) {
		viewFiles(user.id, "read");
	});
	var folderImg = document.createElement('img');
	folderImg.setAttribute('src', '../images/folder.png');
	folderImg.setAttribute('alt', 'Folder Image');
	folderImg.setAttribute('class', 'icon');
	var folderImgDiv = document.createElement('div');
	folderImgDiv.appendChild(folderImg);
	user.appendChild(folderImgDiv);
	user.appendChild(textDiv);
	document.getElementById("topbar").appendChild(user);
}

function viewFiles(user, privilege="default") {
	currentDir = user;
	currentPrivilege = privilege;
	var flag = false;
	if(decodeURI(location.hash).substring(1) == user){
		flag = true;
	}
	location.hash = user;
	for (var i = 0; i < allUserList.length; i++) {
		document.getElementById(allUserList[i]).style.opacity = "0.5";
	}
	if(flag){
		onHashChange();
	}
	
}

function setOwner(user){
	if(user == "null"){
		logout();
	}
	owner = user;
	var currentLocation = decodeURI(location.hash).substring(1);
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if(successCheck.success == "true"){
				if(decodeURI(location.hash) != ""){
					onHashChange();
						viewFiles(currentLocation, successCheck.privilege);
				}
			} else {
				logout();
			}
		}
	};
	xmlhttp.open("GET", "../ValidateController?location=" + currentLocation, true);
	xmlhttp.send();
}

function openFolder(file, privilege) {
	if(file=="success" && privilege=="logout"){
		logout();
	}
	var item = document.createElement('li');
	item.setAttribute('class', 'list');
	var button = document.createElement('button');
	button.addEventListener('contextmenu', function(element) {
		element.preventDefault();
		buttonRightClick(event, this.id);
	});
	button.setAttribute('type', 'button');
	button.setAttribute('class', 'openfolderbutton');
	button.setAttribute('id', file);
	if (file.slice(-4) == ".txt") {
		var fileImg = document.createElement('img');
		fileImg.setAttribute('src', '../images/file.png');
		fileImg.setAttribute('alt', 'File Image');
		fileImg.setAttribute('width', '25px');
		fileImg.setAttribute('height', '25px');
		button.appendChild(fileImg);
		button.setAttribute('ondblclick', "openFile('" + file + "')");
	} else {
		var folderImg = document.createElement('img');
		folderImg.setAttribute('src', '../images/folder.png');
		folderImg.setAttribute('alt', 'Folder Image');
		folderImg.setAttribute('width', '25px');
		folderImg.setAttribute('height', '25px');
		button.appendChild(folderImg);
		button.setAttribute('ondblclick', "viewFiles('" + file + "','"
				+ privilege + "')");
	}
	var btnText;
	if (file.indexOf('/') != -1) {
		btnText = file.substring(file.lastIndexOf('/') + 1);
	} else {
		btnText = file;
	}
	if (privilege != "owner") {
		button.appendChild(document.createTextNode(" " + btnText + " ("
				+ privilege + ")"));
	} else {
		button.appendChild(document.createTextNode(" " + btnText));
	}
	item.appendChild(button);
	document.getElementById("myfolderlist").appendChild(item);
}

function goBack() {
	closeBoxForm();
	closeButtonForm();
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var accessCheck = JSON.parse(this.responseText);
			if (accessCheck.access != "denied") {
// alert(accessCheck.prevLocation+"@"+ accessCheck.access)
				currentPrivilege = accessCheck.access;
				viewFiles(accessCheck.prevLocation, accessCheck.access);
			}
		}
	};
	xmlhttp.open("GET", "../GoBackController?location=" + location.hash.substring(1), true);
	xmlhttp.send();
}

function setBoxRightClick() {
	document.getElementById("viewbox").addEventListener('contextmenu',
			function(element) {
				element.preventDefault();
				windowRightClick(event);
			});
}

function buttonRightClick(event, id) {
	closeAll();
	if (currentPrivilege != "read" && currentPrivilege != "write") {
		document.getElementById("selected").innerHTML = id.substring(id
				.lastIndexOf('/') + 1);
		selectedButton = id;
		document.getElementById("buttonForm").style.display = "block";
		document.getElementById("buttonForm").style.left = event.clientX + "px";
		document.getElementById("buttonForm").style.top = event.clientY + "px";
	}
}

function windowRightClick(event) {
	if (event.target.nodeName == "DIV"
			&& (currentPrivilege == "owner" || currentPrivilege == "write")) {
		closeAll();
		document.getElementById("containerForm").style.display = "block";
		document.getElementById("containerForm").style.left = event.clientX
				+ "px";
		document.getElementById("containerForm").style.top = event.clientY
				+ "px";
	}
}

function closeAll() {
	closeButtonForm();
	closeBoxForm();
	closeSearchMenu();
	var text = document.getElementById("searchText");
	text.value = "";
}

function closeButtonForm() {
	document.getElementById("buttonForm").style.display = "none";
}

function closeBoxForm() {
	document.getElementById("containerForm").style.display = "none";
}

function closeNewFolderForm() {
	document.getElementById("newfolderForm").style.display = "none";
	document.getElementById("foldername").value = "";
}

function closeNewFileForm() {
	document.getElementById("newfileForm").style.display = "none";
	document.getElementById("filename").value = "";
}

function closeEditor() {
	document.getElementById("textarea").value = '';
	document.getElementById("editor").style.display = "none";
}

function closeShareFileForm() {
	document.getElementById("shareFileForm").style.display = "none";
}

function closeViewShareForm() {
	document.getElementById("viewShareForm").style.display = "none";
}

function closeSearchMenu() {
	document.getElementById("searchmenu").style.display = "none";
}

function deleteFile() {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeButtonForm();
				onHashChange();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("POST", "../DeleteFileController?location=" + selectedButton,
			true);
	xmlhttp.send();
}

function newFolder() {
	closeBoxForm();
	document.getElementById("newfolderForm").style.display = "block";
}

function newFolderHandler() {
	var folderName = document.getElementById("foldername").value;
	if(folderName == ""){
		alert("Name Empty");
		return;
	}
	if(folderName.indexOf('/') != -1){
		alert("Invalid name");
		return;
	}
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeNewFolderForm();
				onHashChange();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("POST", "../NewFolderController?location=" + location.hash.substring(1)
			+ "&foldername=" + folderName, true);
	xmlhttp.send();
}

function newFile() {
	closeBoxForm();
	document.getElementById("newfileForm").style.display = "block";
}

var fileName;
function newFileHandler() {
	fileName = document.getElementById("filename").value;
	if(fileName == ""){
		alert("Name Empty");
		return;
	}
	if(fileName.indexOf('/') != -1){
		alert("Invalid name");
		return;
	}
	closeNewFileForm();

	document.getElementById("displayfilename").innerHTML = decodeURI(location.hash).substring(1)+"/"+fileName + ".txt";
	document.getElementById("editor").style.display = "block";
	document.getElementById("savebutton").style.display = "inline-block";
	document.getElementById("editbutton").style.display = "none";
	document.getElementById("textarea").disabled = false;
}

function submitFile() {
	var text = document.getElementById("textarea").value;
	var xmlhttp = new XMLHttpRequest();
	fileName = document.getElementById("displayfilename").innerHTML;
// fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.length);
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeEditor();
				onHashChange();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	if (document.getElementById("editbutton").style.display == "inline-block") {
		xmlhttp.open("POST", "../NewFileController?location=" + location.hash.substring(1)
				+ "&filename=" + fileName + "&mode=edit", true);
	} else {
		xmlhttp.open("POST", "../NewFileController?location=" + location.hash.substring(1)
				+ "&filename=" + fileName + "&mode=new", true);
	}
	xmlhttp.send(text);
}

function openFile(fileName) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				document.getElementById("textarea").value = successCheck.content;
				document.getElementById("textarea").disabled = true;
				document.getElementById("savebutton").style.display = "none";
				if (successCheck.privilege == "read") {
					fileName = fileName + " (Read only mode)";
					document.getElementById("editbutton").style.display = "none";
				} else {
					document.getElementById("editbutton").style.display = "inline-block";
					document.getElementById("editbutton").style.opacity = "1";
				}
				document.getElementById("displayfilename").innerHTML = fileName;
				document.getElementById("editor").style.display = "block";
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../OpenFileController?location=" + location.hash.substring(1)
			+ "&filename=" + fileName, true);
	xmlhttp.send();
}

function editFile() {
	document.getElementById("textarea").disabled = false;
	document.getElementById("savebutton").style.display = "inline-block";
	document.getElementById("editbutton").style.opacity = "0.5";
}

function shareFile() {
	var readSelect = document.getElementById("readselect");
	while (readSelect.firstChild) {
		readSelect.removeChild(readSelect.firstChild);
	}
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var parcedJsonObject = JSON.parse(this.responseText);
			if (parcedJsonObject.success == "true") {
				closeButtonForm();
				document.getElementById("selectedFile").innerHTML = selectedButton
						.substring(selectedButton.lastIndexOf('/') + 1);
				var option = document.createElement('option');
				parcedJsonObject.users.forEach(addOption);
				document.getElementById("shareFileForm").style.display = "block";
			} else if (parcedJsonObject.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("GET", "../AllUserListController?location=" + selectedButton,
			true);
	xmlhttp.send();
}

function addOption(value) {
	var option = document.createElement('option');
	option.appendChild(document.createTextNode(value));
	document.getElementById("readselect").appendChild(option);
}

function shareFileHandler() {
	var readSelect = document.getElementById("readselect");
	var getPrivilege;
	if (document.getElementById('read').checked) {
		getPrivilege = "read"
	} else {
		getPrivilege = "write"
	}
	var opt = [];
	for (var i = 0, len = readSelect.options.length; i < len; i++) {
		if (readSelect.options[i].selected)
			opt.push(readSelect.options[i].value);
	}
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeShareFileForm();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	if (opt != "") {
		xmlhttp.open("POST", "../ShareFileController?location="
				+ selectedButton + "&privilege=" + getPrivilege, true);
		xmlhttp.send(opt);
	} else {
		alert("No user selected");
	}
}

function viewShare() {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var parsedJsonObj = JSON.parse(this.responseText);
			closeButtonForm();
			if(parsedJsonObj.success == "ERROR"){
				alert(parsedJsonObj.success);
				return;
			}
			if (Object.keys(parsedJsonObj)[0] == "notshared") {
				alert("No Shared users");
				return;
			}
			document.getElementById("viewFile").innerHTML = selectedButton
					.substring(selectedButton.lastIndexOf('/') + 1);
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
				if(x == "success"){
					continue;
				}
				var editOption = document.createElement('button');
				var p = document.createElement('span');
				p.innerHTML = "&#8644;";
				editOption.appendChild(p);
				editOption.setAttribute('id', x);
				editOption.setAttribute('onclick', 'change(this.id)');
				editOption.setAttribute('type', 'button');
				editOption.setAttribute('class', 'editshare');
				var removeOption = document.createElement('button');
				var span = document.createElement('span');
				span.innerHTML = "&#10005;";
				removeOption.appendChild(span);
				removeOption.setAttribute('id', x);
				removeOption.setAttribute('onclick', 'removeShare(this.id)');
				removeOption.setAttribute('type', 'button');
				removeOption.setAttribute('class', 'removeshare');
				var tr = document.createElement('tr');
				var td1 = document.createElement('td');
				td1.appendChild(document.createTextNode(x));
				td1.setAttribute('align', 'center');
				tr.appendChild(td1);
				var td2 = document.createElement('td');
				td2
						.appendChild(document.createTextNode(parsedJsonObj[x]
								+ " "));
				td2.appendChild(editOption);
				td2.appendChild(removeOption);
				td2.setAttribute('align', 'right');
				tr.appendChild(td2);
				table.appendChild(tr);
				document.getElementById("viewShareForm").style.display = "block";
			}
		}
	};
	xmlhttp.open("GET", "../ViewShareController?location=" + selectedButton,
			true);
	xmlhttp.send();
}

function removeShare(sharedUser) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeViewShareForm();
				viewShare();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("POST", "../RemoveShareController?user=" + sharedUser
			+ "&location=" + selectedButton, true);
	xmlhttp.send();
}

function change(sharedUser) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var successCheck = JSON.parse(this.responseText);
			if (successCheck.success == "true") {
				closeViewShareForm();
				viewShare();
			} else if (successCheck.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(successCheck.success);
			}
		}
	};
	xmlhttp.open("POST", "../ChangePrivilegeController?user=" + sharedUser
			+ "&location=" + selectedButton, true);
	xmlhttp.send();
}

function onKeyPress() {
	var text = document.getElementById("searchText");
	if (text.value.length >= 3) {
		for (var i = 0; i < commonWords.length; i++) {
			if(commonWords[i] == text.value){
				return;
			}
		}
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var successCheck = JSON.parse(this.responseText);
				var searchMenu = document.getElementById("searchmenu");
				searchMenu.style.display = "block";
				var searchResult = document.getElementById("searchresult");
				while (searchResult.firstChild) {
					searchResult.removeChild(searchResult.firstChild);
				}
				if(successCheck.length == 0){
					var br = document.createElement('br');
					document.getElementById("searchresult").appendChild(br);
					searchResult.appendChild(document.createTextNode(" No matches found ! "));
					return;
				}
					for(var i=0; i<successCheck.length; i++){						
						showFoundFiles(successCheck[i], successCheck.length);
					}
			}
		};
		xmlhttp.open("POST", "../SearchController?", true);
		xmlhttp.send(text.value);
	} else {
		closeSearchMenu();
	}
}

function showFoundFiles(successCheck, length) {
	var searchResult = document.getElementById("searchresult");
	for (x in successCheck) {
		if(x=="editDistance"){
			var br = document.createElement('br');
			searchResult.appendChild(br);
			if(length>1){
				searchResult.appendChild(document.createTextNode(" Exact word not found. "));
				searchResult.appendChild(document.createTextNode(" Suggestions : ")); 
			} else {				
				searchResult.appendChild(document.createTextNode(" No matches found ! "));
			}
			continue;
		}
		wordsList(x, successCheck[x]);
	}
	var br = document.createElement('br');
	document.getElementById("searchresult").appendChild(br);
}
function wordsList(word, files) {
	var searchResult = document.getElementById("searchresult");
	for(var i=0; i<files.length; i++)
	{
		var fileDetail = files[i];
			var br = document.createElement('br');
			searchResult.appendChild(br);
			searchResult.appendChild(document
					.createTextNode("Found: " + word + " "));
			fileList(word, Object.keys(fileDetail)[0], Object.values(fileDetail)[0]);
			var hr = document.createElement('hr');
			searchResult.appendChild(hr);
	}
	
}
function fileList(word, filePath, count) {
	var item = document.createElement('li');
	var button = document.createElement('div');
	button.setAttribute('id', filePath);
	var fileImg = document.createElement('img');
	fileImg.setAttribute('src', '../images/file.png');
	fileImg.setAttribute('alt', 'File Image');
	fileImg.setAttribute('width', '25px');
	fileImg.setAttribute('height', '25px');
	button.appendChild(fileImg);
	button.appendChild(document.createTextNode(" " + filePath + " (count: "
			+ count + ")"));
	item.appendChild(button);
	var fileButton = document.createElement('button');
	fileButton.setAttribute('class', 'editshare');
	fileButton.setAttribute('onclick', "openFile('" + filePath + "')");
	fileButton.appendChild(document.createTextNode("open"));
	item.appendChild(fileButton);
	var folderButton = document.createElement('button');
	folderButton.setAttribute('class', 'removeshare');
	folderButton.appendChild(document.createTextNode("open location"));
	folderButton.setAttribute('onclick', "openSourceLocation('" + filePath
			+ "')");
	item.appendChild(folderButton);
		var replaceButton = document.createElement('button');
		replaceButton.setAttribute('class', 'replace');
		replaceButton.appendChild(document.createTextNode("Replace word"));
		replaceButton.setAttribute('onclick', "replace('" + word
				+ "')");
		item.appendChild(replaceButton);
	document.getElementById("searchresult").appendChild(item);
}

function replace(word){
	var text = document.getElementById("searchText");
	text.value = word;
	onKeyPress();
}

function openSourceLocation(directory) {
	location.hash = directory.substring(0, directory.lastIndexOf('/'));
}

function logout() {
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

function onHashChange() {
	window.scrollTo(0, 0);
	var xmlhttp = new XMLHttpRequest();
	var hashValue = decodeURI(location.hash).substring(1);
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("dispdir").innerHTML = hashValue;	
			if(hashValue.indexOf('/')!=-1){
				document.getElementById(hashValue.substring(0,hashValue.indexOf('/'))).style.opacity = "1";
			} else {
				document.getElementById(hashValue).style.opacity = "1";
			}
			var myfolderlist = document.getElementById("myfolderlist");
			while (myfolderlist.firstChild) {
				myfolderlist.removeChild(myfolderlist.firstChild);
			}
			var parsedJsonObj = JSON.parse(this.responseText);
			if (parsedJsonObj.success == "ERROR") {
				alert("Folder not available");
				var temp = hashValue.substring(0,hashValue.lastIndexOf('/'));
				location.hash = temp.substring(0,temp.lastIndexOf('/'));
				goBack();
				
			} else {				
				for (x in parsedJsonObj) {
					openFolder(x, parsedJsonObj[x]);
				}
			}
		}
	};
	if (hashValue.indexOf('/') == -1) {
		xmlhttp.open("GET", "../ViewFolderController?shareduser=" + hashValue,
				true);
	} else {
		xmlhttp.open("GET", "../ViewFolderForLocationController?location="
				+ hashValue, true);
	}
	xmlhttp.send();
}