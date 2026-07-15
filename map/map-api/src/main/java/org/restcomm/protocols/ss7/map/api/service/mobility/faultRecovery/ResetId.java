package org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery;

import java.io.Serializable;

/**
 *
 Reset-Id ::= OCTET STRING (SIZE (1..4))
 -- Reset-Ids shall be unique within the HPLMN.
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface ResetId extends Serializable {

    byte[] getData();
}
