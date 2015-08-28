import task.SimpleMission;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) {
		try {
			new SimpleMission(new URL("https://docs.oracle.com/javase/tutorial/essential/io/examples/xanadu.txt"),
					Paths.get("C:\\Users\\Dafang\\Downloads\\xanadu.txt")).start();
		} catch (MalformedURLException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
