package main;

import javax.swing.*;
import java.awt.*;

public class SplashScreenApp {

    public static void main(String[] args) {
        // 1. Create a borderless window (JWindow is perfect for splash screens)
        JWindow splashScreen = new JWindow();
        splashScreen.setSize(400, 250);
        splashScreen.setLocationRelativeTo(null); // Center on screen

        // 2. Set up the main panel with a nice background color
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185)); // A professional deep blue
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        // 3. Add the App Title
        JLabel titleLabel = new JLabel("ALASTKA Tourism", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.CENTER);

        // 4. Add a Progress Bar at the bottom
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113)); // Nice green loading color
        progressBar.setBackground(Color.WHITE);
        panel.add(progressBar, BorderLayout.SOUTH);

        splashScreen.add(panel);
        splashScreen.setVisible(true);

        // 5. Simulate a loading process
        try {
            for (int i = 0; i <= 100; i += 2) {
                Thread.sleep(40); // Controls the speed of the loading bar
                progressBar.setValue(i);

                // Optional: Change text based on progress to look authentic
                if (i == 20) progressBar.setString("Connecting to Database...");
                if (i == 50) progressBar.setString("Loading UI Modules...");
                if (i == 80) progressBar.setString("Starting App...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 6. Close the splash screen and launch the real application
        splashScreen.dispose();

        // Launch your existing Login screen!
        SwingUtilities.invokeLater(() -> {
            LoginApp.main(null);
        });
    }
}