package org.onosproject.sdnwise.protocol;

/**
 * Created by aca on 2/14/15.
 */
public enum SDNWiseVersion {
    SDN_WISE(0.2);

    public final double sdnWiseVersion;

    private SDNWiseVersion(double sdnWiseVersion) {
        this.sdnWiseVersion = sdnWiseVersion;
    }

    public double getSDNWiseVersion() {
        return this.sdnWiseVersion;
    }
}
