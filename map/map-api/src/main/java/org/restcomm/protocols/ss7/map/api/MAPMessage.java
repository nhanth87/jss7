
package org.restcomm.protocols.ss7.map.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This is super interface for all service message in MAP
 *
 * @author amit bhayani
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface MAPMessage extends Serializable {

    long getInvokeId();

    void setInvokeId(long invokeId);

    MAPDialog getMAPDialog();

    void setMAPDialog(MAPDialog mapDialog);

    MAPMessageType getMessageType();

    int getOperationCode();

    boolean isReturnResultNotLast();

}
