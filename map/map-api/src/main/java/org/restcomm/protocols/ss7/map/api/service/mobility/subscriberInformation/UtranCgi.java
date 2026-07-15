package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation;

import org.restcomm.protocols.ss7.map.api.MAPException;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface UtranCgi extends Serializable {

    byte[] getData();

    int getMCC() throws Exception;

    int getMNC() throws Exception;

    int getUci() throws MAPException;
}
