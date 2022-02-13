package app.contestTimetable.model.pocketlist;

import java.util.HashMap;
import java.util.Map;

public class LocationSummary {

//    private String location;

    private Integer contestid;


    private Map<String,Integer> contestitem = new HashMap<>();

    private Integer members;
    private Integer usbFlashDriveNumbers;


    public Integer getContestid() {
        return contestid;
    }

    public void setContestid(Integer contestid) {
        this.contestid = contestid;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Map<String, Integer> getContestitem() {
        return contestitem;
    }

    public void setContestitem(Map<String, Integer> contestitem) {
        this.contestitem = contestitem;
    }

    public Integer getUsbFlashDriveNumbers() {
        return usbFlashDriveNumbers;
    }

    public void setUsbFlashDriveNumbers(Integer usbFlashDriveNumbers) {
        this.usbFlashDriveNumbers = usbFlashDriveNumbers;
    }
}
