package org.restcomm.protocols.ss7.tcap;

import org.jctools.maps.NonBlockingHashMap;
import org.restcomm.protocols.ss7.tcap.api.DialogRegistry;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;

/**
 * Lock-free dialog registry backed by JCTools NonBlockingHashMap.
 */
public class DialogRegistryImpl implements DialogRegistry {

    private final NonBlockingHashMap<Long, DialogImpl> dialogs = new NonBlockingHashMap<>();

    @Override
    public Dialog get(Long id) {
        return dialogs.get(id);
    }

    @Override
    public void put(Long id, Dialog dialog) {
        dialogs.put(id, (DialogImpl) dialog);
    }

    @Override
    public Dialog remove(Long id) {
        return dialogs.remove(id);
    }

    @Override
    public boolean containsKey(Long id) {
        return dialogs.containsKey(id);
    }

    @Override
    public int size() {
        return dialogs.size();
    }

    @Override
    public void clear() {
        dialogs.clear();
    }

    Iterable<Long> keys() {
        return dialogs.keySet();
    }
}
