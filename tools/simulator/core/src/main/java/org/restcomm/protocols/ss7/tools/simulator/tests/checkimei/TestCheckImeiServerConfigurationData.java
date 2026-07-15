

package org.restcomm.protocols.ss7.tools.simulator.tests.checkimei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.service.mobility.imei.EquipmentStatus;

/**
 * @author mnowa
 *
 */
@JacksonXmlRootElement(localName = "testCheckImeiServerConfigurationData")
public class TestCheckImeiServerConfigurationData {

    protected static final String AUTO_EQUIPMENT_STATUS = "autoEquipmentStatus";
    protected static final String ONE_NOTIFICATION_FOR_100_DIALOGS = "oneNotificationFor100Dialogs";

    protected EquipmentStatus autoEquipmentStatus = EquipmentStatus.whiteListed;

    protected boolean oneNotificationFor100Dialogs = false;

    public EquipmentStatus getAutoEquipmentStatus() {
        return autoEquipmentStatus;
    }

    public void setAutoEquipmentStatus(EquipmentStatus autoEquipmentStatus) {
        this.autoEquipmentStatus = autoEquipmentStatus;
    }

    public boolean isOneNotificationFor100Dialogs() {
        return oneNotificationFor100Dialogs;
    }

    public void setOneNotificationFor100Dialogs(boolean oneNotificationFor100Dialogs) {
        this.oneNotificationFor100Dialogs = oneNotificationFor100Dialogs;
    }


}
