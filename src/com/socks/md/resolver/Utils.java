package com.socks.md.resolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	public static void writeStringToFile(String content, File targetFile)
			throws Exception {
		FileWriter writer = new FileWriter(targetFile);
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static String getImageTagByUrl(String url) {
		return "![](" + url + ")";
	}

	public static String getFilePathFromImageTag(String localImageTag) {
		return localImageTag.replace("![](", "").replace(")", "");
	}

	public static String getStringFromFile(File file, String encode) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), encode);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				sb.append(lineTxt).append("\n");
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return sb.toString();
	}

	public static String getFileNameFromImageTag(String localImageTag) {
		String[] strArray = localImageTag.split("/");
		return strArray[strArray.length - 1].replace(")", "");
	}

	public static void println(String message) {
		System.out.println(message);
	}

	public static String readDataFromConsole(String prompt) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		try {
			System.out.print(prompt);
			str = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
}
