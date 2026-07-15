
package org.restcomm.protocols.ss7.tools.simulator.level3;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "capConfigurationData")
public class CapConfigurationData {

    // protected static final String LOCAL_SSN = "localSsn";
    // protected static final String REMOTE_SSN = "remoteSsn";
    protected static final String REMOTE_ADDRESS_DIGITS = "remoteAddressDigits";

    // private int localSsn;
    // private int remoteSsn;
    private String remoteAddressDigits;

    // public int getLocalSsn() {
    // return localSsn;
    // }
    //
    // public void setLocalSsn(int localSsn) {
    // this.localSsn = localSsn;
    // }
    //
    // public int getRemoteSsn() {
    // return remoteSsn;
    // }
    //
    // public void setRemoteSsn(int remoteSsn) {
    // this.remoteSsn = remoteSsn;
    // }

    public String getRemoteAddressDigits() {
        return remoteAddressDigits;
    }

    public void setRemoteAddressDigits(String remoteAddressDigits) {
        this.remoteAddressDigits = remoteAddressDigits;
    }


}
