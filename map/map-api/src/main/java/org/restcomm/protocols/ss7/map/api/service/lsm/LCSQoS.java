package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
 *
 *
 <code>
  LCS-QoS ::= SEQUENCE {
   horizontal-accuracy          [0] Horizontal-Accuracy OPTIONAL,
   verticalCoordinateRequest    [1] NULL                OPTIONAL,
   vertical-accuracy            [2] Vertical-Accuracy   OPTIONAL,
   responseTime                 [3] ResponseTime        OPTIONAL,
   extensionContainer           [4] ExtensionContainer  OPTIONAL,
   ...,
   velocityRequest              [5] NULL                OPTIONAL,
   lcs-qos-class                [6] LCS-QoS-Class       OPTIONAL
  }
  -- lcs-qos-class may only be included in MO-LR request sent by the UE to the network.
 </code>
 *
 * @author amit bhayani
 *
 */
public interface LCSQoS extends Serializable {

    /**
     * Horizontal-Accuracy ::= OCTET STRING (SIZE (1)) -- bit 8 = 0 -- bits 7-1 = 7 bit Uncertainty Code defined in 3GPP TS
     * 23.032. The horizontal location -- error should be less than the error indicated by the uncertainty code with 67% --
     * confidence.
     *
     */
    Integer getHorizontalAccuracy();

    boolean getVerticalCoordinateRequest();

    /**
     * Vertical-Accuracy ::= OCTET STRING (SIZE (1)) -- bit 8 = 0 -- bits 7-1 = 7 bit Vertical Uncertainty Code defined in 3GPP
     * TS 23.032. -- The vertical location error should be less than the error indicated -- by the uncertainty code with 67%
     * confidence.
     *
     */
    Integer getVerticalAccuracy();

    /**
     * ResponseTime ::= SEQUENCE {
     *  responseTimeCategory     ResponseTimeCategory,
     *  ...}
     * -- note: an expandable SEQUENCE simplifies later addition of a numeric response time.
     */
    ResponseTime getResponseTime();

    MAPExtensionContainer getExtensionContainer();

    boolean getVelocityRequest();

    /**
     * LCS-QoS-Class ::= ENUMERATED {
     *  bestEffort  (0),
     *  assured     (1),
     *  ... }
     *  -- exception handling:
     *  -- an unrecognized value shall be treated the same as value 0 (bestEffort)
     */
    LCSQoSClass getLCSQoSClass();

}
