package org.restcomm.protocols.ss7.scheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Grep-based regression guard for timer refactor anti-patterns.
 */
public class TimerRefactorRegressionTest {

    private static final Path JSS7_ROOT = locateJss7Root();

    private static Path locateJss7Root() {
        Path cwd = Paths.get("").toAbsolutePath();
        if (Files.exists(cwd.resolve("tcap"))) {
            return cwd;
        }
        Path parent = cwd.getParent();
        if (parent != null && Files.exists(parent.resolve("tcap"))) {
            return parent;
        }
        return cwd;
    }

    @Test
    public void dialogImplMustNotUseScheduledFutureForTimers() throws IOException {
        Path dialogImpl = resolveSource("tcap/tcap-impl/src/main/java/org/restcomm/protocols/ss7/tcap/DialogImpl.java");
        String source = readFile(dialogImpl);
        Assert.assertFalse(source.contains("ScheduledFuture"), "DialogImpl must not use ScheduledFuture for timers");
        Assert.assertFalse(source.contains("idleTimerFuture"), "DialogImpl must not keep idleTimerFuture");
        Assert.assertTrue(source.contains("TimerHandle"), "DialogImpl must use TimerHandle for idle timer");
        Assert.assertTrue(source.contains("getTimerDialogScope(this.localTransactionId)"),
                "DialogImpl.release must call timerScheduler.cancelAll with stack-scoped dialog id");
    }

    @Test
    public void mapDialogImplMustNotDelegateResetToTcap() throws IOException {
        Path mapDialogImpl = resolveSource("map/map-impl/src/main/java/org/restcomm/protocols/ss7/map/MAPDialogImpl.java");
        String source = readFile(mapDialogImpl);
        Assert.assertFalse(source.contains("getTcapDialog().resetTimer"), "MAPDialogImpl must not call tcapDialog.resetTimer");
        Assert.assertTrue(source.contains("MapTimerIds"), "MAPDialogImpl must schedule guard invoke timers");
        Assert.assertTrue(source.contains("cancelAll(this.tcapDialog.getLocalDialogId())"),
                "MAPDialogImpl.release must call timerScheduler.cancelAll(dialogId)");
    }

    @Test
    public void capDialogImplMustNotDelegateResetToTcap() throws IOException {
        Path capDialogImpl = resolveSource("cap/cap-impl/src/main/java/org/restcomm/protocols/ss7/cap/CAPDialogImpl.java");
        String source = readFile(capDialogImpl);
        Assert.assertFalse(source.contains("getTcapDialog().resetTimer"), "CAPDialogImpl must not call tcapDialog.resetTimer");
        Assert.assertTrue(source.contains("CapTimerIds"), "CAPDialogImpl must schedule guard invoke timers");
        Assert.assertTrue(source.contains("cancelAll(this.tcapDialog.getLocalDialogId())"),
                "CAPDialogImpl.release must call timerScheduler.cancelAll(dialogId)");
    }

    @Test
    public void invokeImplMustUseTimerHandle() throws IOException {
        Path invokeImpl = resolveSource("tcap/tcap-impl/src/main/java/org/restcomm/protocols/ss7/tcap/asn/InvokeImpl.java");
        String source = readFile(invokeImpl);
        Assert.assertFalse(source.contains("timerFuture"), "InvokeImpl must not keep timerFuture");
        Assert.assertTrue(source.contains("TimerHandle"), "InvokeImpl must use TimerHandle");
    }

    @Test
    public void previewDialogDataMustUseTimerScheduler() throws IOException {
        Path previewDialogData = resolveSource("tcap/tcap-impl/src/main/java/org/restcomm/protocols/ss7/tcap/PreviewDialogData.java");
        String source = readFile(previewDialogData);
        Assert.assertFalse(source.contains("idleTimerFuture"), "PreviewDialogData must not keep idleTimerFuture");
        Assert.assertTrue(source.contains("TimerScheduler"), "PreviewDialogData must use TimerScheduler");
    }

    private static Path resolveSource(String relativePath) {
        List<Path> candidates = new ArrayList<>();
        candidates.add(JSS7_ROOT.resolve(relativePath));
        candidates.add(Paths.get(relativePath));

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.normalize();
            }
        }
        Assert.fail("Unable to locate source file: " + relativePath + " from " + JSS7_ROOT);
        return null;
    }

    private static String readFile(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            lines.forEach(line -> sb.append(line).append('\n'));
        }
        return sb.toString();
    }
}
