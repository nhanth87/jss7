
package org.restcomm.protocols.ss7.tools.simulator.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.restcomm.protocols.ss7.tools.simulator.level1.DialogicConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level1.M3uaConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level2.SccpConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level3.CapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.level3.MapConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.management.Instance_L1;
import org.restcomm.protocols.ss7.tools.simulator.management.Instance_L2;
import org.restcomm.protocols.ss7.tools.simulator.management.Instance_L3;
import org.restcomm.protocols.ss7.tools.simulator.management.Instance_TestTask;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ati.TestAtiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapScfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.cap.TestCapSsfConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.checkimei.TestCheckImeiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.lcs.TestLcsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.psi.TestPsiServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.sms.TestSmsServerConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdClientConfigurationData;
import org.restcomm.protocols.ss7.tools.simulator.tests.ussd.TestUssdServerConfigurationData;

/**
 *
 * @author <a href="mailto:serg.vetyutnev@gmail.com"> Sergey Vetyutnev </a>
 * @modified <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ConfigurationData {

    public static final String INSTANCE_L1 = "instance_L1";
    public static final String INSTANCE_L2 = "instance_L2";
    public static final String INSTANCE_L3 = "instance_L3";
    public static final String INSTANCE_TESTTASK = "instance_TestTask";

    public static final String M3UA = "m3ua";
    public static final String DIALOGIC = "dialogic";
    public static final String SCCP = "sccp";
    public static final String MAP = "map";
    public static final String CAP = "cap";
    public static final String TEST_USSD_CLIENT = "testUssdClient";
    public static final String TEST_USSD_SERVER = "testUssdServer";
    public static final String TEST_SMS_CLIENT = "testSmsClient";
    public static final String TEST_SMS_SERVER = "testSmsServer";
    public static final String TEST_CAP_SCF = "testCapScf";
    public static final String TEST_CAP_SSF = "testCapSsf";
    public static final String TEST_ATI_CLIENT = "testAtiClient";
    public static final String TEST_ATI_SERVER = "testAtiServer";
    public static final String TEST_CHECK_IMEI_CLIENT = "testCheckImeiClient";
    public static final String TEST_CHECK_IMEI_SERVER = "testCheckImeiServer";
    public static final String TEST_MAP_LCS_CLIENT = "testMapLcsClient";
    public static final String TEST_MAP_LCS_SERVER = "testMapLcsServer";
    public static final String TEST_MAP_PSI_SERVER = "testMapPsiServer";

    private Instance_L1 instance_L1 = new Instance_L1(Instance_L1.VAL_NO);
    private Instance_L2 instance_L2 = new Instance_L2(Instance_L2.VAL_NO);
    private Instance_L3 instance_L3 = new Instance_L3(Instance_L3.VAL_NO);
    private Instance_TestTask instance_TestTask = new Instance_TestTask(Instance_TestTask.VAL_NO);

    private M3uaConfigurationData m3uaConfigurationData = new M3uaConfigurationData();
    private DialogicConfigurationData dialogicConfigurationData = new DialogicConfigurationData();
    private SccpConfigurationData sccpConfigurationData = new SccpConfigurationData();
    private MapConfigurationData mapConfigurationData = new MapConfigurationData();
    private CapConfigurationData capConfigurationData = new CapConfigurationData();

    private TestSmsClientConfigurationData testSmsClientConfigurationData = new TestSmsClientConfigurationData();
    private TestSmsServerConfigurationData testSmsServerConfigurationData = new TestSmsServerConfigurationData();
    private TestUssdClientConfigurationData testUssdClientConfigurationData = new TestUssdClientConfigurationData();
    private TestUssdServerConfigurationData testUssdServerConfigurationData = new TestUssdServerConfigurationData();
    private TestCapScfConfigurationData testCapScfConfigurationData = new TestCapScfConfigurationData();
    private TestCapSsfConfigurationData testCapSsfConfigurationData = new TestCapSsfConfigurationData();
    private TestAtiClientConfigurationData testAtiClientConfigurationData = new TestAtiClientConfigurationData();
    private TestAtiServerConfigurationData testAtiServerConfigurationData = new TestAtiServerConfigurationData();
    private TestCheckImeiClientConfigurationData testCheckImeiClientConfigurationData = new TestCheckImeiClientConfigurationData();
    private TestCheckImeiServerConfigurationData testCheckImeiServerConfigurationData = new TestCheckImeiServerConfigurationData();
    private TestLcsClientConfigurationData testLcsClientConfigurationData = new TestLcsClientConfigurationData();
    private TestLcsServerConfigurationData testLcsServerConfigurationData = new TestLcsServerConfigurationData();
    private TestPsiServerConfigurationData testPsiServerConfigurationData = new TestPsiServerConfigurationData();


    public Instance_L1 getInstance_L1() {
        return instance_L1;
    }

    public void setInstance_L1(Instance_L1 val) {
        instance_L1 = val;
    }

    public Instance_L2 getInstance_L2() {
        return instance_L2;
    }

    public void setInstance_L2(Instance_L2 val) {
        instance_L2 = val;
    }

    public Instance_L3 getInstance_L3() {
        return instance_L3;
    }

    public void setInstance_L3(Instance_L3 val) {
        instance_L3 = val;
    }

    public Instance_TestTask getInstance_TestTask() {
        return instance_TestTask;
    }

    public void setInstance_TestTask(Instance_TestTask val) {
        instance_TestTask = val;
    }

    @JsonProperty(M3UA)
    @JsonAlias("m3uaConfigurationData")
    public M3uaConfigurationData getM3uaConfigurationData() {
        return m3uaConfigurationData;
    }

    @JsonProperty(M3UA)
    public void setM3uaConfigurationData(M3uaConfigurationData m3uaConfigurationData) {
        this.m3uaConfigurationData = m3uaConfigurationData;
    }

    @JsonProperty(DIALOGIC)
    @JsonAlias("dialogicConfigurationData")
    public DialogicConfigurationData getDialogicConfigurationData() {
        return dialogicConfigurationData;
    }

    @JsonProperty(DIALOGIC)
    public void setDialogicConfigurationData(DialogicConfigurationData dialogicConfigurationData) {
        this.dialogicConfigurationData = dialogicConfigurationData;
    }

    @JsonProperty(SCCP)
    @JsonAlias("sccpConfigurationData")
    public SccpConfigurationData getSccpConfigurationData() {
        return sccpConfigurationData;
    }

    @JsonProperty(SCCP)
    public void setSccpConfigurationData(SccpConfigurationData sccpConfigurationData) {
        this.sccpConfigurationData = sccpConfigurationData;
    }

    @JsonProperty(MAP)
    @JsonAlias("mapConfigurationData")
    public MapConfigurationData getMapConfigurationData() {
        return mapConfigurationData;
    }

    @JsonProperty(MAP)
    public void setMapConfigurationData(MapConfigurationData mapConfigurationData) {
        this.mapConfigurationData = mapConfigurationData;
    }

    @JsonProperty(CAP)
    @JsonAlias("capConfigurationData")
    public CapConfigurationData getCapConfigurationData() {
        return capConfigurationData;
    }

    @JsonProperty(CAP)
    public void setCapConfigurationData(CapConfigurationData capConfigurationData) {
        this.capConfigurationData = capConfigurationData;
    }

    @JsonProperty(TEST_SMS_CLIENT)
    @JsonAlias("testSmsClientConfigurationData")
    public TestSmsClientConfigurationData getTestSmsClientConfigurationData() {
        return testSmsClientConfigurationData;
    }

    @JsonProperty(TEST_SMS_CLIENT)
    public void setTestSmsClientConfigurationData(TestSmsClientConfigurationData testSmsClientConfigurationData) {
        this.testSmsClientConfigurationData = testSmsClientConfigurationData;
    }

    @JsonProperty(TEST_SMS_SERVER)
    @JsonAlias("testSmsServerConfigurationData")
    public TestSmsServerConfigurationData getTestSmsServerConfigurationData() {
        return testSmsServerConfigurationData;
    }

    @JsonProperty(TEST_SMS_SERVER)
    public void setTestSmsServerConfigurationData(TestSmsServerConfigurationData testSmsServerConfigurationData) {
        this.testSmsServerConfigurationData = testSmsServerConfigurationData;
    }

    @JsonProperty(TEST_USSD_CLIENT)
    @JsonAlias("testUssdClientConfigurationData")
    public TestUssdClientConfigurationData getTestUssdClientConfigurationData() {
        return testUssdClientConfigurationData;
    }

    @JsonProperty(TEST_USSD_CLIENT)
    public void setTestUssdClientConfigurationData(TestUssdClientConfigurationData testUssdClientConfigurationData) {
        this.testUssdClientConfigurationData = testUssdClientConfigurationData;
    }

    @JsonProperty(TEST_USSD_SERVER)
    @JsonAlias("testUssdServerConfigurationData")
    public TestUssdServerConfigurationData getTestUssdServerConfigurationData() {
        return testUssdServerConfigurationData;
    }

    @JsonProperty(TEST_USSD_SERVER)
    public void setTestUssdServerConfigurationData(TestUssdServerConfigurationData testUssdServerConfigurationData) {
        this.testUssdServerConfigurationData = testUssdServerConfigurationData;
    }

    @JsonProperty(TEST_CAP_SCF)
    @JsonAlias("testCapScfConfigurationData")
    public TestCapScfConfigurationData getTestCapScfConfigurationData() {
        return testCapScfConfigurationData;
    }

    @JsonProperty(TEST_CAP_SCF)
    public void setTestCapScfConfigurationData(TestCapScfConfigurationData testCapScfConfigurationData) {
        this.testCapScfConfigurationData = testCapScfConfigurationData;
    }

    @JsonProperty(TEST_CAP_SSF)
    @JsonAlias("testCapSsfConfigurationData")
    public TestCapSsfConfigurationData getTestCapSsfConfigurationData() {
        return testCapSsfConfigurationData;
    }

    @JsonProperty(TEST_CAP_SSF)
    public void setTestCapSsfConfigurationData(TestCapSsfConfigurationData testCapSsfConfigurationData) {
        this.testCapSsfConfigurationData = testCapSsfConfigurationData;
    }

    @JsonProperty(TEST_ATI_CLIENT)
    @JsonAlias("testAtiClientConfigurationData")
    public TestAtiClientConfigurationData getTestAtiClientConfigurationData() {
        return testAtiClientConfigurationData;
    }

    @JsonProperty(TEST_ATI_CLIENT)
    public void setTestAtiClientConfigurationData(TestAtiClientConfigurationData testAtiClientConfigurationData) {
        this.testAtiClientConfigurationData = testAtiClientConfigurationData;
    }

    @JsonProperty(TEST_ATI_SERVER)
    @JsonAlias("testAtiServerConfigurationData")
    public TestAtiServerConfigurationData getTestAtiServerConfigurationData() {
        return testAtiServerConfigurationData;
    }

    @JsonProperty(TEST_ATI_SERVER)
    public void setTestAtiServerConfigurationData(TestAtiServerConfigurationData testAtiServerConfigurationData) {
        this.testAtiServerConfigurationData = testAtiServerConfigurationData;
    }

    @JsonProperty(TEST_CHECK_IMEI_CLIENT)
    @JsonAlias("testCheckImeiClientConfigurationData")
    public TestCheckImeiClientConfigurationData getTestCheckImeiClientConfigurationData() {
        return testCheckImeiClientConfigurationData;
    }

    @JsonProperty(TEST_CHECK_IMEI_CLIENT)
    public void setTestCheckImeiClientConfigurationData(TestCheckImeiClientConfigurationData testCheckImeiClientConfigurationData) {
        this.testCheckImeiClientConfigurationData = testCheckImeiClientConfigurationData;
    }

    @JsonProperty(TEST_CHECK_IMEI_SERVER)
    @JsonAlias("testCheckImeiServerConfigurationData")
    public TestCheckImeiServerConfigurationData getTestCheckImeiServerConfigurationData() {
        return testCheckImeiServerConfigurationData;
    }

    @JsonProperty(TEST_CHECK_IMEI_SERVER)
    public void setTestCheckImeiServerConfigurationData(TestCheckImeiServerConfigurationData testCheckImeiServerConfigurationData) {
        this.testCheckImeiServerConfigurationData = testCheckImeiServerConfigurationData;
    }

    @JsonProperty(TEST_MAP_LCS_SERVER)
    @JsonAlias("testLcsServerConfigurationData")
    public TestLcsServerConfigurationData getTestLcsServerConfigurationData() {
        return testLcsServerConfigurationData;
    }

    @JsonProperty(TEST_MAP_LCS_SERVER)
    public void setTestLcsServerConfigurationData(TestLcsServerConfigurationData testLcsServerConfigurationData) {
        this.testLcsServerConfigurationData = testLcsServerConfigurationData;
    }

    @JsonProperty(TEST_MAP_LCS_CLIENT)
    @JsonAlias("testLcsClientConfigurationData")
    public TestLcsClientConfigurationData getTestLcsClientConfigurationData() {
        return testLcsClientConfigurationData;
    }

    @JsonProperty(TEST_MAP_LCS_CLIENT)
    public void setTestLcsClientConfigurationData(TestLcsClientConfigurationData testLcsClientConfigurationData) {
        this.testLcsClientConfigurationData = testLcsClientConfigurationData;
    }

    @JsonProperty(TEST_MAP_PSI_SERVER)
    @JsonAlias("testPsiServerConfigurationData")
    public TestPsiServerConfigurationData getTestPsiServerConfigurationData() {
        return testPsiServerConfigurationData;
    }

    @JsonProperty(TEST_MAP_PSI_SERVER)
    public void setTestPsiServerConfigurationData(TestPsiServerConfigurationData testPsiServerConfigurationData) {
        this.testPsiServerConfigurationData = testPsiServerConfigurationData;
    }

}
