package panel;

import task.SimpleMission;

import java.io.Serializable;
import java.util.ArrayList;

public class MissionManager implements Serializable {
    ArrayList<SimpleMission> missions;
    private static MissionManager instance = null;

    private MissionManager() {
        missions = new ArrayList<>();
    }

    public static MissionManager getInstance() {
        if (instance == null)
            instance = new MissionManager();

        return instance;
    }

    public void addMission(SimpleMission mission) {
        missions.add(mission);
    }

    public void removeMission(SimpleMission mission) {
        missions.remove(mission);
    }
}
