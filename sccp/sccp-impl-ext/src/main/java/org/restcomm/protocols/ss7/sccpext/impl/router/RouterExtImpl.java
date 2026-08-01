
package org.restcomm.protocols.ss7.sccpext.impl.router;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.jctools.maps.NonBlockingHashMap;
import org.restcomm.protocols.ss7.sccp.impl.SCCPJacksonXMLHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.indicator.AddressIndicator;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.RemoteSignalingPointCode;
import org.restcomm.protocols.ss7.sccp.Router;
import org.restcomm.protocols.ss7.sccp.Rule;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.SccpProtocolVersion;
import org.restcomm.protocols.ss7.sccp.SccpStack;
import org.restcomm.protocols.ss7.sccp.impl.oam.SccpOAMMessage;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0001Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0010Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0011Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccpext.impl.congestion.NetworkIdStateImpl;
import org.restcomm.protocols.ss7.sccpext.impl.congestion.SccpCongestionControl;
import org.restcomm.protocols.ss7.sccpext.router.RouterExt;

/**
*
* @author Amit Bhayani
* @author sergey vetyutnev
*
*/
public class RouterExtImpl implements RouterExt {
    private static final Logger logger = LogManager.getLogger(RouterImpl.class);

    private static final String SCCP_ROUTER_PERSIST_DIR_KEY = "sccprouter.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String PERSIST_FILE_NAME = "sccprouter4_ext.xml";

    private final StringBuilder persistFile = new StringBuilder();

    private String persistDir = null;

    private RuleComparatorFactory ruleComparatorFactory = null;
    // rule list
    private RuleMap<Integer, Rule> rulesMap = new RuleMap<Integer, Rule>();
    private SccpAddressMap<Integer, SccpAddressImpl> routingAddresses = new SccpAddressMap<Integer, SccpAddressImpl>();

    private final String name;
    private final SccpStack sccpStack;
    private final Router router;

    public RouterExtImpl(String name, SccpStack sccpStack, Router router) {
        this.name = name;
        this.sccpStack = sccpStack;
        this.router = router;
        this.ruleComparatorFactory = RuleComparatorFactory.getInstance("RuleComparatorFactory");
    }

    public String getName() {
        return name;
    }

    public String getPersistDir() {
        return persistDir;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
    }

    public void start() {
        this.persistFile.setLength(0);

        if (persistDir != null) {
            this.persistFile.append(persistDir).append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFile.append(System.getProperty(SCCP_ROUTER_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY)))
                    .append(File.separator).append(this.name).append("_").append(PERSIST_FILE_NAME);
        }

        logger.info(String.format("SCCP RouterExt configuration file path %s", persistFile.toString()));

        this.load();

