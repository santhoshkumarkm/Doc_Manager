package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.TimerTask;

public class AddWordsTask extends TimerTask {

	static FileList fileList = new FileList();
	File file = new File("/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/NewFiles.txt");
	HashMapUtil hashMapUtil = new HashMapUtil();

	public AddWordsTask(){
		if (file.exists()) {
			fileList = (FileList) Utilities.readFile(file);
		}
	}

	public static FileList getFileList() {
		return fileList;
	}

	@Override
	public void run() {
//		System.out.println("Listener running");
		String filePath = null, mode = null;
		String fileContent = null;
		if (fileList.getFileNames().size() != 0) {
			try {
				for (Map.Entry<String, String> entry : fileList.getFileNames().entrySet()) {
					filePath = entry.getKey();
					mode = entry.getValue();
					fileContent = Utilities.stringBuilder(new BufferedReader(new FileReader(new File(
							"/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/" + filePath))));
					if (mode.equals("new")) {
						hashMapUtil.addWords(filePath, fileContent);
					} else {
						hashMapUtil.editWords(filePath, fileContent);
					}
					fileList.getFileNames().remove(filePath);
					Utilities.writeFile(file, fileList);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

}
