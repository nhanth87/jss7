package org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement;

import java.io.Serializable;

/**
 *
 * Ext-SupportedFeatures ::= BIT STRING { unlicensedSpectrumAsSecondaryRAT (0) } (SIZE (1..40))
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface ExtSupportedFeatures extends Serializable {

    boolean isUnlicensedSpectrumAsSecondaryRAT();

}
