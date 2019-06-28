package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

public class NeoLoadTestStep {
    NeoLoadTest step;

	public NeoLoadTestStep() {
		step = new NeoLoadTest();
	}

	public NeoLoadTestStep(NeoLoadTest step) {
        this.step = step;
    }

    public NeoLoadTest getStep() {
        return step;
    }

    public void setStep(NeoLoadTest step) {
        this.step = step;
    }

	@Override
	public String toString() {
		return "NeoLoadTestStep{" +
				"step=" + step +
				'}';
	}
}
