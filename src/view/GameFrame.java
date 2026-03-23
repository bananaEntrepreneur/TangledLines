package view;

import model.game.Game;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel _gamePanel;
    private final Game _game;

    public GameFrame(Game game) {
        super("Tangled Lines");
        _game = game;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        _gamePanel = new GamePanel(game);
        add(_gamePanel, BorderLayout.CENTER);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());
        panel.add(resetButton);
        
        return panel;
    }

    private void resetGame() {
        JOptionPane.showMessageDialog(this, 
            "Reset functionality requires game restart", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void refresh() {
        _gamePanel.repaint();
    }
}
