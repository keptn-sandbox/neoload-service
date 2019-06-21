package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

public class NeoLoadTestStep {
    NeoLoadTest step;

    public NeoLoadTestStep(NeoLoadTest step) {
        this.step = step;
    }

    public NeoLoadTest getStep() {
        return step;
    }

    public void setStep(NeoLoadTest step) {
        this.step = step;
    }
}
