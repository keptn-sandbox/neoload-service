package com.keptn.neotys.testexecutor.kubernetes;

import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

public class NeoLoadKubernetesWather implements Watcher {
    KeptnLogger logger;

    public NeoLoadKubernetesWather(KeptnLogger logger) {
        this.logger = logger;
    }

    @Override
    public void eventReceived(Action action, Object o) {
        logger.debug(action.name() +" " + o.toString());
    }

    @Override
    public void onClose(KubernetesClientException e) {
        if (e != null) {
            logger.error(e.getMessage(), e);
        }
    }
}
