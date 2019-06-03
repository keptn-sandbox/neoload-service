package com.keptn.neotys.testexecutor.messageHandler;

import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import io.cloudevents.CloudEvent;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.GITHUB;

public class NeoLoadHandler {

    Logger logger;
    private CloudEvent<KeptnEventFinished> keptnevent;

    public NeoLoadHandler(CloudEvent<KeptnEventFinished> keptnEventFinishedCloudEvent)
    {
        this.keptnevent=keptnEventFinishedCloudEvent;
        LogManager.getLogManager().reset();
        logger = LogManager.getLogManager().getLogger("");
    }

    private void getNeoLoaddata()
    {


        try {
            Path localPath = Files.createTempDirectory("Gitfolder_" + keptnevent.getId());

            Git result = Git.cloneRepository()
                .setURI(getGitHubFolder())
                .setDirectory(localPath.toFile())
                .setBranch(keptnevent.getData().get().getStage())
                .call();

            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            logger.info("Having repository: " + result.getRepository().getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (NeoLoadJgitExeption neoLoadJgitExeption) {
            neoLoadJgitExeption.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }


    }

    private boolean hasNeoLoadFolder(Path path)
    {
        //#TODO : Check if repo has reoLoad folder
        return true;
    }

    private boolean hasNeoLoadKeptn(Path path)
    {
        return true;
    }

    private String getGitHubFolder() throws IOException, NeoLoadJgitExeption {
        String gitFolder;

        if (keptnevent.getData().isPresent()) {
            KeptnEventFinished finished = keptnevent.getData().get();
            gitFolder = GITHUB + finished.getGithuborg() + "/" + finished.getService() +".git";
            return gitFolder;

        } else {
            throw new NeoLoadJgitExeption("No data in Event");

        }

    }

}
