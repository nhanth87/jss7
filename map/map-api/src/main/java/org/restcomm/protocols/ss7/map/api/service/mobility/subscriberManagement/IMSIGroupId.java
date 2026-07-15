package org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement;


import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;

import java.io.Serializable;

/**
 *
 <code>
   group-Service-Id [0] Group-Service-ID,
   plmnId           [1] PLMN-Id,
   local-Group-ID   [2] Local-GroupID,
 }
 </code>
 * <p>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface IMSIGroupId extends Serializable {

    Long getGroupServiceId();

    PlmnId getPLMNId();

    LocalGroupId getLocalGroupId();
}
