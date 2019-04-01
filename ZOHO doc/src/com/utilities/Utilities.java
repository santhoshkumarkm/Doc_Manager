package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

public class Utilities {
	private static FileInputStream fin;
	private static ObjectInputStream oin;
	private static FileOutputStream fout;
	private static ObjectOutputStream oout;
	
	static int min(int x,int y,int z) 
    { 
        if (x<=y && x<=z) return x; 
        if (y<=x && y<=z) return y; 
        else return z; 
    } 
	
	static int editDistance(String str1, String str2, int m, int n) 
    { 
        // Create a table to store results of subproblems 
        int dp[][] = new int[m+1][n+1]; 
       
        // Fill d[][] in bottom up manner 
        for (int i=0; i<=m; i++) 
        { 
            for (int j=0; j<=n; j++) 
            { 
                // If first string is empty, only option is to 
                // insert all characters of second string 
                if (i==0) 
                    dp[i][j] = j;  // Min. operations = j 
       
                // If second string is empty, only option is to 
                // remove all characters of second string 
                else if (j==0) 
                    dp[i][j] = i; // Min. operations = i 
       
                // If last characters are same, ignore last char 
                // and recur for remaining string 
                else if (str1.charAt(i-1) == str2.charAt(j-1)) 
                    dp[i][j] = dp[i-1][j-1]; 
       
                // If the last character is different, consider all 
                // possibilities and find the minimum 
                else
                    dp[i][j] = 1 + min(dp[i][j-1],  // Insert 
                                       dp[i-1][j],  // Remove 
                                       dp[i-1][j-1]); // Replace 
            } 
        } 
   
        return dp[m][n]; 
    } 

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

	public static LinkedHashMap<Integer, String>[] getEditedWords(String prevFile, String text) {
		LinkedHashMap<Integer, String> prevList = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> updatedList = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String>[] twoLists = new LinkedHashMap[2];
		String[] prev = prevFile.split("\\W+");
		String[] updated = text.split("\\W+");
		int count = 0;
		for (count = 0; count < updated.length; count++) {
			if (count < prev.length && !updated[count].equals(prev[count])) {
				prevList.put(count, prev[count]);
				updatedList.put(count, updated[count]);
			} else if (count >= prev.length) {
				updatedList.put(count, updated[count]);
			}
		}
		if (text.length() == 0) {
//			count--;
			updatedList = null;
		}
		while (count < prev.length) {
			prevList.put(count, prev[count]);
			count++;
		}
		twoLists[0] = prevList;
		twoLists[1] = updatedList;
		return twoLists;
	}
}