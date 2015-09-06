package panel;

import task.SimpleMission;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class MissionManager implements Serializable {

    private ArrayList<SimpleMission> missions;
    private transient DownloadTableModel model;

    public MissionManager() {
        missions = new ArrayList<>();
    }

    public void addMission(SimpleMission mission) {
        missions.add(mission);
        Object[] row = {mission.getPath(), mission.getUrl(), mission.getCurrentSize() / mission.getTotalSize()};
        model.addRow(row);
    }

    public void addMission(int index, SimpleMission mission) {
        missions.add(mission);
        Object[] row = {mission.getPath(), mission.getUrl(), mission.getCurrentSize() / mission.getTotalSize()};
        model.insertRow(index, row);
    }

    public void removeMission(int index, SimpleMission mission) {
        missions.remove(mission);
        model.removeRow(index);
    }

    public boolean contains(SimpleMission mission) {
        return missions.contains(mission);
    }

    public SimpleMission findMissionByURL(URL url) {
        for (SimpleMission m : missions) {
            if (m.getUrl().toString().equals(url.toString())) {
                return m;
            }
        }
        return null;
    }

    public void stopMissions() {
        for (SimpleMission m : missions) {
            m.pause();
            try {
                m.join();
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }
    }

    public void setModel(DownloadTableModel m) {
        model = m;
    }

    public void populateTable() {
        for (SimpleMission m : missions) {
            Object[] row = {m.getPath(), m.getUrl(), m.getCurrentSize() / m.getTotalSize() * 100};
            model.addRow(row);
        }
    }

    public int getSize() {
        return missions.size();
    }

    public SimpleMission get(int index) {
        return missions.get(index);
    }
}