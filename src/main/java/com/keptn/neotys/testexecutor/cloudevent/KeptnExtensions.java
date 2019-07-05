package com.keptn.neotys.testexecutor.cloudevent;

import io.cloudevents.Extension;

import java.util.Objects;

public class KeptnExtensions implements Extension {

    private String shkeptncontext;
    private String contenttype;

    //Mandatory to let cloud-events lib instatiate class
    public KeptnExtensions() {
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
                '}';
    }
}
