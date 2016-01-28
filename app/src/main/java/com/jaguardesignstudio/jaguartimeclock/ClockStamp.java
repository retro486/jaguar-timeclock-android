package com.jaguardesignstudio.jaguartimeclock;

import java.util.Date;

// TODO make this serializable

/**
 * Created by RustyPowerhouse on 1/26/2016.
 */
public class ClockStamp {
    private String status;
    private Date lastTime;

    public ClockStamp(String mStatus, Date mLastTime) {
        this.status = mStatus;
        this.lastTime = mLastTime;
    }

    public String getStatus() {
        return status;
    }

    public Date getLastTime() {
        return lastTime;
    }
}