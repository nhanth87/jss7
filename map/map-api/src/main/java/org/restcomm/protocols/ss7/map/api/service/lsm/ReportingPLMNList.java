package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 <code>
 ReportingPLMNList::= SEQUENCE {
  plmn-ListPrioritized  [0] NULL       OPTIONAL,
  plmn-List             [1] PLMNList,
  ...}

   PLMNList::= SEQUENCE SIZE (1..maxNumOfReportingPLMN) OF ReportingPLMN
    maxNumOfReportingPLMN INTEGER ::= 20

   ReportingPLMN::= SEQUENCE {
    plmn-Id                        [0] PLMN-Id,
    ran-Technology                 [1] RAN-Technology  OPTIONAL,
    ran-PeriodicLocationSupport    [2] NULL            OPTIONAL,
    ...}

 </code>
 ReportingPLMNList::= SEQUENCE { plmn-ListPrioritized [0] NULL OPTIONAL, plmn-List [1] PLMNList, ...}
 *
 * PLMNList::= SEQUENCE SIZE (1..20) OF ReportingPLMN
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface ReportingPLMNList extends Serializable {

    boolean getPlmnListPrioritized();

    ArrayList<ReportingPLMN> getPlmnList();

}
