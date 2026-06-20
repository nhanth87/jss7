package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.restcomm.protocols.ss7.tcap.api.TCAPException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DialogIdAllocatorTest {

    private DialogIdAllocator allocator;

    @BeforeMethod
    public void setUp() {
        allocator = new DialogIdAllocator(100, 109);
    }

    @Test(groups = { "functional.settings" })
    public void allocateAndRelease() throws TCAPException {
        Long id1 = allocator.allocate();
        Long id2 = allocator.allocate();

        assertTrue(id1 >= 100 && id1 <= 109);
        assertTrue(id2 >= 100 && id2 <= 109);
        assertFalse(id1.equals(id2));
        assertEquals(allocator.getUsedCount(), 2);

        allocator.release(id1);
        assertEquals(allocator.getUsedCount(), 1);

        Long id3 = allocator.allocate();
        assertTrue(id3 >= 100 && id3 <= 109);
        assertFalse(id3.equals(id2));
        assertEquals(allocator.getUsedCount(), 2);
    }

    @Test(groups = { "functional.settings" })
    public void tryReserveSpecificId() throws TCAPException {
        assertTrue(allocator.tryReserve(105L));
        assertFalse(allocator.tryReserve(105L));
        assertEquals(allocator.getUsedCount(), 1);

        allocator.release(105L);
        assertTrue(allocator.tryReserve(105L));
    }

    @Test(groups = { "functional.settings", "expectedExceptions" }, expectedExceptions = TCAPException.class)
    public void allocateUntilExhausted() throws TCAPException {
        for (int i = 0; i < 10; i++) {
            allocator.allocate();
        }
        allocator.allocate();
    }

    @Test(groups = { "functional.settings" })
    public void reconfigurePreservesMarkedIds() throws TCAPException {
        allocator.tryReserve(102L);
        allocator.tryReserve(104L);

        allocator.configure(100, 109);
        allocator.markUsed(102L);
        allocator.markUsed(104L);

        assertFalse(allocator.tryReserve(102L));
        assertFalse(allocator.tryReserve(104L));
        assertTrue(allocator.tryReserve(103L));
    }
}
