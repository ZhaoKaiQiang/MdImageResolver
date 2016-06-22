package com.socks.md.resolver;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qiniu.http.Response;

public class MainThread {

	private static final Pattern pattern = Pattern
			.compile("!\\[]\\([\\w-_/]+.[png]{0,}[jpg]{0,}[PNG]{0,}[jpeg]{0,}[gif]{0,}\\)");

	private static UploadUtil mUploadDemo;
	private static LocalProperty mLocalProperty;

	public static void main(String[] args) throws Exception {

		mLocalProperty = LocalProperty.getLocalProperty();

		if (mLocalProperty == null) {
			String accessKey = Utils
					.readDataFromConsole("Please input your access key:\n");
			String secretKey = Utils
					.readDataFromConsole("Please input your secret key:\n");
			String bucketName = Utils
					.readDataFromConsole("Please input your bucket name:\n");

			String domainName = Utils
					.readDataFromConsole("Please input your domain name:\n");

			mLocalProperty = new LocalProperty();
			mLocalProperty.setAccessKey(accessKey);
			mLocalProperty.setSecretKey(secretKey);
			mLocalProperty.setBucketName(bucketName);
			mLocalProperty.setDomainName(domainName);
			mLocalProperty.saveLocalProperty();
		}

		mUploadDemo = new UploadUtil(mLocalProperty.getAccessKey(),
				mLocalProperty.getSecretKey(), mLocalProperty.getBucketName());

		String filePath = Utils
				.readDataFromConsole("Please drag your Markdown file to this window !\n");

		// filePath =
		// "/Users/zhao/workspace/MdImageResolver/src/com/socks/md/resolver/KLog.md";

		File targetFile = new File(filePath);

		if (!targetFile.exists() || !targetFile.canWrite()) {
			Utils.println("Error ! File not found or can't be write !");
			return;
		}

		Utils.println("Start parse file :\n" + targetFile.getAbsolutePath()
				+ "\n");
		String blogString = Utils.getStringFromFile(targetFile, "UTF-8");

		Matcher matcher = pattern.matcher(blogString);

		File imgFile;
		int successCount = 0;
		int failedCount = 0;
		while (matcher.find()) {
			String imageTag = matcher.group();
			String imgFilePath = Utils.getFilePathFromImageTag(imageTag);
			Utils.println("Find Local image path is :\n" + imgFilePath + "\n");
			imgFile = new File(imgFilePath);

			if (!imgFile.exists() || !imgFile.canRead()) {
				Utils.println("Error ! File not found or can't be write !"
						+ imgFile.getAbsolutePath());
				continue;
			}

			Utils.println("Start upload " + imgFile.getName()
					+ " to QiNiu service...\n");
			Response response = mUploadDemo.upload(imgFile.getAbsolutePath(),
					imgFile.getName());
			if (response.isOK()) {
				String qiniuUrl = mLocalProperty.getImageUrl(imgFile.getName());
				Utils.println("Upload successful ! The url of image is "
						+ qiniuUrl + "\n");
				blogString = blogString.replace(imageTag,
						Utils.getImageTagByUrl(qiniuUrl));
				successCount++;
			} else {
				failedCount++;
				Utils.println("Error ! " + response.error);
			}
		}

		Utils.writeStringToFile(blogString, targetFile);
		Utils.println("Upload image completed !\n Success:" + successCount
				+ "   Failed:" + failedCount);

		System.exit(1);
	}
}
