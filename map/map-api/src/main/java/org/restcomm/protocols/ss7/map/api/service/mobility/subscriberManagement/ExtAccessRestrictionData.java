package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import java.io.Serializable;

/**
 *
 <code>
 Ext-AccessRestrictionData ::= BIT STRING {
 nrAsSecondaryRATNotAllowed (0),
 unlicensedSpectrumAsSecondaryRATNotAllowed (1) } (SIZE (1..32))
 </code>
 * <p>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface ExtAccessRestrictionData extends Serializable {

    boolean getNrAsSecondaryRATNotAllowed();

    boolean getUnlicensedSpectrumAsSecondaryRATNotAllowed();
}
