package com.webscrapper.images.scrapper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WebScrapperUtils {
	/*
	 * This method downloads all the images specified in the List of image urls
	 * whose file size is greater than 100KB by default.
	 */
	public static void downloadImagesFromUrls(Set<String> imgLinks) throws IOException {
		for (String link : imgLinks) {
			downloadImageFromUrl(link, 100000);
		}
	}

	/*
	 * This method downloads all the images specified in the List of image urls
	 * with specified file size.
	 */
	public static void downloadImagesFromUrls(Set<String> imgLinks, long fileSizeThresholdBytes) throws IOException {
		for (String link : imgLinks) {
			downloadImageFromUrl(link, fileSizeThresholdBytes);
		}
	}

	/*
	 * This method downloads all the images specified in the List of image urls
	 * with specified file size using multi-threading.
	 */
	public static void downloadImagesFromUrls(Set<String> imgLinks, long fileSizeThresholdBytes, int threadCount)
			throws IOException {
		// Download image here
//		for (String link : imgLinks) {
//			Runnable runnable = new Runnable() {
//				public void run() {
//					downloadImageFromUrl(link, fileSizeThresholdBytes);
//				}
//			};
//
//		}
	}

	/* This method downloads the image to a specified location from given url */
	public static void downloadImageFromUrl(String link, long fileSizeThresholdBytes) throws IOException {
		URL url = new URL(link);
		String fileName = url.getFile();
		String destName = "src/main/java/downloads" + fileName.substring(fileName.lastIndexOf("/"));

		InputStream is = url.openStream();

		byte[] b = new byte[2048];
		int length;
		long fileSizeBytes = 0;

		while ((length = is.read(b)) != -1) {
			fileSizeBytes += length;
		}
		is.close();

		// Download all images > threshold
		if (fileSizeBytes > fileSizeThresholdBytes) {
			is = url.openStream();
			OutputStream os = new FileOutputStream(destName);
			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}
			os.close();
			System.out.println("Downloaded " + destName);
		}
		is.close();
	}

	/*
	 * This method gets all the image links i.e. absolute as well as relative
	 * path from a page source.
	 */
	public static Set<String> getImageLinksFromSource(StringBuilder pageSource) {
		Set<String> allImageLinks = new HashSet<String>();

		allImageLinks.addAll(getAllDirectImageLinks(pageSource));
		allImageLinks.addAll(getAllImagesFromImageTag(pageSource));

		// Print all image links in the set.
		for (Iterator<String> iterator = allImageLinks.iterator(); iterator.hasNext();) {
			String link = (String) iterator.next();
			System.out.println(link);
		}

		return allImageLinks;
	}

	/*
	 * This method gets all the image urls that are in img tag with relative
	 * path specified.
	 */
	public static List<String> getAllImagesFromImageTag(StringBuilder pageSource) {
		List<String> links = new LinkedList<String>();

		return links;
	}

	/*
	 * This method returns all the links starting with http and ending with jpg
	 * or jpeg or png or bmp using string match.
	 */
	public static List<String> getAllDirectImageLinks(StringBuilder pageSource) {
		List<String> links = new LinkedList<String>();

		System.out.println("\n\nPageSource: \n" + pageSource + "\n");

		StringBuilder sourceAfterFindingHttp = pageSource;
		int indx = -1;
		while ((indx = sourceAfterFindingHttp.indexOf("http")) != -1) {
			String link = "";
			int indxOfSingle = sourceAfterFindingHttp.indexOf("'", indx);
			int indxOfDouble = sourceAfterFindingHttp.indexOf("\"", indx);

			if (indxOfDouble == -1)
				link = sourceAfterFindingHttp.substring(indx, indxOfSingle);
			else if (indxOfSingle == -1)
				link = sourceAfterFindingHttp.substring(indx, indxOfDouble);
			else if ((indxOfSingle > indxOfDouble))
				link = sourceAfterFindingHttp.substring(indx, indxOfDouble);
			else if ((indxOfSingle < indxOfDouble))
				link = sourceAfterFindingHttp.substring(indx, indxOfSingle);

			String tempLink = link.toLowerCase();
			if (tempLink.endsWith("jpg") || tempLink.endsWith("jpeg") || tempLink.endsWith("png")
					|| tempLink.endsWith("bmp")) {
				links.add(link);
			}

			sourceAfterFindingHttp = new StringBuilder(sourceAfterFindingHttp.substring(indx + 1));
		}

		return links;
	}

	/* This method gets WebPage source from a given url. */
	public static StringBuilder getPageSource(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection urlConn = url.openConnection();

		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
		String line = "";
		StringBuilder pageSrc = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			pageSrc.append(line);
		}

		return pageSrc;
	}

	/* This method gets WebPage source from a given url and charset (UTF-8). */
	public static StringBuilder getPageSource(String urlString, String charset) throws IOException {
		URL url = new URL(urlString);
		URLConnection urlConn = url.openConnection();

		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), charset));
		String line = "";
		StringBuilder pageSrc = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			pageSrc.append(line);
		}

		return pageSrc;
	}
}
