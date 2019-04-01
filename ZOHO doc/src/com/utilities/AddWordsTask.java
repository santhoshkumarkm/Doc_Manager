package com.utilities;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;

public class AddWordsTask extends TimerTask {

	static FileList fileList = new FileList();
	static EditList editList = new EditList();
	File file = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/NewFiles.txt");
	File editFile = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/EditFiles.txt");
	static HashMapUtil hashMapUtil = new HashMapUtil();

	public AddWordsTask() {
		if (file.exists()) {
			fileList = (FileList) Utilities.readFile(file);
		}
		if (editFile.exists()) {
			editList = (EditList) Utilities.readFile(editFile);
		}
		hashMapUtil.setTrie();
	}

	public static HashMapUtil getHashMapUtil() {
		return hashMapUtil;
	}

	public static FileList getFileList() {
		return fileList;
	}

	public static EditList getEditList() {
		return editList;
	}

	@Override
	public void run() {
		try {
			String filePath = null;
			String fileContent = null;
			if (editList.getFileNames().size() != 0) {
				Map<String, LinkedHashMap<Integer, String>[]> tempList = new LinkedHashMap<String, LinkedHashMap<Integer, String>[]>();
				tempList.putAll(editList.getFileNames());
				for (Map.Entry<String, LinkedHashMap<Integer, String>[]> entry : tempList.entrySet()) {
					filePath = entry.getKey();
					LinkedHashMap<Integer, String>[] twoLists = entry.getValue();
					if (twoLists[0].size() > 0) {
						hashMapUtil.removeWords(Long.valueOf(filePath.substring(0, filePath.indexOf('+'))),
								twoLists[0]);
					}
					if (twoLists[1] != null) {
						hashMapUtil.editWords(Long.valueOf(filePath.substring(0, filePath.indexOf('+'))), twoLists[1]);
					}
					editList.getFileNames().remove(filePath);
					Utilities.writeFile(editFile, editList);
					break;
				}
			}

			if (fileList.getFileNames().size() != 0) {
				filePath = fileList.getFileNames().iterator().next();
				FileOperations tempFile = new FileOperations(filePath);
				fileContent = tempFile.read();
				hashMapUtil.addWords(filePath, fileContent);
				fileList.getFileNames().remove(filePath);
				Utilities.writeFile(file, fileList);
			}
		} catch (Exception ee) {
			System.out.println("Catch occurred " + ee);
			ee.printStackTrace();
		}
	}
}
