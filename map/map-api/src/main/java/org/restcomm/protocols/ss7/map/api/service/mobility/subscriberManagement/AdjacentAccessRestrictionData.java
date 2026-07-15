package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;

import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;

import java.io.Serializable;

/**
 *
 <code>
   AdjacentAccessRestrictionData ::= SEQUENCE {
   plmnId [0] PLMN-Id,
   accessRestrictionData [1] AccessRestrictionData,
   ... ,
   ext-AccessRestrictionData [2] Ext-AccessRestrictionData OPTIONAL
   }
 </code>
 * <p>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface AdjacentAccessRestrictionData extends Serializable {

    PlmnId getPLMNId();

    AccessRestrictionData getAccessRestrictionData();

    ExtAccessRestrictionData getExtAccessRestrictionData();
}
