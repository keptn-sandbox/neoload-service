package com.keptn.neotys.testexecutor.cloudevent;

import io.cloudevents.Extension;

import java.util.Objects;
import java.util.Optional;

public class KeptnExtensions implements Extension {

    private String shkeptncontext;
    private String contenttype;
    private String triggeredid;
    private String shkeptnspecversion;



    //Mandatory to let cloud-events lib instatiate class
    public KeptnExtensions()
    {

    }
    public KeptnExtensions(String shkeptncontext, Optional<String> contenttype,  Optional<String> triggeredid,  Optional<String> shkeptnspecversion) {
        if(contenttype.isPresent()) {
            this.contenttype = contenttype.get();
            this.contenttype=this.contenttype.replaceAll("\"","");
        }
        if(triggeredid.isPresent()) {
            this.triggeredid = triggeredid.get();
            this.triggeredid=this.triggeredid.replaceAll("\"","");
        }
        this.shkeptncontext=shkeptncontext;

        if(shkeptnspecversion.isPresent()) {
            this.shkeptnspecversion = shkeptnspecversion.get();
            this.shkeptnspecversion=this.shkeptnspecversion.replaceAll("\"","");
        }
    }

    public KeptnExtensions(final String shkeptncontext, final String datacontenttype) {
        this.shkeptncontext = shkeptncontext;
        this.contenttype = datacontenttype;
    }
    public String getShkeptncontext() {
        return shkeptncontext;
    }

    public void setShkeptncontext(String shkeptncontext) {
        this.shkeptncontext = shkeptncontext;
    }

    public String getDatacontenttype() {
        return contenttype;
    }

    public void setDatacontenttype(String datacontenttype) {
        this.contenttype = datacontenttype;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getTriggeredid() {
        return triggeredid;
    }

    public void setTriggeredid(String triggeredid) {
        this.triggeredid = triggeredid;
    }

    public String getShkeptnspecversion() {
        return shkeptnspecversion;
    }

    public void setShkeptnspecversion(String shkeptnspecversion) {
        this.shkeptnspecversion = shkeptnspecversion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shkeptncontext);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final KeptnExtensions other = (KeptnExtensions) obj;
        return Objects.equals(this.shkeptncontext, other.shkeptncontext);
    }

    @Override
    public String toString() {
        return "KeptExtension{" +
                "shkeptncontext='" + shkeptncontext + '\'' +
                ", datacontenttype='" + contenttype + '\'' +
                ", triggeredid='" + triggeredid + '\'' +
                ", shkeptnspecversion='" + shkeptnspecversion + '\'' +

                '}';
    }
}
