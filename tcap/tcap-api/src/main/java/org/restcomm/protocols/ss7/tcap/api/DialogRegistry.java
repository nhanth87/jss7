package org.restcomm.protocols.ss7.tcap.api;

import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;

/**
 * Registry of active TCAP dialogs keyed by local transaction id.
 */
public interface DialogRegistry {

    Dialog get(Long id);

    void put(Long id, Dialog dialog);

    Dialog remove(Long id);

    boolean containsKey(Long id);

    int size();

    void clear();
}
