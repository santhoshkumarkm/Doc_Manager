function navigate(element) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("mySpace").innerHTML = this.responseText;
		}
	};
	xmlhttp
			.open("GET", "../NavigationController?option=" + element.value,
					true);
	xmlhttp.send();
}

function validateFile() {
	var url = new File(document.getElementById("fileurl"));
	if (url.exists()) {
		alert('true');
	} else {
		alert('false');
	}
}

function alertTemp(element) {
	alert(element);
}

var folderImg = document.createElement('img');
folderImg.setAttribute('src', '../images/folder.png');
folderImg.setAttribute('alt', 'Folder');
folderImg.setAttribute('height', '20px');
folderImg.setAttribute('width', '20px');

var fileImg = document.createElement('img');
fileImg.setAttribute('src', '../images/file.png');
fileImg.setAttribute('alt', 'File');
fileImg.setAttribute('height', '20px');
fileImg.setAttribute('width', '20px');

var space = document.createElement('p');
space.innerHTML = "&emsp;";

function getFolders() {
	var xmlhttp = new XMLHttpRequest();
	var currentDir = document.getElementById("currentdir").innerHTML;
	xmlhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var jsonObj = JSON.parse(this.responseText);
			makeUL(jsonObj.folders, folderImg);
			makeUL(jsonObj.files, fileImg);
		}
	};
	xmlhttp.open("GET", "../ViewFolderController?currentdir=" + currentDir,
			true);
	xmlhttp.send();
}

function makeUL(array, element) {
	for (var i = 0; i < array.length; i++) {
		var item = document.createElement('li');
		var div = document.createElement('div');
		div.appendChild(element);
		div.appendChild(document.createTextNode(space.innerHTML+array[i]));
		item.appendChild(div);
		document.getElementById("myfolderlist").appendChild(item);
	}
}