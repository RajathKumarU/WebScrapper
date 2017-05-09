package com.webscrapper.images.scrapper;

import java.io.IOException;
import java.util.Scanner;

public class DownloadWallpaper {
	public static void main(String[] args) throws IOException {
		System.out.println("Enter/Paste a url from which wallpapers should be downloaded: ");
		Scanner input = new Scanner(System.in);
		String url = input.nextLine();

		System.out.println("Find images downloaded in /src/main/java/downloads");
		input.close();

		//WebScrapper.downloadImages(url);
		WebScrapper.downloadImages(url, 100000, 10);
		
		System.out.println("Downloaded!");
	}
}
