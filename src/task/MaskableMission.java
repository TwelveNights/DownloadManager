package task;

import java.io.File;
import java.net.URL;

import exception.IncorrectMissionStateException;

public interface MaskableMission extends Mission {

	public void setUrl(URL url);

	public void setFile(File file);

}
