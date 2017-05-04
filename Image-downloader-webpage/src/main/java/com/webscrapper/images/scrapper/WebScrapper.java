package com.webscrapper.images.scrapper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class WebScrapper {
	public static void downloadImages(String urlString) throws IOException {

		StringBuilder pageSource = getPageSource(urlString);
		System.out.println("\n\nPageSource: \n" + pageSource + "\n");

		List<String> imgLinks = getImageLinksFromSource(pageSource);
		for (String link : imgLinks) {
			System.out.println(link);
		}

		downloadImagesFromUrls(imgLinks);
	}

	public static void downloadImagesFromUrls(List<String> imgLinks) throws IOException {
		for (String link : imgLinks) {
			// Download image here
			URL url = new URL(link);
			String fileName = url.getFile();
			String destName = "src/main/java/downloads" + fileName.substring(fileName.lastIndexOf("/"));
			// System.out.println(destName);

			InputStream is = url.openStream();
			InputStream isNew = url.openStream();

			byte[] b = new byte[2048];
			int length;
			long fileSizeBytes = 0;

			while ((length = is.read(b)) != -1) {
				fileSizeBytes += length;
			}
			is.close();

			// Download all images > 100KB
			if (fileSizeBytes > 100000) {
				isNew = url.openStream();
				OutputStream os = new FileOutputStream(destName);
				while ((length = isNew.read(b)) != -1) {
					os.write(b, 0, length);
				}
				os.close();
			}
			isNew.close();
		}
	}

	/* This method gets all the image links from a page source */
	public static List<String> getImageLinksFromSource(StringBuilder pageSource) {
		List<String> links = new LinkedList<String>();

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
				if (!links.contains(link))
					links.add(link);
			}

			sourceAfterFindingHttp = new StringBuilder(sourceAfterFindingHttp.substring(indx + 1));
		}

		return links;
	}

	/* This method gets */
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
}
