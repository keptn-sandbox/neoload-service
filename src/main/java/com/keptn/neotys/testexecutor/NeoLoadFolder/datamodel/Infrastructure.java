package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Infrastructure {
    List<NeoLoadLG> local_LG;
    List<Population> populations;

	public Infrastructure() {
		local_LG = new ArrayList<>();
		populations = new ArrayList<>();
	}

	public Infrastructure(List<NeoLoadLG> local_LG, List<Population> populations) {
        this.local_LG = local_LG;
        this.populations = populations;
    }

    public List<NeoLoadLG> getLocal_LG() {
        return local_LG;
    }

    public void setLocal_LG(List<NeoLoadLG> local_LG) {
        this.local_LG = local_LG;
    }

    public List<Population> getPopulations() {
        return populations;
    }

    public void setPopulations(List<Population> populations) {
        this.populations = populations;
    }
}
