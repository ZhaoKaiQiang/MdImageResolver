package com.socks.test;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import com.socks.md.resolver.Utils;

public class ResolverTest {

	@Test
	public void testParseImageTag() throws Exception {
		File targetFile = new File(
				"/Users/zhao/Desktop/MdImageResolver/src/com/socks/test",
				"image.md");
		String blogString = Utils.getStringFromFile(targetFile, "UTF-8");
		ArrayList<String> tagsArrayList = Utils.getImageTags(blogString);
		for (int i = 0; i < tagsArrayList.size(); i++) {
			Utils.println(tagsArrayList.get(i));
		}
	}

	@Test
	public void testFindAlt() throws Exception {
		File targetFile = new File(
				"/Users/zhao/Desktop/MdImageResolver/src/com/socks/test",
				"image.md");
		String blogString = Utils.getStringFromFile(targetFile, "UTF-8");
		ArrayList<String> tagsArrayList = Utils.getImageTags(blogString);

		for (int i = 0; i < tagsArrayList.size(); i++) {
			String imageTag = tagsArrayList.get(i);
			String imgFilePath = Utils.getFilePathFromImageTag(imageTag);
			String altString = Utils.getAltFromImageTag(imageTag);
			Utils.println("Find Local image path is :\n" + imgFilePath
					+ "\nImage alt is " + altString);
			Utils.println("New image tag is "
					+ Utils.getImageTagByUrl(altString,
							"http://www.google.com/") + "\n");
		}

	}

}
