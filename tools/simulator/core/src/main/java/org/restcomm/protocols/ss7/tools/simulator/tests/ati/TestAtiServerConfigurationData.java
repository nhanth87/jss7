package org.restcomm.protocols.ss7.tools.simulator.tests.ati;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


/**
*
* @author sergey vetyutnev
*
*/
@JacksonXmlRootElement(localName = "testAtiServerConfigurationData")
public class TestAtiServerConfigurationData {

    protected static final String ATI_REACTION = "atiReaction";

    protected ATIReaction atiReaction = new ATIReaction(ATIReaction.VAL_RETURN_SUCCESS);

    public ATIReaction getATIReaction() {
        return atiReaction;
    }

    public void setATIReaction(ATIReaction val) {
        atiReaction = val;
    }


}
