package com.webscrapper.images.scrapper;

import java.io.IOException;
import java.util.Set;

public class WebScrapper {
	public static void downloadImages(String urlString) throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks);
	}

	public static void downloadImages(String urlString, long fileSizeThresholdBytes) throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks, fileSizeThresholdBytes);
	}
	
	public static void downloadImages(String urlString, long fileSizeThresholdBytes, int threadCount) throws IOException {

		StringBuilder pageSource = WebScrapperUtils.getPageSource(urlString);

		Set<String> imgLinks = WebScrapperUtils.getImageLinksFromSource(pageSource);

		WebScrapperUtils.downloadImagesFromUrls(imgLinks, fileSizeThresholdBytes, threadCount);
	}

}
