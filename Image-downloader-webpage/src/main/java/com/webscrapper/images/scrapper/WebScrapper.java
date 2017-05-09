package com.webscrapper.images.scrapper;

import java.io.IOException;
import java.util.Set;

public class WebScrapper {

	/* Downloads images with files > 100KB without multi-threading. */
	public static void downloadImages(String urlString) throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(urlString, pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks);
	}

	/* Downloads images with files > specified size without multi-threading. */
	public static void downloadImages(String urlString, long fileSizeThresholdBytes) throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(urlString, pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks, fileSizeThresholdBytes);
	}

	/* Downloads images with files > specified size with multi-threading. */
	public static void downloadImages(String urlString, long fileSizeThresholdBytes, int threadCount)
			throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(urlString, pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks, fileSizeThresholdBytes, threadCount);
	}

}
