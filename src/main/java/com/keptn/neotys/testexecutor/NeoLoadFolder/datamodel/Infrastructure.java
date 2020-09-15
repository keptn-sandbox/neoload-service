package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Infrastructure {
    Integer numberOfMachine;
    String zoneId;
    boolean managedbyKeptn;

    public Infrastructure(Integer numberOfMachine, String zoneId, boolean managedbyKeptn) {
        this.numberOfMachine = numberOfMachine;
        this.zoneId = zoneId;
        this.managedbyKeptn = managedbyKeptn;
    }
    public Infrastructure()
    {

    }

    public Integer getNumberOfMachine() {
        return numberOfMachine;
    }

    public void setNumberOfMachine(Integer numberOfMachine) {
        this.numberOfMachine = numberOfMachine;
    }


    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public boolean isManagedbyKeptn() {
        return managedbyKeptn;
    }

    public void setManagedbyKeptn(boolean managedbyKeptn) {
        this.managedbyKeptn = managedbyKeptn;
    }

    @Override
    public String toString() {
        return "Infrastructure{" +
                "managedbyKeptn=" + String.valueOf(managedbyKeptn) +
                ", zoneId ="+ zoneId+
                "numberOfMachine="+String.valueOf(numberOfMachine)+
                '}';
    }
}
