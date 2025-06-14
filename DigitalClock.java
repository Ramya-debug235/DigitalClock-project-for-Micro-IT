import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Digital Clock
 *  - GUI when a display is available
 *  - Console clock in headless environments
 *  - Optional runtime limit: pass seconds as first argument
 *
 *   • Compile/run classic way:
 *       javac DigitalClock.java
 *       java  DigitalClock 15        // run 15 s then exit
 *
 *   • Or single‑file launch on JDK‑11+:
 *       java  DigitalClock.java 15
 */
public class DigitalClock {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // How long to run (seconds). ≤0 → run forever
        long limitSecs = args.length > 0 ? Long.parseLong(args[0]) : 0;

        if (GraphicsEnvironment.isHeadless()) {
            runConsole(limitSecs);
        } else {
            SwingUtilities.invokeLater(() -> new GuiClock(limitSecs));
        }
    }

    /* ---------- GUI version ---------- */
    private static class GuiClock extends JFrame {
        private final JLabel label = new JLabel("", SwingConstants.CENTER);
        private long remaining;    // seconds remaining (0 = infinite)

        GuiClock(long limitSecs) {
            this.remaining = limitSecs;
            setTitle("Digital Clock");
            setSize(240, 90);
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            label.setFont(new Font("Consolas", Font.BOLD, 36));
            add(label);

            Timer t = new Timer(1000, (ActionEvent e) -> tick());
            t.start();
            tick();                // initial display
            setVisible(true);
        }

        private void tick() {
            label.setText(LocalTime.now().format(FMT));
            if (remaining > 0 && --remaining == 0) {
                dispose();        // close window
                System.exit(0);   // end program
            }
        }
    }

    /* ---------- Console version ---------- */
    private static void runConsole(long limitSecs) {
        System.out.println("Console Clock — Ctrl+C to exit");
        while (limitSecs <= 0 || limitSecs-- > 0) {
            System.out.print("\r" + LocalTime.now().format(FMT));
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        System.out.println();   // newline before exiting
    }
}