package test;

import task.SimpleMission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://seaside-c.jp/program/suzakinishi/net_radio/suzakinishi_112.wma");
			Path path = Paths.get("C:\\Users\\Dafang\\Downloads\\suzakinishi_112.wma");
			SimpleMission m = new SimpleMission(url, path);
			m.start();

			long i = 1000000;

			while (m.inProgress()) {

				if (m.current > i) {
					m.pause();
					m.join();
					System.out.println("Mission in progress : " + m.inProgress());
					System.out.println("Mission current progress : " + m.current);
					System.out.println("File size : " + path.toFile().length());
					System.out.println("Mission total bytes : " + m.total);
					i += 1000000;
					m.start();
				}
				Thread.yield();
			}
			m.join();
			System.out.println("END");
		} catch (MalformedURLException e) {
			// TODO
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
