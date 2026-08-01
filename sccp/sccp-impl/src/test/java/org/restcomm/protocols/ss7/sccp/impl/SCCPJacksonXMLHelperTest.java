package org.restcomm.protocols.ss7.sccp.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;
import org.restcomm.protocols.ss7.sccp.impl.router.LongMessageRuleImpl;
import org.restcomm.protocols.ss7.sccp.impl.router.LongMessageRuleMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3ServiceAccessPointMap;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;
import org.testng.annotations.Test;

/**
 * Persist XML must never emit illegal {@code <1>} map-key element names.
 */
public class SCCPJacksonXMLHelperTest {

    @Test(groups = { "sccp", "functional.encode" })
    public void sanitizeRewritesNumericElementNames() {
        String legacy = "<RouterConfig><longMessageRules><1><firstSpc>1</firstSpc></1></longMessageRules></RouterConfig>";
        String san = SCCPJacksonXMLHelper.sanitizeNumericElementNames(legacy);
        assertTrue(san.contains("<k1>"));
        assertFalse(san.matches("(?s).*</?\\d+>.*"));
    }

    @Test(groups = { "sccp", "functional.encode" })
    public void integerMapKeysRoundTripAsKN() throws Exception {
        RouterImpl.RouterConfig cfg = new RouterImpl.RouterConfig();
        cfg.longMessageRules = new LongMessageRuleMap<>();
        cfg.longMessageRules.put(1, new LongMessageRuleImpl(1, 16384, LongMessageRuleType.XUDT_ENABLED));
        cfg.saps = new Mtp3ServiceAccessPointMap<>();

        String xml = SCCPJacksonXMLHelper.toXML(cfg);
        assertTrue(xml.contains("<k1>"), xml);
        assertFalse(xml.matches("(?s).*</?\\d+>.*"), xml);

        RouterImpl.RouterConfig loaded = SCCPJacksonXMLHelper.fromXML(xml, RouterImpl.RouterConfig.class);
        assertEquals(loaded.longMessageRules.size(), 1);
        assertTrue(loaded.longMessageRules.containsKey(1));

        // legacy bare <1> still loads via sanitize-on-read
        String legacy = xml.replace("<k1>", "<1>").replace("</k1>", "</1>");
        RouterImpl.RouterConfig fromLegacy = SCCPJacksonXMLHelper.fromXML(legacy, RouterImpl.RouterConfig.class);
        assertTrue(fromLegacy.longMessageRules.containsKey(1));
    }
}
