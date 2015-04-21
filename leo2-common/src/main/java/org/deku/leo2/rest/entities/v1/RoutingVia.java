package org.deku.leo2.rest.entities.v1;

/**
 * Created by masc on 21.04.15.
 */
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
