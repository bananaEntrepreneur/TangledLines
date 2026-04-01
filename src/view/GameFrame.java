package view;

import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel _gamePanel;
    private final GameState _gameState;
    private final LevelNavigation _levelNavigation;
    private final View _view;
    private JLabel _levelLabel;
    private JButton _nextLevelButton;
    private JButton _restartButton;

    public GameFrame(Game game, View view) {
        super("Tangled Lines");
        _gameState = game;
        _levelNavigation = game;
        _view = view;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        _gamePanel = new GamePanel(game);
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

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        _levelLabel = new JLabel("Level 1/" + _gameState.getTotalLevels());
        panel.add(_levelLabel);

        _nextLevelButton = new JButton("Next Level");
        _nextLevelButton.setEnabled(false);
        _nextLevelButton.addActionListener(e -> {
            if (_levelNavigation.nextLevel()) {
                _view.subscribeToNodes();
                updateLevelLabel();
                updateButtons();
                _gamePanel.repaint();
            }
        });
        panel.add(_nextLevelButton);

        _restartButton = new JButton("Restart Level");
        _restartButton.addActionListener(e -> {
            _levelNavigation.restartLevel();
            _view.subscribeToNodes();
            updateLevelLabel();
            updateButtons();
            _gamePanel.repaint();
        });
        panel.add(_restartButton);

        return panel;
    }

    private void updateLevelLabel() {
        _levelLabel.setText("Level " + (_levelNavigation.getCurrentLevelIndex() + 1) + "/" + _gameState.getTotalLevels());
    }

    private void updateButtons() {
        if (_gameState.isWin()) {
            _nextLevelButton.setEnabled(_levelNavigation.hasNextLevel());
        } else {
            _nextLevelButton.setEnabled(false);
        }
    }
}
