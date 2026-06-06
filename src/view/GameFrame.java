package view;

import model.game.GameState;
import model.game.LevelNavigation;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel _gamePanel;
    private final GameState _gameState;
    private final LevelNavigation _navigation;
    private JLabel _levelLabel;
    private JButton _nextLevelButton;
    private JButton _restartButton;

    public GameFrame(
        GameState gameState,
        LevelNavigation navigation
    ) {
        super(GameStyle.WINDOW_TITLE);
        _gameState = gameState;
        _navigation = navigation;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        _gamePanel = new GamePanel(gameState);
        add(_gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public void refresh() {
        _gamePanel.repaint();
        updateLevelLabel();
        updateButtons();
    }

    public void recreateWidgets() {
        _gamePanel.recreateWidgets();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        _levelLabel = new JLabel(GameStyle.LABEL_LEVEL + "1/" + _navigation.getTotalLevelCount());
        panel.add(_levelLabel);

        _nextLevelButton = new JButton(GameStyle.BUTTON_NEXT_LEVEL);
        _nextLevelButton.setEnabled(false);
        _nextLevelButton.addActionListener(e -> handleNextLevel());
        panel.add(_nextLevelButton);

        _restartButton = new JButton(GameStyle.BUTTON_RESTART);
        _restartButton.addActionListener(e -> handleRestartLevel());
        panel.add(_restartButton);

        return panel;
    }

    private void handleNextLevel() { _navigation.nextLevel(); }

    private void handleRestartLevel() {
        _navigation.restartLevel();
    }

    private void updateLevelLabel() {
        _levelLabel.setText(
            GameStyle.LABEL_LEVEL + (_navigation.getCurrentLevelIndex() + 1) + "/" + _navigation.getTotalLevelCount()
        );
    }

    private void updateButtons() {
        _nextLevelButton.setEnabled(_gameState.isCurrentLevelWon() && _navigation.hasNextLevel());
    }
}
