package com.webscrapper.images.scrapper;

import java.io.IOException;
import java.util.Scanner;

public class DownloadWallpaper {
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		System.out.println("Enter/Paste a url from which wallpapers should be downloaded: ");
		Scanner input = new Scanner(System.in);
		String url = input.nextLine();

		System.out.println("Find images downloaded in /src/main/java/downloads");
		input.close();

		// WebScrapper.downloadImages(url);
		WebScrapper.downloadImages(url, 100000, 0);

		System.out.println("\n\nDownloaded!!!");

		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to execute: " + (endTime - startTime) + " milliseconds");

	}
}
