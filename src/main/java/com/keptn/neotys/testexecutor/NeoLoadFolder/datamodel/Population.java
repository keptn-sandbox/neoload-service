package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Population {
    String name;
    List<NeoLoadLG> lgs;

	public Population() {
		lgs = new ArrayList<>();
	}

	public Population(String name, List<NeoLoadLG> lgs) {
        this.name = name;
        this.lgs = lgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NeoLoadLG> getLgs() {
        return lgs;
    }

    public void setLgs(List<NeoLoadLG> lgs) {
        this.lgs = lgs;
    }
}
