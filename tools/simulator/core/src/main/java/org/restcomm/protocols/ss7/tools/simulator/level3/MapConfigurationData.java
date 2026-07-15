
package org.restcomm.protocols.ss7.tools.simulator.level3;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "mapConfigurationData")
public class MapConfigurationData {

    protected static final String LOCAL_SSN = "localSsn";
    protected static final String REMOTE_SSN = "remoteSsn";
    protected static final String REMOTE_ADDRESS_DIGITS = "remoteAddressDigits";
    protected static final String ORIG_REFERENCE = "origReference";
    protected static final String ORIG_REFERENCE_ADDRESS_NATURE = "origReferenceAddressNature";
    protected static final String ORIG_REFERENCE_NUMBERING_PLAN = "origReferenceNumberingPlan";
    protected static final String DEST_REFERENCE = "destReference";
    protected static final String DEST_REFERENCE_ADDRESS_NATURE = "destReferenceAddressNature";
    protected static final String DEST_REFERENCE_NUMBERING_PLAN = "destReferenceNumberingPlan";

    // private int localSsn;
    // private int remoteSsn;
    private String remoteAddressDigits;

    private String origReference;
    private AddressNature origReferenceAddressNature = AddressNature.international_number;
    private NumberingPlan origReferenceNumberingPlan = NumberingPlan.ISDN;
    private String destReference;
    private AddressNature destReferenceAddressNature = AddressNature.international_number;
    private NumberingPlan destReferenceNumberingPlan = NumberingPlan.ISDN;

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

    public String getOrigReference() {
        return origReference;
    }

    public void setOrigReference(String origReference) {
        this.origReference = origReference;
    }

    public AddressNature getOrigReferenceAddressNature() {
        return origReferenceAddressNature;
    }

    public void setOrigReferenceAddressNature(AddressNature origReferenceAddressNature) {
        this.origReferenceAddressNature = origReferenceAddressNature;
    }

    public NumberingPlan getOrigReferenceNumberingPlan() {
        return origReferenceNumberingPlan;
    }

    public void setOrigReferenceNumberingPlan(NumberingPlan origReferenceNumberingPlan) {
        this.origReferenceNumberingPlan = origReferenceNumberingPlan;
    }

    public String getDestReference() {
        return destReference;
    }

    public void setDestReference(String destReference) {
        this.destReference = destReference;
    }

    public AddressNature getDestReferenceAddressNature() {
        return destReferenceAddressNature;
    }

    public void setDestReferenceAddressNature(AddressNature destReferenceAddressNature) {
        this.destReferenceAddressNature = destReferenceAddressNature;
    }

    public NumberingPlan getDestReferenceNumberingPlan() {
        return destReferenceNumberingPlan;
    }

    public void setDestReferenceNumberingPlan(NumberingPlan destReferenceNumberingPlan) {
        this.destReferenceNumberingPlan = destReferenceNumberingPlan;
    }


}
