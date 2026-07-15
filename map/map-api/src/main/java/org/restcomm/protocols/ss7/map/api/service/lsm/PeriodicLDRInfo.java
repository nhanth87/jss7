package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 <code>
  PeriodicLDRInfo ::= SEQUENCE {
   reportingAmount   ReportingAmount,
   reportingInterval ReportingInterval,
   ...,
   reportingOptionMilliseconds [0] ReportingOptionMilliseconds OPTIONAL
 }
 -- reportingInterval x reportingAmount shall not exceed 8639999 (99 days, 23 hours,59 minutes and 59 seconds)
    for compatibility with OMA MLP and RLP
 -- When reportingOptionMilliseconds is provided and supported, reportingInterval & reportingAmount shall be ignored.

   ReportingAmount ::= INTEGER (1..maxReportingAmount)
   ReportingAmount ::= INTEGER (1..8639999)

   ReportingInterval ::= INTEGER (1..maxReportingInterval)
    -- ReportingInterval is in seconds
    ReportingInterval ::= INTEGER (1..8639999)

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
 * @author sergey vetyutnev
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public interface PeriodicLDRInfo extends Serializable {

    int getReportingAmount();

    int getReportingInterval();

    ReportingOptionMilliseconds getReportingOptionMilliseconds();
}
