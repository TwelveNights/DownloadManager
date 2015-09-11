package panel;

import task.Mission;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Methods in this class is not thread-safe. This is true even for methods that
 * have "safely" in their names.
 */
public class MissionManager implements Serializable {

	private static final long serialVersionUID = -6993535597006106378L;

	private ArrayList<Mission> missions;

	public MissionManager() {
		missions = new ArrayList<Mission>();
	}

	public boolean addMission(Mission mission) {
		return missions.add(mission);
	}

	public void addMission(int index, Mission mission) {
		missions.add(index, mission);
	}

	public boolean removeMission(Mission mission) {
		return missions.remove(mission);
	}

	public Mission removeMission(int index) {
		return missions.remove(index);
	}

	public boolean safelyRemoveMission(Mission mission) throws InterruptedException {
		if (missions.remove(mission)) {
			mission.pause();
			mission.join();
			return true;
		} else
			return false;
	}

	public Mission safelyRemoveMission(int index) throws InterruptedException {
		Mission mission = removeMission(index);
		mission.pause();
		mission.join();
		return mission;
	}

	public boolean safelyAbortMission(Mission mission) throws InterruptedException, IOException {
		if (safelyRemoveMission(mission))
			return Files.deleteIfExists(mission.getPath());
		else
			return false;
	}

	public Mission safelyAbortMission(int index) throws InterruptedException, IOException {
		Mission mission = safelyRemoveMission(index);
		Files.deleteIfExists(mission.getPath());
		return mission;
	}

	public boolean contains(Mission mission) {
		return missions.contains(mission);
	}

	public void stopMissions() {
		for (Mission m : missions) {
			m.pause();
		}
	}

	public void joinMissions() {
		for (Mission m : missions) {
			try {
				m.join();
			} catch (InterruptedException i) {
				i.printStackTrace();
			}
		}
	}

	public int getSize() {
		return missions.size();
	}

	public Mission get(int index) {
		return missions.get(index);
	}

	public Stream<Mission> getMissionStream() {
		return missions.stream();
	}

}