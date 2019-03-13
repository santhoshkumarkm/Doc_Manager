package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;

public class AddWordsTask extends TimerTask {

	static FileList fileList = new FileList();
	static EditList editList = new EditList();
	File file = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/NewFiles.txt");
	File editFile = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/EditFiles.txt");
	HashMapUtil hashMapUtil = new HashMapUtil();

	public AddWordsTask() {
		if (file.exists()) {
			fileList = (FileList) Utilities.readFile(file);
		}
		if (editFile.exists()) {
			editList = (EditList) Utilities.readFile(editFile);
		}
	}

	public static FileList getFileList() {
		return fileList;
	}

	public static EditList getEditList() {
		return editList;
	}

	@Override
	public void run() {
//		System.out.println("Listener running");
		String filePath = null;
		String fileContent = null;
		if (editList.getFileNames().size() != 0) {
			try {
				for (Map.Entry<String, LinkedHashMap<Integer, String>[]> entry : editList.getFileNames().entrySet()) {
					filePath = entry.getKey();
					LinkedHashMap<Integer, String>[] twoLists = entry.getValue();
					fileContent = Utilities.stringBuilder(new BufferedReader(new FileReader(new File(
							"/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/" + filePath))));
					hashMapUtil.removeWords(filePath, twoLists[0]);
					hashMapUtil.editWords(filePath, twoLists[1]);
					Utilities.writeFile(editFile, editList);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (fileList.getFileNames().size() != 0) {
			try {
				filePath = fileList.getFileNames().iterator().next();
				fileContent = Utilities.stringBuilder(new BufferedReader(new FileReader(
						new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/" + filePath))));
//				System.out.println("file path: " + filePath);
//				System.out.println("file content: " + fileContent);
				hashMapUtil.addWords(filePath, fileContent);
				fileList.getFileNames().remove(filePath);
				Utilities.writeFile(file, fileList);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
