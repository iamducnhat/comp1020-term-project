import javax.swing.*;

/**
 * Coffee Shop POS and Loyalty System
 * Team VinTony - COMP1020 Spring 2026
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VinTony Coffee Shop - POS & Loyalty System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
