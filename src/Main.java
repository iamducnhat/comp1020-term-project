import view.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 *
 * VinTony Coffee POS & Loyalty System
 * Team 5 — COMP1020 Spring 2026
 *
 * Run: javac -sourcepath src -d out src/Main.java && java -cp out Main
 */
public class Main {

    public static void main(String[] args) {
        // All Swing operations must run on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame.applyLookAndFeel();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
