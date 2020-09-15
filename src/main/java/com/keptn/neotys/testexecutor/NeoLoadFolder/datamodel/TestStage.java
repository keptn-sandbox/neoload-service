package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

public class TestStage {
    String ramp_up;
    String cool_down;
    String warm_up;


    public TestStage(String ramp_up, String cool_down, String warm_up) {
        this.ramp_up = ramp_up;
        this.cool_down = cool_down;
        this.warm_up = warm_up;
    }

    public String getRamp_up() {
        return ramp_up;
    }

    public void setRamp_up(String ramp_up) {
        this.ramp_up = ramp_up;
    }

    public String getCool_down() {
        return cool_down;
    }

    public void setCool_down(String cool_down) {
        this.cool_down = cool_down;
    }

    public String getWarm_up() {
        return warm_up;
    }

    public void setWarm_up(String warm_up) {
        this.warm_up = warm_up;
    }
}
