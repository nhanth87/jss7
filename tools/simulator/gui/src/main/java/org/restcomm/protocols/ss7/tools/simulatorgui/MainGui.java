
package org.restcomm.protocols.ss7.tools.simulatorgui;

import java.awt.EventQueue;

import org.restcomm.protocols.ss7.tools.simulator.TesterHostFactoryImpl;
import org.restcomm.protocols.ss7.tools.simulator.TesterHostFactoryInterface;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class MainGui implements Runnable {

    private final String appName;

    public MainGui(String appName) {
        this.appName = appName;
    }

    protected TesterHostFactoryInterface getTesterHostFactory() {
        return new TesterHostFactoryImpl();
    }

    public static void main(String[] args) {

        String appName = "main";
        if (args != null && args.length > 0) {
            appName = args[0];
        }

        EventQueue.invokeLater(new MainGui(appName));
    }

    protected ConnectionForm createConnectionForm() {
        return new ConnectionForm(this.getTesterHostFactory());
    }

    @Override
    public void run() {
        try {
            // Cross-platform L&F is more reliable on Wayland/Xwayland than GTK
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignore) {
            }
            ConnectionForm frame = createConnectionForm();
            frame.setAppName(appName);
            // Do not use setLocationRelativeTo(null) — off-screen on Niri multi-output
            frame.setLocation(80, 80);
            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
            System.out.println("SS7 Simulator: connection window visible at " + frame.getLocation()
                    + " size=" + frame.getSize() + " display=" + System.getenv("DISPLAY"));
            javax.swing.Timer dropTop = new javax.swing.Timer(3000, ev -> frame.setAlwaysOnTop(false));
            dropTop.setRepeats(false);
            dropTop.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
