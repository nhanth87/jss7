package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 <code>
 ReportingOptionMilliseconds ::= SEQUENCE {
 reportingAmountMilliseconds    ReportingAmountMilliseconds,
 reportingIntervalMilliseconds  ReportingIntervalMilliseconds,
 ...}
 -- reportingAmountMilliseconds x reportingIntervalMilliseconds shall not exceed 8639999000
 -- (99 days, 23 hours, 59 minutes and 59 seconds) for compatibility with OMA MLP and RLP

 ReportingAmountMilliseconds ::= INTEGER (1..maxReportingAmountMilliseconds)
 maxReportingAmountMilliseconds INTEGER ::= 8639999000

 ReportingIntervalMilliseconds ::= INTEGER (1..maxReportingIntervalMilliseconds)
 maxReportingIntervalMilliseconds INTEGER ::= 999
 </code>
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface ReportingOptionMilliseconds extends Serializable {

    int getReportingAmountMilliseconds();

    int getReportingIntervalMilliseconds();
}
