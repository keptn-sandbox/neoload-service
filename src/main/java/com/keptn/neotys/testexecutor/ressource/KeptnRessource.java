package com.keptn.neotys.testexecutor.ressource;

import io.netty.handler.codec.ByteToMessageCodec;
import org.apache.commons.codec.binary.Base64;

public class KeptnRessource {
    private String resourceURI;
    private String resourceContent;

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public String getResourceContent() {
        return resourceContent;
    }

    public void setResourceContent(String resourceContent) {
        this.resourceContent = resourceContent;
    }

    public KeptnRessource(String resourceURI, String resourceContent) {
        this.resourceURI = resourceURI;
        this.resourceContent = resourceContent;
    }

    public String getDecodedRessourceContent()
    {
        return  new String(Base64.decodeBase64(resourceContent.getBytes()));
    }
}
