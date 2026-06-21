package org.restcomm.protocols.ss7.map.load.ussd;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UssdMenuEngineTest {

    @Test
    public void balanceProfileWalksMainThenExit() throws Exception {
        UssdMenuEngine engine = UssdMenuEngine.defaultEngine();
        long dialog = 1L;
        engine.beginDialog(dialog);
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.BALANCE), "1");
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.BALANCE), "0");
    }

    @Test
    public void dataProfileSelectsBundle() throws Exception {
        UssdMenuEngine engine = UssdMenuEngine.defaultEngine();
        long dialog = 2L;
        engine.beginDialog(dialog);
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.DATA), "2");
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.DATA), "1");
    }

    @Test
    public void subscribeProfileUsesWildcard() throws Exception {
        UssdMenuEngine engine = UssdMenuEngine.defaultEngine();
        long dialog = 3L;
        engine.beginDialog(dialog);
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.SUBSCRIBE), "3");
        assertEquals(engine.nextInput(dialog, UssdMenuEngine.Profile.SUBSCRIBE), "100");
    }
}
