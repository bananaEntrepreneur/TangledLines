package view;

import model.game.Game;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel _gamePanel;
    private final Game _game;
    private final View _view;
    private JLabel _levelLabel;
    private JButton _nextLevelButton;
    private JButton _restartButton;

    public GameFrame(Game game, View view) {
        super("Tangled Lines");
        _game = game;
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

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        _levelLabel = new JLabel("Level 1/" + _game.getTotalLevels());
        panel.add(_levelLabel);

        _nextLevelButton = new JButton("Next Level");
        _nextLevelButton.setEnabled(false);
        _nextLevelButton.addActionListener(e -> {
            if (_game.nextLevel()) {
                _view.subscribeToNodes();
                updateLevelLabel();
                updateButtons();
                _gamePanel.repaint();
            }
        });
        panel.add(_nextLevelButton);

        _restartButton = new JButton("Restart Level");
        _restartButton.addActionListener(e -> {
            _game.restartLevel();
            _view.subscribeToNodes();
            updateLevelLabel();
            updateButtons();
            _gamePanel.repaint();
        });
        panel.add(_restartButton);

        return panel;
    }

    public void refresh() {
        _gamePanel.repaint();
        updateLevelLabel();
        updateButtons();
    }

    private void updateLevelLabel() {
        _levelLabel.setText("Level " + (_game.getCurrentLevelIndex() + 1) + "/" + _game.getTotalLevels());
    }

    private void updateButtons() {
        if (_game.isWin()) {
            _nextLevelButton.setEnabled(_game.hasNextLevel());
        } else {
            _nextLevelButton.setEnabled(false);
        }
    }
}
