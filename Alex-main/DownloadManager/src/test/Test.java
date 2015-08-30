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
			URL url = new URL("http://media.soundcloud.com/stream/m84jLCWdKDgA.mp3");
			Path path = Paths.get("C:\\Users\\Dafang\\Downloads\\m84jLCWdKDgA.mp3");
			SimpleMission m = new SimpleMission(url, path);
			m.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		*/
		
		try {
			URL url = new URL("http://seaside-c.jp/program/suzakinishi/net_radio/suzakinishi_112.wma");
			Path path = Paths.get("C:\\Users\\Dafang\\Downloads\\suzakinishi_112.wma");
			SimpleMission m = new SimpleMission(url, path);
			m.start();

			long i = 1000000;

			while (m.getStatus() == Status.IN_PROGRESS) {

				if (m.getCurrentSize() > i) {
					m.pause();
					m.join();
					System.out.println("Mission status : " + m.getStatus());
					System.out.println("File size : " + path.toFile().length());
					System.out.println("Mission current progress : " + m.getCurrentSize());
					System.out.println("Mission total bytes : " + m.getTotalSize());
					i += 1000000;
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
