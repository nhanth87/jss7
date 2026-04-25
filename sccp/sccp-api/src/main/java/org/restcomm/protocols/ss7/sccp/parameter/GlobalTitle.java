
package org.restcomm.protocols.ss7.sccp.parameter;

import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author baranowb
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface GlobalTitle extends Parameter {

    /**
     * Defines fields included into the global title.
     *
     * @return
     */
    GlobalTitleIndicator getGlobalTitleIndicator();

    /**
     * Address string.
     *
     * @return
     */
    String getDigits();
}
