package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.List;
import java.util.Optional;

public class Infrastructure {
    List<NeoLoadLG> Local_LG;
    List<Population> populations;

    public Infrastructure(List<NeoLoadLG> local_LG, List<Population> populations) {
        Local_LG = local_LG;
        this.populations = populations;
    }

    public List<NeoLoadLG> getLocal_LG() {
        return Local_LG;
    }

    public void setLocal_LG(List<NeoLoadLG> local_LG) {
        Local_LG = local_LG;
    }

    public List<Population> getPopulations() {
        return populations;
    }

    public void setPopulations(List<Population> populations) {
        this.populations = populations;
    }
}
