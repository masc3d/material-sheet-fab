package org.deku.leo2.rest.entities.v1;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Routing service find via response
 * Created by masc on 21.04.15.
 */
@XmlRootElement()
public class RoutingVia {
    private String[] mSectors;
    private String mLabelContent;

    public RoutingVia() {
    }

    public RoutingVia(String[] sectors) {
        mSectors = sectors;
        mLabelContent = String.join(";", sectors);
    }

    public String[] getSectors() {
        return mSectors;
    }

    public void setSectors(String[] sectors) {
        mSectors = sectors;
    }

    public String getLabelContent() {
        return mLabelContent;
    }

    public void setLabelContent(String labelContent) {
        mLabelContent = labelContent;
    }
}
