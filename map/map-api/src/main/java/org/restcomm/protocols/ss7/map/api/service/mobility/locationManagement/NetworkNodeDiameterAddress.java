package org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement;

import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;

import java.io.Serializable;

/**
 *
 * NetworkNodeDiameterAddress::= SEQUENCE { diameter-Name [0] DiameterIdentity, diameter-Realm [1] DiameterIdentity }
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface NetworkNodeDiameterAddress extends Serializable {

    DiameterIdentity getDiameterName();
    DiameterIdentity getDiameterRealm();
}
