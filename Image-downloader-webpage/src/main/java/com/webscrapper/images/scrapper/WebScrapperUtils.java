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

		/* This class implements Runnable and is used for multi-threading */
		class downloadImage implements Runnable {
			String imageLink = "";
			long threshold = 0;

			downloadImage(String imageLink, long threshold) {
				this.imageLink = imageLink;
				this.threshold = threshold;
			}

			public void run() {
				try {
					// download the image here
					downloadImageFromUrl(imageLink, threshold);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/* Run normally without any threads. */
		if (threadCount == 0) {
			downloadImagesFromUrls(imgLinks, fileSizeThresholdBytes);
		}
		/* No of threads are equal to no of links. */
		else if (threadCount < 0) {
			int i = 0;
			Thread thread[] = new Thread[imgLinks.size()];
			for (String link : imgLinks) {
				// Initialize each thread and run in parallel.
				thread[i] = new Thread(new downloadImage(link, fileSizeThresholdBytes));
				System.out.println("Running [Thread - " + i + "] : Downloading " + link);
				thread[i].start();

				i++;
			}

			// wait for all threads to complete.
			waitUntilAllThreadsComplete(thread);

		}
		/* Creates specific number of threads at a time. */
		else {
			List<String> links = new LinkedList<String>();
			links.addAll(imgLinks);

			int i;
			for (i = 0; i < links.size(); i += threadCount) {
				int lowerThreshold = (threadCount < (links.size() - i)) ? threadCount : (links.size() - i);

				Thread thread[] = new Thread[lowerThreshold];
				for (int j = 0; j < lowerThreshold; j++) {
					// Initialize each thread and run in parallel.
					thread[j] = new Thread(new downloadImage(links.get(i + j), fileSizeThresholdBytes));
					System.out.println("Running [Thread - " + j + "] : Downloading " + links.get(i + j));
					thread[j].start();
				}

				// wait for all threads to complete.
				waitUntilAllThreadsComplete(thread);

			}
		}
	}

	/*
	 * This is used to wait main thread until all the other threads are
	 * completed.
	 */
	public static void waitUntilAllThreadsComplete(Thread[] thread) {
		boolean checkIsThreadAlive = true;
		while (checkIsThreadAlive) {
			checkIsThreadAlive = false;
			for (int j = 0; j < thread.length; j++) {
				if (thread[j].isAlive()) {
					checkIsThreadAlive = true;
				}
			}
		}
	}

	/*
	 * This method downloads the image to a specified location from given url.
	 */
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
	public static Set<String> getImageLinksFromSource(String urlString, StringBuilder pageSource) {
		Set<String> allImageLinks = new HashSet<String>();

		allImageLinks.addAll(getAllDirectImageLinks(pageSource));
		allImageLinks.addAll(getAllImagesFromImageTag(urlString, pageSource));

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
	public static List<String> getAllImagesFromImageTag(String urlString, StringBuilder pageSource) {
		List<String> links = new LinkedList<String>();

		StringBuilder sourceAfterFindingImg = pageSource;
		int indx = -1;
		while ((indx = sourceAfterFindingImg.indexOf("<img")) != -1) {
			String relativePath = "";

			String imgTagString = sourceAfterFindingImg.substring(indx, sourceAfterFindingImg.indexOf(">", indx));
			int indxOfSrc = imgTagString.toLowerCase().indexOf("src=");
			String srcAttribute = "";

			if (imgTagString.charAt(indxOfSrc + 4) == '\'')
				srcAttribute = imgTagString.substring(indxOfSrc, imgTagString.indexOf("'", indxOfSrc + 5));
			else
				srcAttribute = imgTagString.substring(indxOfSrc, imgTagString.indexOf("\"", indxOfSrc + 5));

			relativePath = srcAttribute.toLowerCase().replaceAll("src=", "").replaceAll("'", "").replaceAll("\"", "");

			if (relativePath.endsWith("jpg") || relativePath.endsWith("jpeg") || relativePath.endsWith("png")
					|| relativePath.endsWith("bmp")) {
				String currDirInUrl = urlString.substring(0, urlString.lastIndexOf("/"));

				if (relativePath.startsWith("//") && currDirInUrl.startsWith("https"))
					links.add("https:" + relativePath);
				else if (relativePath.startsWith("//") && currDirInUrl.startsWith("http"))
					links.add("http:" + relativePath);
				else if (relativePath.startsWith("/"))
					links.add(currDirInUrl + relativePath);
				else if (relativePath.matches("\\.\\./\\w"))
					links.add(currDirInUrl.substring(0, currDirInUrl.lastIndexOf("/")) + relativePath);
				else if (relativePath.matches("\\w"))
					links.add(currDirInUrl + "/" + relativePath);
			}

			sourceAfterFindingImg = new StringBuilder(sourceAfterFindingImg.substring(indx + 1));
		}

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
