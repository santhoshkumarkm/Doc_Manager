var owner = null;
var currentDir = null;
var currentPrivilege = null;
var selectedButton = null;
var allUserList = [];

function request(method, URL, body, callback){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var responseObj = JSON.parse(this.responseText);
			if(responseObj != null){
				 callback(responseObj);
			}
		}
	};
	xmlhttp.open(method, URL, true);
	if(body == null){		
		xmlhttp.send();
	} else {
		xmlhttp.send(body);
	}
}

function checkUser(){
	var userName = document.getElementById("userName").value;
	if(userName.indexOf('/') != -1){
		document.getElementById("successCheck").innerHTML = " '/' cannot be used in user name"
		return;
	}
	request("GET", "../DuplicateUserController?username="+userName, null, function(responseObj){		
		if(responseObj.success == "true"){
			document.getElementById("successCheck").innerHTML = " (Available)";
		} else {
			document.getElementById("successCheck").innerHTML = " (User Name not available)";
		}
	});
}

function submitSignUpForm(){
	var password = document.getElementById("password").value;
	var confirmPassword = document.getElementById("confirmPassword").value;
	var userName = document.getElementById("userName").value;
	if(userName.length >=3 && userName.indexOf('/') == -1 && document.getElementById("successCheck").innerHTML== " (Available)" && password.length >= 6 && password == confirmPassword){		
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
	request("GET", "../SharedUserListController", null, function(responseObj){
			var userList = responseObj.userList;
			allUserList.push(rootUser);
			userList.forEach(userListTraverse);
		});
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
	request("GET", "../ValidateController?location=" + currentLocation, null, function(responseObj){
		if(responseObj.success == "true"){
			if(decodeURI(location.hash) != ""){				
				onHashChange();
				viewFiles(currentLocation, responseObj.privilege);
			}
		} else {
			logout();
		}
	});
}

function openFolder(file, privilege) {
	var shared = false; 
	if(privilege.startsWith("owner")){
		if(privilege.endsWith("(s)")){
			shared = true;
		}
		privilege = privilege.substring(0,"owner".length);
	}
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
	} else if (shared){
		var span = document.createElement('span');
		span.style.color = 'black';
		span.innerHTML = "&#8599;";
		button.appendChild(document.createTextNode(" " + btnText + "  "));
		button.appendChild(span);
	} else {
		button.appendChild(document.createTextNode(" " + btnText));
	}
	item.appendChild(button);
	document.getElementById("myfolderlist").appendChild(item);
}

