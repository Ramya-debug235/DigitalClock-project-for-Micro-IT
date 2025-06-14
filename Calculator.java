import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

/**
 * Calculator that shows a Swing GUI when a display is available
 * and falls back to a console calculator in headless environments.
 */
public class Calculator {

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Headless environment detected – switching to console mode.");
            new ConsoleCalculator().start();
        } else {
            SwingUtilities.invokeLater(GUICalculator::new);
        }
    }

    /* ------------ GUI VERSION ------------- */
    private static class GUICalculator extends JFrame implements ActionListener {
        private final JTextField display = new JTextField();
        private String operator = "";
        private double num1 = 0;

        GUICalculator() {
            setTitle("Calculator");
            setSize(300, 400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            display.setEditable(false);
            display.setFont(new Font("Arial", Font.BOLD, 24));
            add(display, BorderLayout.NORTH);

            JPanel panel = new JPanel(new GridLayout(4, 4, 10, 10));
            String[] keys = { "7","8","9","/",
                              "4","5","6","*",
                              "1","2","3","-",
                              "0","C","=","+" };
            for (String k : keys) {
                JButton b = new JButton(k);
                b.setFont(new Font("Arial", Font.BOLD, 20));
                b.addActionListener(this);
                panel.add(b);
            }
            add(panel, BorderLayout.CENTER);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.matches("\\d")) {                 // digit
                display.setText(display.getText() + cmd);
            } else if ("C".equals(cmd)) {            // clear
                display.setText("");
                operator = "";
                num1 = 0;
            } else if ("=".equals(cmd)) {            // equals
                try {
                    double num2 = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(calculate(num1, num2, operator)));
                } catch (NumberFormatException ex) {
                    display.setText("Error");
                }
            } else {                                 // operator (+ - * /)
                try {
                    num1 = Double.parseDouble(display.getText());
                    operator = cmd;
                    display.setText("");
                } catch (NumberFormatException ex) {
                    display.setText("Error");
                }
            }
        }

        private double calculate(double a, double b, String op) {
            return switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> b == 0 ? Double.NaN : a / b;
                default  -> b;
            };
        }
    }

    /* ------------ CONSOLE VERSION ------------- */
    private static class ConsoleCalculator {
        private final Scanner sc = new Scanner(System.in);

        void start() {
            System.out.println("Simple CLI Calculator (type \"exit\" to quit)");
            while (true) {
                try {
                    System.out.print("Enter expression [e.g. 2 + 3] ➜ ");
                    String line = sc.nextLine().trim();
                    if (line.equalsIgnoreCase("exit")) break;
                    String[] parts = line.split("\\s+");
                    if (parts.length != 3) throw new IllegalArgumentException();

                    double a = Double.parseDouble(parts[0]);
                    String op = parts[1];
                    double b = Double.parseDouble(parts[2]);

                    double res = switch (op) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        case "/" -> b == 0 ? Double.NaN : a / b;
                        default  -> throw new IllegalArgumentException();
                    };
                    System.out.println("Result = " + res);
                } catch (Exception ex) {
                    System.out.println("Invalid input. Try again.");
                }
            }
            System.out.println("Good‑bye!");
        }
    }
}