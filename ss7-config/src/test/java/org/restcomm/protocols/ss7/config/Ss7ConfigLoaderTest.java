/*
 * jSS7 :: ss7-config :: tests
 */
package org.restcomm.protocols.ss7.config;

import junit.framework.TestCase;

import java.io.InputStream;
import java.util.List;

/**
 * Loader tests written in JUnit-3 style (the ss7-parent tree pins junit 3.8.1),
 * so they extend {@link TestCase} and assert exceptions with try/fail/catch.
 */
public class Ss7ConfigLoaderTest extends TestCase {

    private Ss7Config example() {
        try (InputStream in = getClass().getResourceAsStream("/ss7-example.json")) {
            assertNotNull("ss7-example.json must be on the classpath", in);
            return Ss7ConfigLoader.load(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── happy path: the annotated template ────────────────────
    public void testParseAnnotatedExample() {
        Ss7Config c = example();
        assertEquals("ss7", c.stackName());
        assertTrue(c.protocols().map());
        assertFalse(c.protocols().cap());

        assertEquals(2, c.sctp().links().size());
        Ss7Config.Link l0 = c.sctp().links().get(0);
        assertEquals("STPT1A", l0.name());
        assertTrue("multi-homing secondary IP preserved",
                l0.localSecondary().contains("10.155.183.171"));

        assertEquals(1, c.m3ua().as().size());
        assertEquals(2, c.m3ua().as().get(0).links().size());

        assertEquals(1, c.sccp().localPoints().size());
        assertEquals(4773, c.sccp().localPoints().get(0).pc());
        assertEquals(2, c.sccp().localPoints().get(0).reachablePointCodes().size());
        assertEquals(5, c.sccp().routing().size());

        assertEquals(2, c.services().size());
    }

    // comments (// and #) must be tolerated in a config file
    public void testCommentsAreAllowed() {
        Ss7Config c = example(); // the template file is full of // comments
        assertNotNull(c);
    }

    // ── multi-SSN helpers ─────────────────────────────────────
    public void testMultiSsnHelpers() {
        Ss7Config c = example();
        assertEquals(List.of(6, 8), Ss7ConfigLoader.allSsns(c));
        assertEquals(6, Ss7ConfigLoader.primarySsn(c));
        assertEquals(List.of(8), Ss7ConfigLoader.extraSsns(c));
    }

    // ── multi-RC bind (routingContexts preferred over single routingContext) ──
    public void testRoutingContextsPreferredOverSingle() {
        String json = """
            {
              "sctp": { "links": [ { "name": "L1", "local": "1.1.1.1:1", "peer": "2.2.2.2:2" } ] },
              "m3ua": { "as": [ {
                  "name": "AS1", "mode": "loadshare", "links": ["L1"],
                  "routingContext": 12,
                  "routingContexts": [12, 13]
                } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ]
            }
            """;
        Ss7Config c = Ss7ConfigLoader.parse(json);
        Ss7Config.As as = c.m3ua().as().get(0);
        assertEquals(Long.valueOf(12L), as.routingContext());
        assertEquals(List.of(12L, 13L), as.routingContexts());
        long[] resolved = Ss7StackBuilder.resolveRoutingContexts(as);
        assertEquals(2, resolved.length);
        assertEquals(12L, resolved[0]);
        assertEquals(13L, resolved[1]);
    }

    public void testSingleRoutingContextFallback() {
        String json = """
            {
              "sctp": { "links": [ { "name": "L1", "local": "1.1.1.1:1", "peer": "2.2.2.2:2" } ] },
              "m3ua": { "as": [ {
                  "name": "AS1", "mode": "loadshare", "links": ["L1"],
                  "routingContext": 12
                } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ]
            }
            """;
        Ss7Config c = Ss7ConfigLoader.parse(json);
        Ss7Config.As as = c.m3ua().as().get(0);
        assertEquals(Long.valueOf(12L), as.routingContext());
        assertTrue(as.routingContexts() == null || as.routingContexts().isEmpty());
        long[] resolved = Ss7StackBuilder.resolveRoutingContexts(as);
        assertEquals(1, resolved.length);
        assertEquals(12L, resolved[0]);
    }

    // ── derived defaults ──────────────────────────────────────
    public void testDefaultsApplied() {
        String json = """
            {
              "sccp": { "localPoints": [ { "pc": 100, "networkId": 0 } ],
                        "routing": [ { "match": { "ssn": 6 }, "to": { "pc": 100, "ssn": 6 } } ] },
              "services": [ { "name": "hlr", "ssn": 6, "protocol": "map" } ]
            }
            """;
        Ss7Config c = Ss7ConfigLoader.parse(json);
        assertEquals(8, c.sctp().workerThreads());          // default
        assertEquals(5000, c.tcap().maxDialogs());          // default
        assertTrue(c.protocols().map());                    // default true
        assertEquals("national", c.sccp().localPoints().get(0).networkIndicator());
        Ss7Config.Rule r = c.sccp().routing().get(0);
        assertEquals("all", r.from());                      // default
        assertEquals("K", r.mask());                        // default
        assertEquals("*", r.match().gt());                  // default wildcard
    }

    // ── full VNPT translation validates ───────────────────────
    public void testVnptTranslationValidates() {
        try (InputStream in = getClass().getResourceAsStream("/ss7-vnpt.json")) {
            assertNotNull(in);
            Ss7Config c = Ss7ConfigLoader.load(in);
            assertEquals("VNPT", c.stackName());
            assertEquals(12, c.sctp().links().size());
            assertEquals(12, c.m3ua().as().get(0).links().size());
            assertEquals(6, c.sccp().localPoints().get(0).reachablePointCodes().size());
            assertEquals(12, c.sccp().routing().size());
        } catch (Exception e) {
            if (e instanceof Ss7ConfigException) throw (Ss7ConfigException) e;
            throw new RuntimeException(e);
        }
    }

    // ── validation failures ───────────────────────────────────
    public void testUnknownLinkReference() {
        expectInvalid("""
            { "sctp": { "links": [ { "name": "L1", "local": "1.1.1.1:1", "peer": "2.2.2.2:2" } ] },
              "m3ua": { "as": [ { "name": "AS1", "mode": "loadshare", "links": ["NOPE"] } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "unknown link");
    }

    public void testUnknownRouteAs() {
        expectInvalid("""
            { "sctp": { "links": [ { "name": "L1", "local": "1.1.1.1:1", "peer": "2.2.2.2:2" } ] },
              "m3ua": { "as": [ { "name": "AS1", "mode": "loadshare", "links": ["L1"] } ],
                        "routes": [ { "to": { "dpc": 1, "opc": 2 }, "via": "GHOST" } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "unknown AS");
    }

    public void testDuplicateServiceSsn() {
        expectInvalid("""
            { "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "a", "ssn": 6, "protocol": "map" },
                            { "name": "b", "ssn": 6, "protocol": "map" } ] }
            """, "duplicate service SSN");
    }

    public void testEmptyServices() {
        expectInvalid("""
            { "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [] }
            """, "at least one service");
    }

    public void testBadChannelEnum() {
        expectInvalid("""
            { "sctp": { "links": [ { "name": "L1", "channel": "FOO", "local": "1.1.1.1:1", "peer": "2.2.2.2:2" } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "invalid channel");
    }

    public void testRuleWithoutTarget() {
        expectInvalid("""
            { "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ],
                        "routing": [ { "match": { "ssn": 6 } } ] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "no destination");
    }

    public void testRuleNetworkIdWithoutLocalPoint() {
        expectInvalid("""
            { "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ],
                        "routing": [ { "networkId": 9, "match": { "ssn": 6 }, "to": { "pc": 1, "ssn": 6 } } ] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "no matching localPoint");
    }

    public void testBadHostPort() {
        expectInvalid("""
            { "sctp": { "links": [ { "name": "L1", "local": "1.1.1.1", "peer": "2.2.2.2:2" } ] },
              "sccp": { "localPoints": [ { "pc": 1, "networkId": 0 } ], "routing": [] },
              "services": [ { "name": "s", "ssn": 6, "protocol": "map" } ] }
            """, "ip:port");
    }

    private void expectInvalid(String json, String expectFragment) {
        try {
            Ss7ConfigLoader.parse(json);
            fail("expected Ss7ConfigException mentioning '" + expectFragment + "'");
        } catch (Ss7ConfigException e) {
            assertTrue("message '" + e.getMessage() + "' should mention '" + expectFragment + "'",
                    e.getMessage().toLowerCase().contains(expectFragment.toLowerCase()));
        }
    }
}