function goBack() {
	closeBoxForm();
	closeButtonForm();
	request("GET", "../GoBackController?location=" + location.hash.substring(1), null, function(responseObj){
			if (responseObj.access != "denied") {
				currentPrivilege = responseObj.access;
				viewFiles(responseObj.prevLocation, responseObj.access);
			}
		});
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
	if (currentPrivilege == "owner"){
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
	var confirmLogout = confirm("Confirm delete?");
	if(confirmLogout == false){
		return;
	}
	request("POST", "../DeleteFileController?location=" + selectedButton, null, function(responseObj){
		if (responseObj.success == "true") {
			closeButtonForm();
			onHashChange();
		} else if (responseObj.success == "logout") {
			window.location.replace("../index.jsp");
		} else {
			alert(responseObj.success);
		}
	});
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
	request("POST", "../NewFolderController?location=" + location.hash.substring(1)
			+ "&foldername=" + folderName, null, function(responseObj){
		if (responseObj.success == "true") {
			closeNewFolderForm();
			onHashChange();
		} else if (responseObj.success == "logout") {
			window.location.replace("../index.jsp");
		} else {
			alert(responseObj.success);
		}
	});
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
	fileName = document.getElementById("displayfilename").innerHTML;
	if (document.getElementById("editbutton").style.display == "inline-block") {
		request("POST", "../NewFileController?location=" + location.hash.substring(1)
				+ "&filename=" + fileName + "&mode=edit", text, submitFileImpl);
	} else {
		request("POST", "../NewFileController?location=" + location.hash.substring(1)
				+ "&filename=" + fileName + "&mode=new", text, submitFileImpl);
	}
}

function submitFileImpl(responseObj){
	if (responseObj.success == "true") {
		closeEditor();
		onHashChange();
	} else if (responseObj.success == "logout") {
		window.location.replace("../index.jsp");
	} else {
		alert(responseObj.success);
	}
}

function openFile(fileName) {
	request("GET", "../OpenFileController?location=" + location.hash.substring(1)
			+ "&filename=" + fileName, null, function(responseObj){
		if (responseObj.success == "true") {
			document.getElementById("textarea").value = responseObj.content;
			document.getElementById("textarea").disabled = true;
			document.getElementById("savebutton").style.display = "none";
			if (responseObj.privilege == "read") {
				fileName = fileName + " (Read only mode)";
				document.getElementById("editbutton").style.display = "none";
			} else {
				document.getElementById("editbutton").style.display = "inline-block";
				document.getElementById("editbutton").style.opacity = "1";
			}
			document.getElementById("displayfilename").innerHTML = fileName;
			document.getElementById("editor").style.display = "block";
		} else if (responseObj.success == "logout") {
			window.location.replace("../index.jsp");
		} else {
			alert(responseObj.success);
		}
	});
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
	request("GET", "../AllUserListController?location=" + selectedButton, null, function(responseObj){
		if (responseObj.success == "true") {
			closeButtonForm();
			document.getElementById("selectedFile").innerHTML = selectedButton
				.substring(selectedButton.lastIndexOf('/') + 1);
			var option = document.createElement('option');
			responseObj.users.forEach(addOption);
			document.getElementById("shareFileForm").style.display = "block";
		} else if (responseObj.success == "logout") {
				window.location.replace("../index.jsp");
		} else {
			alert(responseObj.success);
		}
	});
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
	if (opt != "") {
		request("POST", "../ShareFileController?location="
			+ selectedButton + "&privilege=" + getPrivilege, opt, function(responseObj){
			if (responseObj.success == "true") {
				closeShareFileForm();
				onHashChange();
			} else if (responseObj.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(responseObj.success);
			}
		});
	} else {
		alert("No user selected");
	}
}

function viewShare() {
	request("GET", "../ViewShareController?location=" + selectedButton, null, function(responseObj){
			closeButtonForm();
			if(responseObj.success == "ERROR"){
				alert(responseObj.success);
				return;
			}
			if (Object.keys(responseObj)[0] == "notshared") {
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
			for (x in responseObj) {
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
			td2.appendChild(document.createTextNode(responseObj[x]+ " "));
			td2.appendChild(editOption);
			td2.appendChild(removeOption);
			td2.setAttribute('align', 'right');
			tr.appendChild(td2);
			table.appendChild(tr);
			document.getElementById("viewShareForm").style.display = "block";
			}
		});
}

function removeShare(sharedUser) {
	request("POST", "../RemoveShareController?user=" + sharedUser
			+ "&location=" + selectedButton, null, function(responseObj){
			if (responseObj.success == "true") {
				closeViewShareForm();
				viewShare();
				onHashChange();
			} else if (responseObj.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(responseObj.success);
			}
		});
}

function change(sharedUser) {
	request("POST", "../ChangePrivilegeController?user=" + sharedUser
			+ "&location=" + selectedButton, null, function(responseObj){
			if (responseObj.success == "true") {
				closeViewShareForm();
				viewShare();
			} else if (responseObj.success == "logout") {
				window.location.replace("../index.jsp");
			} else {
				alert(responseObj.success);
			}
		});
}

function containsAplhaNumeric(str) {
	  var code, i, len;
	  for (i = 0, len = str.length; i < len; i++) {
	    code = str.charCodeAt(i);
	    if ((code > 47 && code < 58) || //(0-9)
	        (code > 64 && code < 91) || //(A-Z)
	        (code > 96 && code < 123)) { //(a-z)
	      return true;
	    }
	  }
	  return false;
}

function onKeyPress() {
	var text = document.getElementById("searchText").value;
	if (text.length >= 3 && containsAplhaNumeric(text)) {
		request("POST", "../SearchController?", text, function(responseObj){
				var searchResult = document.getElementById("searchresult");
				var searchMenu = document.getElementById("searchmenu");
				searchMenu.style.display = "block";
				while (searchResult.firstChild) {
					searchResult.removeChild(searchResult.firstChild);
				}
				if(responseObj.common != null){
					var br = document.createElement('br');
					document.getElementById("searchresult").appendChild(br);
					searchResult.appendChild(document.createTextNode(" Word is very common ! "));
					return;					
				}
				if(responseObj.length == 0){
					var br = document.createElement('br');
					document.getElementById("searchresult").appendChild(br);
					searchResult.appendChild(document.createTextNode(" No matches found ! "));
					return;
				}
					for(var i=0; i<responseObj.length; i++){						
						showFoundFiles(responseObj[i], responseObj.length);
					}
			});
	} else {
		closeSearchMenu();
	}
}

function showFoundFiles(responseObj, length) {
	var searchResult = document.getElementById("searchresult");
	for (x in responseObj) {
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
		wordsList(x, responseObj[x]);
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

function confirmLogout(){
	var confirmOut = confirm("Are your sure to logout?");
	if(confirmOut == true){
		logout();
	}
	
}

function logout() {
	request("GET", "../LogoutController", null, function(responseObj){
			window.location.replace("../index.jsp");
	});
}

function onHashChange() {
	window.scrollTo(0, 0);
	var hashValue = decodeURI(location.hash).substring(1);
	if (hashValue.indexOf('/') == -1) {
		request("GET", "../ViewFolderController?shareduser=" + hashValue, null, hashChangeImpl);
	} else {
		request("GET","../ViewFolderForLocationController?location=" + hashValue, null, hashChangeImpl);
	}
}

function hashChangeImpl(responseObj){
	var hashValue = decodeURI(location.hash).substring(1);
	if (responseObj.success == "ERROR") {
		alert("Folder not available");
		var temp = hashValue.substring(0,hashValue.lastIndexOf('/'));
		location.hash = temp.substring(0,temp.lastIndexOf('/'));
		goBack();
	} else if (responseObj.success == "DENIED") {
		alert("Folder not accessible");
		window.history.back();
	} else {
		document.getElementById("dispdir").innerHTML = hashValue;
		for (var i = 0; i < allUserList.length; i++) {
			document.getElementById(allUserList[i]).style.opacity = "0.5";
		}
		if(hashValue.indexOf('/')!=-1){
			document.getElementById(hashValue.substring(0,hashValue.indexOf('/'))).style.opacity = "1";
		} else {
			document.getElementById(hashValue).style.opacity = "1";
		}
		var myfolderlist = document.getElementById("myfolderlist");
		while (myfolderlist.firstChild) {
			myfolderlist.removeChild(myfolderlist.firstChild);
		}
		for (x in responseObj) {
			openFolder(x, responseObj[x]);
		}
	}
}