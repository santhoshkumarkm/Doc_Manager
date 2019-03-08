package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utilities {
	private static FileInputStream fin;
	private static ObjectInputStream oin;
	private static FileOutputStream fout;
	private static ObjectOutputStream oout;

	public static String stringBuilder(BufferedReader bin) {
		StringBuilder stringBuilder = new StringBuilder();
		String s = "";
		try {
			while ((s = bin.readLine()) != null) {
				stringBuilder.append(s + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	public static Object readFile(File file) {
		Object object = null;
		try {
			fin = new FileInputStream(file);
			oin = new ObjectInputStream(fin);
			object = oin.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (oin != null)
				try {
					oin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return object;
	}

	public static void writeFile(File file, Object object) {
		try {
			fout = new FileOutputStream(file);
			oout = new ObjectOutputStream(fout);
			oout.writeObject(object);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (oout != null)
				try {
					oout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
	}
}