        logger.info("Started SCCP Router");
    }

    public void stop() {
        this.store();
    }

    /**
     * Looks up rule for translation.
     *
     * @param calledParty called party address
     * @return the rule with match to the called party address
     */
    public Rule findRule(SccpAddress calledParty, SccpAddress callingParty, boolean isMtpOriginated, int msgNetworkId) {

        for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
            Rule rule = e.getValue();
            if (rule.matches(calledParty, callingParty, isMtpOriginated, msgNetworkId)) {
                return rule;
            }
        }
        return null;
    }

    @Override
    public Rule getRule(int id) {
        return this.rulesMap.get(id);
    }

    @Override
    public SccpAddress getRoutingAddress(int id) {
        return this.routingAddresses.get(id);
    }

    @Override
    public Map<Integer, Rule> getRules() {
        Map<Integer, Rule> rulesMapTmp = new HashMap<Integer, Rule>();
        rulesMapTmp.putAll(rulesMap);
        return rulesMapTmp;
    }

    @Override
    public Map<Integer, SccpAddress> getRoutingAddresses() {
        Map<Integer, SccpAddress> routingAddressesTmp = new HashMap<Integer, SccpAddress>();
        routingAddressesTmp.putAll(routingAddresses);
        return routingAddressesTmp;
    }

    @Override
    public void addRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            int pAddressId, int sAddressId, Integer newCallingPartyAddressAddressId, int networkId, SccpAddress patternCallingAddress) throws Exception {

        Rule ruleTmp = this.getRule(id);

        if (ruleTmp != null) {
            throw new Exception(SccpOAMMessage.RULE_ALREADY_EXIST);
        }

        int maskumberOfSecs = (mask.split("/").length - 1);
        int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

        if (maskumberOfSecs != patternNumberOfSecs) {
            throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
        }

        SccpAddress pAddress = this.getRoutingAddress(pAddressId);
        if (pAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
        }

        int primAddNumberOfSecs = (pAddress.getGlobalTitle().getDigits().split("/").length - 1);
        if (maskumberOfSecs != primAddNumberOfSecs) {
            throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
        }

        if (sAddressId != -1) {
            SccpAddress sAddress = this.getRoutingAddress(sAddressId);
            if (sAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
            }

            int secAddNumberOfSecs = (sAddress.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != secAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
            }
        }

        if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
            throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
        }

        synchronized (this) {
            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size() + 1)];
            int count = 0;

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void modifyRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            int pAddressId, int sAddressId, Integer newCallingPartyAddressAddressId, int networkId, SccpAddress patternCallingAddress) throws Exception {
        synchronized (this) {
            Rule ruleTmp = this.getRule(id);

            if (ruleTmp == null) {
                throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
            }

            int maskumberOfSecs = (mask.split("/").length - 1);
            int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

            if (maskumberOfSecs != patternNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
            }

            SccpAddress pAddress = this.getRoutingAddress(pAddressId);

            if (pAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
            }
            int primAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != primAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
            }

            if (sAddressId != -1) {
                SccpAddress sAddress = this.getRoutingAddress(sAddressId);
                if (sAddress == null) {
                    throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
                }
                int secAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
                if (maskumberOfSecs != secAddNumberOfSecs) {
                    throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
                }
            }

            if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
                throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
            }

            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size())];
            int count = 0;

            // Remove the old rule so that it doesn't overwrite the new modifications
            this.removeRule( id );

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void modifyRule(int id, RuleType ruleType, LoadSharingAlgorithm algo, OriginationType originationType, SccpAddress pattern, String mask,
            Integer pAddressId, Integer sAddressId, Integer newCallingPartyAddressAddressId, Integer networkId, SccpAddress patternCallingAddress) throws Exception {
        synchronized (this) {
            Rule ruleTmp = this.getRule(id);

            if (ruleTmp == null) {
                throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
            }

            if(networkId == null)
                networkId = ruleTmp.getNetworkId();
            if(newCallingPartyAddressAddressId == null)
                newCallingPartyAddressAddressId = ruleTmp.getNewCallingPartyAddressId();
            if(sAddressId == null)
                sAddressId = ruleTmp.getSecondaryAddressId();
            if(pAddressId == null)
                pAddressId = ruleTmp.getPrimaryAddressId();
            if(mask == null)
                mask = ruleTmp.getMask();
            if(originationType == null)
                originationType = ruleTmp.getOriginationType();
            if(algo == null)
                algo = ruleTmp.getLoadSharingAlgorithm();
            if(ruleType == null)
                ruleType = ruleTmp.getRuleType();

            int maskumberOfSecs = (mask.split("/").length - 1);
            int patternNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);

            if (maskumberOfSecs != patternNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PATTERN);
            }

            SccpAddress pAddress = this.getRoutingAddress(pAddressId);

            if (pAddress == null) {
                throw new Exception(String.format(SccpOAMMessage.NO_PRIMARY_ADDRESS, pAddressId));
            }
            int primAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
            if (maskumberOfSecs != primAddNumberOfSecs) {
                throw new Exception(SccpOAMMessage.SEC_MISMATCH_PRIMADDRESS);
            }

            if (sAddressId != -1) {
                SccpAddress sAddress = this.getRoutingAddress(sAddressId);
                if (sAddress == null) {
                    throw new Exception(String.format(SccpOAMMessage.NO_BACKUP_ADDRESS, sAddressId));
                }
                int secAddNumberOfSecs = (pattern.getGlobalTitle().getDigits().split("/").length - 1);
                if (maskumberOfSecs != secAddNumberOfSecs) {
                    throw new Exception(SccpOAMMessage.SEC_MISMATCH_SECADDRESS);
                }
            }

            if (sAddressId == -1 && ruleType != RuleType.SOLITARY) {
                throw new Exception(SccpOAMMessage.RULETYPE_NOT_SOLI_SEC_ADD_MANDATORY);
            }

            RuleImpl rule = new RuleImpl(ruleType, algo, originationType, pattern, mask, networkId, patternCallingAddress);
            rule.setPrimaryAddressId(pAddressId);
            rule.setSecondaryAddressId(sAddressId);
            rule.setNewCallingPartyAddressId(newCallingPartyAddressAddressId);

            rule.setRuleId(id);
            RuleImpl[] rulesArray = new RuleImpl[(this.rulesMap.size())];
            int count = 0;

            // Remove the old rule so that it doesn't overwrite the new modifications
            this.removeRule( id );

            for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
                Integer ruleId = e.getKey();
                RuleImpl ruleTemp1 = (RuleImpl) e.getValue();
                ruleTemp1.setRuleId(ruleId);
                rulesArray[count++] = ruleTemp1;
            }

            // add latest rule
            rulesArray[count++] = rule;

            // Sort
            Arrays.sort(rulesArray, this.ruleComparatorFactory.getRuleComparator());

            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            for (int i = 0; i < rulesArray.length; i++) {
                RuleImpl ruleTemp = rulesArray[i];
                newRule.put(ruleTemp.getRuleId(), ruleTemp);
            }
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void removeRule(int id) throws Exception {

        if (this.getRule(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.RULE_DOESNT_EXIST, name));
        }

        synchronized (this) {
            RuleMap<Integer, Rule> newRule = new RuleMap<Integer, Rule>();
            newRule.putAll(this.rulesMap);
            newRule.remove(id);
            this.rulesMap = newRule;
            this.store();
        }
    }

    @Override
    public void addRoutingAddress(int primAddressId, SccpAddress primaryAddress) throws Exception {

        if (this.getRoutingAddress(primAddressId) != null) {
            throw new Exception(SccpOAMMessage.ADDRESS_ALREADY_EXIST);
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, (SccpAddressImpl) primaryAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    @Override
    public void modifyRoutingAddress(int primAddressId, SccpAddress primaryAddress) throws Exception {
        if (this.getRoutingAddress(primAddressId) == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, (SccpAddressImpl) primaryAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    @Override
    public void modifyRoutingAddress(int primAddressId, Integer ai, Integer pc, Integer ssnValue, Integer tt, Integer npValue,
            Integer naiValue, String digits) throws Exception {
        RoutingIndicator ri;
        GlobalTitle gt = null;
        int dpc;
        int ssn;

        SccpAddressImpl sccpAddress = (SccpAddressImpl) this.getRoutingAddress(primAddressId);

        if (sccpAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        if(ai != null) {
            AddressIndicator aiObj = new AddressIndicator(ai.byteValue(), SccpProtocolVersion.ITU);
            ri = aiObj.getRoutingIndicator();
        } else {
            ri = sccpAddress.getAddressIndicator().getRoutingIndicator();
        }

        if(pc != null) {
            dpc = pc;
        } else {
            dpc = sccpAddress.getSignalingPointCode();
        }

        if(ssnValue != null) {
            ssn = ssnValue;
        } else {
            ssn = sccpAddress.getSubsystemNumber();
        }

        if(tt != null || npValue != null || naiValue != null || digits != null) {
            gt = modifyGt(tt, npValue, naiValue, digits, sccpAddress);
        } else {
            gt = sccpAddress.getGlobalTitle();
        }
        SccpAddressImpl modifiedSccpAddress = new SccpAddressImpl(ri, gt, dpc, ssn);

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.put(primAddressId, modifiedSccpAddress);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    public SccpAddress modifySccpAddress(SccpAddress sccpAddress, Integer ai, Integer pc, Integer ssnValue, Integer tt, Integer npValue,
            Integer naiValue, String digits) throws Exception {
        RoutingIndicator ri;
        GlobalTitle gt = null;
        int dpc;
        int ssn;

        if (sccpAddress == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        if(ai != null) {
            AddressIndicator aiObj = new AddressIndicator(ai.byteValue(), SccpProtocolVersion.ITU);
            ri = aiObj.getRoutingIndicator();
        } else {
            ri = sccpAddress.getAddressIndicator().getRoutingIndicator();
        }

        if(pc != null) {
            dpc = pc;
        } else {
            dpc = sccpAddress.getSignalingPointCode();
        }

        if(ssnValue != null) {
            ssn = ssnValue;
        } else {
            ssn = sccpAddress.getSubsystemNumber();
        }

        if(tt != null || npValue != null || naiValue != null || digits != null) {
            gt = modifyGt(tt, npValue, naiValue, digits, sccpAddress);
        } else {
            gt = sccpAddress.getGlobalTitle();
        }
        return new SccpAddressImpl(ri, gt, dpc, ssn);
    }

    private GlobalTitle modifyGt(Integer ttValue, Integer npValue, Integer naiValue, String digits, SccpAddress sccpAddress) {

        GlobalTitle gt = null;

        if(digits == null)
            digits = sccpAddress.getGlobalTitle().getDigits();

        NumberingPlan np = null;
        NatureOfAddress nai = null;
        Integer tt = null;

        if (naiValue != null)
            nai = NatureOfAddress.valueOf(naiValue);

        if(npValue != null)
            np = NumberingPlan.valueOf(npValue);

        if(ttValue != null)
            tt = ttValue;

        switch (sccpAddress.getGlobalTitle().getGlobalTitleIndicator()) {
            case GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY:
                if(nai == null)
                    nai = ((GlobalTitle0001Impl)sccpAddress.getGlobalTitle()).getNatureOfAddress();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, nai);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY:
                if(tt == null)
                    tt = ((GlobalTitle0010Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME:
                if(np == null)
                    np = ((GlobalTitle0011Impl)sccpAddress.getGlobalTitle()).getNumberingPlan();
                if(tt == null)
                    tt = ((GlobalTitle0011Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt, np, null);
                break;
            case GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS:
                if(nai == null)
                    nai = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getNatureOfAddress();
                if(np == null)
                    np = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getNumberingPlan();
                if(tt == null)
                    tt = ((GlobalTitle0100Impl)sccpAddress.getGlobalTitle()).getTranslationType();
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits, tt, np, null, nai);
                break;

            case NO_GLOBAL_TITLE_INCLUDED:
                gt = sccpStack.getSccpProvider().getParameterFactory().createGlobalTitle(digits);
                break;
        }
        return gt;
    }

    @Override
    public void removeRoutingAddress(int id) throws Exception {
        if (this.getRoutingAddress(id) == null) {
            throw new Exception(String.format(SccpOAMMessage.ADDRESS_DOESNT_EXIST, name));
        }

        synchronized (this) {
            SccpAddressMap<Integer, SccpAddressImpl> newPrimaryAddress = new SccpAddressMap<Integer, SccpAddressImpl>();
            newPrimaryAddress.putAll(this.routingAddresses);
            newPrimaryAddress.remove(id);
            this.routingAddresses = newPrimaryAddress;
            this.store();
        }
    }

    public void removeAllResources() {

        synchronized (this) {
            if (this.rulesMap.size() == 0 && this.routingAddresses.size() == 0)
                // no resources allocated - nothing to do
                return;

            rulesMap = new RuleMap<Integer, Rule>();
            routingAddresses = new SccpAddressMap<Integer, SccpAddressImpl>();

            // We store the cleared state
            this.store();
        }
    }

    public NonBlockingHashMap<Integer, NetworkIdState> getNetworkIdStateList() {
        return getNetworkIdList(-1);
    }

    public NonBlockingHashMap<Integer, NetworkIdState> getNetworkIdList(int affectedPc) {
        NonBlockingHashMap<Integer, NetworkIdState> res = new NonBlockingHashMap<Integer, NetworkIdState>();

        for (Map.Entry<Integer, Rule> e : this.rulesMap.entrySet()) {
            Rule rule = e.getValue();
            NetworkIdStateImpl networkIdState = getRoutingAddressStatusForRoutingRule(rule, affectedPc);
            if (networkIdState != null) {
                NetworkIdState prevNetworkIdState = res.get(rule.getNetworkId());
                if (prevNetworkIdState != null) {
                    if (prevNetworkIdState.isAvailable()) {
                        if (networkIdState.isAvailable()) {
                            if (prevNetworkIdState.getCongLevel() < networkIdState.getCongLevel()) {
                                res.put(rule.getNetworkId(), networkIdState);
                            }
                        } else {
                            res.put(rule.getNetworkId(), networkIdState);
                        }
                    }
                } else {
                    res.put(rule.getNetworkId(), networkIdState);
                }
            }
        }

        return res;
    }

    private NetworkIdStateImpl getRoutingAddressStatusForRoutingRule(Rule rule, int affectedPc) {
        SccpAddress translationAddressPri = getRoutingAddress(rule.getPrimaryAddressId());
        NetworkIdStateImpl rspStatusPri = getRoutingAddressStatusForRoutingAddress(translationAddressPri, affectedPc);

        if (rule.getRuleType() == RuleType.DOMINANT || rule.getRuleType() == RuleType.LOADSHARED) {
            SccpAddress translationAddressSec = getRoutingAddress(rule.getSecondaryAddressId());
            NetworkIdStateImpl rspStatusSec = getRoutingAddressStatusForRoutingAddress(translationAddressSec, affectedPc);

            if (rspStatusPri.isAffectedByPc() || rspStatusSec.isAffectedByPc()) {
                if (rule.getRuleType() == RuleType.DOMINANT) {
                    if (rspStatusPri.isAvailable())
                        return rspStatusPri;

                    return rspStatusSec;
                }
                if (rule.getRuleType() == RuleType.LOADSHARED) {
                    if (rspStatusPri.isAvailable()) {
                        if (rspStatusSec.isAvailable()) {
                            if (rspStatusPri.getCongLevel() >= rspStatusSec.getCongLevel())
                                return rspStatusPri;
                            else
                                return rspStatusSec;
                        } else {
                            return rspStatusPri;
                        }
                    } else {
                        if (rspStatusSec.isAvailable()) {
                            return rspStatusSec;
                        } else {
                            // both are prohibited - we can select any response
                            return rspStatusPri;
                        }
                    }
                }
            } else {
                return null;
            }
        } else {
            if (rspStatusPri.isAffectedByPc())
                return rspStatusPri;
            else
                return null;
        }

        return null;
    }

    private NetworkIdStateImpl getRoutingAddressStatusForRoutingAddress(SccpAddress routingAddress, int affectedPc) {
        if (routingAddress != null && routingAddress.getAddressIndicator().isPCPresent()) {
            boolean affectedByPc = true;
            if ((affectedPc >= 0 && routingAddress.getSignalingPointCode() != affectedPc))
                affectedByPc = false;
            boolean spcIsLocal = router.spcIsLocal(routingAddress.getSignalingPointCode());
            if (spcIsLocal) {
                return new NetworkIdStateImpl(affectedByPc);
            }

            RemoteSignalingPointCode remoteSpc = sccpStack.getSccpResource().getRemoteSpcByPC(
                    routingAddress.getSignalingPointCode());
            if (remoteSpc == null) {
                return new NetworkIdStateImpl(affectedByPc);
            }
            if (remoteSpc.isRemoteSpcProhibited()) {
                return new NetworkIdStateImpl(false, affectedByPc);
            }
            int congLevel = SccpCongestionControl.generateSccpUserCongLevel(remoteSpc.getCurrentRestrictionLevel());
            if (congLevel > 0) {
                return new NetworkIdStateImpl(congLevel, affectedByPc);
            }
            return new NetworkIdStateImpl(affectedByPc);
        }

        // we return here value that this affectedPc does not affect this rule
        return new NetworkIdStateImpl(false);
    }

    /**
     * Configuration holder for RouterExt persistence (Jackson XML).
     */
    @JacksonXmlRootElement(localName = "sccpRouterExt")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouterConfig {
        @JacksonXmlProperty
        @JsonDeserialize(contentAs = RuleImpl.class)
        public RuleMap<Integer, Rule> rulesMap;

        @JacksonXmlProperty
        public SccpAddressMap<Integer, SccpAddressImpl> routingAddresses;
    }

    /**
     * Persist
     */
    public void store() {
        try {
            if (persistFile.length() == 0) {
                logger.warn("Skipping SCCP RouterExt persist: persist path not initialized (call start() first)");
                return;
            }
            RouterConfig config = new RouterConfig();
            config.rulesMap = this.rulesMap;
            config.routingAddresses = this.routingAddresses;

            try (Writer writer = new FileWriter(persistFile.toString())) {
                // Use helper (Integer map keys → <kN>), not raw XmlMapper.
                SCCPJacksonXMLHelper.toXML(config, writer);
            }
        } catch (Exception e) {
            logger.error("Error while persisting the Rule state in file", e);
        }
    }

    /**
     * Load the router configuration from the persisted Jackson XML file.
     */
    protected void load() {
        File f = new File(persistFile.toString());
        if (!f.exists()) {
            return;
        }

        try (Reader reader = new FileReader(f)) {
            // fromXML sanitizes legacy illegal <N> map-key element names → <kN>.
            RouterConfig config = SCCPJacksonXMLHelper.fromXML(reader, RouterConfig.class);
            if (config != null) {
                if (config.rulesMap != null) {
                    this.rulesMap = config.rulesMap;
                }
                if (config.routingAddresses != null) {
                    this.routingAddresses = config.routingAddresses;
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to load the SS7 configuration file. \n%s", e.getMessage()), e);
        }
    }

    /**
     * No-op retained for API compatibility. Legacy on-disk config migration is no
     * longer supported after the migration to Jackson XML persistence (clean break).
     */
    public static void makeOldConfigCopy(String persistDir, String name) {
        // clean break: nothing to migrate from the old config formats
    }
}
