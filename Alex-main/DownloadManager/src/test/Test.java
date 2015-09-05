package test;

import task.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) {
		/*
		try {
			URL url = new URL("https://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt");
			Path path = Paths.get("C:\\Users\\Dafang\\Downloads\\dictionary.txt");
			Mission m = new MultithreadMission(url, path);
			m.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		*/
		
		try {
			URL url = new URL("http://abeautifulwww.com/NewWikipediaActivityVisualizations_AB91/07WikipediaPS3150DPI.png");
			Path path = Paths.get("C:\\Users\\Dafang\\Downloads\\07WikipediaPS3150DPI.png");
			Mission m = new MultithreadMission(url, path);
			m.start();

			long i = 5000000;

			while (m.getStatus() == Status.IN_PROGRESS) {

				if (m.getCurrentSize() > i) {
					m.pause();
					m.join();
					System.out.println("Mission status : " + m.getStatus());
					System.out.println("File size : " + path.toFile().length());
					System.out.println("Mission current progress : " + m.getCurrentSize());
					System.out.println("Mission total bytes : " + m.getTotalSize());
					i += 5000000;
					m.start();
				}
				Thread.yield();
			}
			m.join();
			System.out.println("Mission status : " + m.getStatus());
			System.out.println("File size : " + path.toFile().length());
			System.out.println("Mission current progress : " + m.getCurrentSize());
			System.out.println("Mission total bytes : " + m.getTotalSize());
			System.out.println("END");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
}
