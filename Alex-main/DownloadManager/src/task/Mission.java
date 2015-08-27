package task;

import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;

public abstract class Mission implements Serializable {

	private static final long serialVersionUID = -6372000897518949968L;

	URL url;
	Path path;
	boolean inprogress = false;
	
	public abstract void start();

}
