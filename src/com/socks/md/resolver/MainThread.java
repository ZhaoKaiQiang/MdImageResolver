package com.socks.md.resolver;

import java.io.File;
import java.util.ArrayList;

import com.qiniu.http.Response;

public class MainThread {

	private static final int MODE_COVER = 1;
	private static final int MODE_BACKUP = 2;

	private static UploadUtil mUploadDemo;
	private static LocalProperty mLocalProperty;

	public static void main(String[] args) throws Exception {

		mLocalProperty = LocalProperty.getLocalProperty();

		if (mLocalProperty == null) {
			mLocalProperty = LocalProperty.getLocalPropertyByConsole();
		}

		mUploadDemo = new UploadUtil(mLocalProperty.getAccessKey(),
				mLocalProperty.getSecretKey(), mLocalProperty.getBucketName());

		String filePath = Utils
				.readDataFromConsole("Please drag your Markdown file to this window \n");

		int mode = 0;
		while (mode != MODE_COVER && mode != MODE_BACKUP) {
			String modeString = Utils
					.readDataFromConsole(
							"Please choose a save mode :\n 1.Coverage Mode.The source file will be overwritten.  \n 2.BackUp Mode. Create a new back up file.\n")
					.trim();
			try {
				mode = Integer.valueOf(modeString);
			} catch (Exception e) {
				mode = 0;
				Utils.println("Please input 1 or 2 to choose save mode !");
			}
		}

		File targetFile = new File(filePath);

		if (!targetFile.exists() || !targetFile.canWrite()) {
			Utils.println("Error ! File not found or can't be write !");
			return;
		}

		Utils.println("Start parse file :" + targetFile.getAbsolutePath()
				+ "\n");
		String blogString = Utils.getStringFromFile(targetFile, "UTF-8");

		File imgFile;
		int successCount = 0;
		int failedCount = 0;

		ArrayList<String> tagsArrayList = Utils.getImageTags(blogString);

		if (tagsArrayList.size() > 0) {

			for (int i = 0; i < tagsArrayList.size(); i++) {
				String imageTag = tagsArrayList.get(i);
				String imgFilePath = Utils.getFilePathFromImageTag(imageTag);
				String altString = Utils.getAltFromImageTag(imageTag);
				Utils.println("Find Local image :" + imgFilePath + "\n");
				imgFile = new File(imgFilePath);

				if (!imgFile.exists() || !imgFile.canRead()) {
					Utils.println("File not found or can't be write !\n");
					failedCount++;
					continue;
				}

				Utils.println("Start upload " + imgFile.getName()
						+ " to QiNiu service...\n");
				Response response = mUploadDemo.upload(
						imgFile.getAbsolutePath(), imgFile.getName());
				if (response.isOK()) {
					String qiniuUrl = mLocalProperty.getImageUrl(imgFile
							.getName());
					Utils.println("Upload successful ! The url of image is "
							+ qiniuUrl + "\n");
					blogString = blogString.replace(imageTag,
							Utils.getImageTagByUrl(altString, qiniuUrl));
					successCount++;
				} else {
					failedCount++;
					Utils.println("Error ! " + response.error);
				}

			}

			switch (mode) {
			case MODE_COVER:
				Utils.writeStringToFile(blogString, targetFile);
				Utils.println("Save as " + targetFile.getAbsolutePath() + "\n");
				break;
			case MODE_BACKUP:
				String fileName = Utils.getBackUpFileName(targetFile.getName());
				File backupFile = new File(targetFile.getParentFile(), fileName);
				Utils.writeStringToFile(blogString, backupFile);
				Utils.println("Save as " + backupFile.getAbsolutePath() + "\n");
				break;
			default:
				throw new IllegalArgumentException("ERROR MODE " + mode);
			}

			Utils.println("Upload image completed ! Success:" + successCount
					+ "   Failed:" + failedCount);
		} else {
			Utils.println("Didn't find any local image tag.");
		}
		System.exit(1);
	}
}
