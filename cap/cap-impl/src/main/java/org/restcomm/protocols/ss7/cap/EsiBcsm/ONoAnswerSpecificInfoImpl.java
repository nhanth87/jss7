
package org.restcomm.protocols.ss7.cap.EsiBcsm;

import java.io.IOException;


import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.CAPException;
import org.restcomm.protocols.ss7.cap.api.CAPParsingComponentException;
import org.restcomm.protocols.ss7.cap.api.EsiBcsm.ONoAnswerSpecificInfo;
import org.restcomm.protocols.ss7.cap.primitives.SequenceBase;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
@XStreamAlias("oNoAnswerSpecificInfo")
 extends SequenceBase implements ONoAnswerSpecificInfo {

    public ONoAnswerSpecificInfoImpl() {
        super("ONoAnswerSpecificInfo");
    }

    protected void _decode(AsnInputStream asnInputStream, int length) throws CAPParsingComponentException, MAPParsingComponentException,
            IOException, AsnException {

        AsnInputStream ais = asnInputStream.readSequenceStreamData(length);
        while (true) {
            if (ais.available() == 0)
                break;

            int tag = ais.readTag();

            ais.advanceElement();
        }
    }

    @Override
    public void encodeData(AsnOutputStream asnOutputStream) throws CAPException {
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");
        sb.append("]");

        return sb.toString();
    }
}
