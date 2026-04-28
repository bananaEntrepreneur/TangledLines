package view;

import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.listeners.GameStateListener;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements GameStateListener {
    private final GamePanel _gamePanel;
    private final Game _game;
    private final View _view;
    private JLabel _levelLabel;
    private JButton _nextLevelButton;
    private JButton _restartButton;

    public GameFrame(Game game, View view) {
        super(GameStyle.WINDOW_TITLE);
        _game = game;
        _view = view;

        _game.addGameStateListener(this);

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

        LevelNavigation nav = _game.getNavigation();
        _levelLabel = new JLabel(GameStyle.LABEL_LEVEL + "1/" + nav.getTotalLevels());
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

    private void handleNextLevel() {
        if (_game.nextLevel()) {
            afterLevelTransition();
        }
    }

    private void handleRestartLevel() {
        _game.restartLevel();
        afterLevelTransition();
    }

    private void afterLevelTransition() {
        _view.refreshNodeSubscriptions();
        _gamePanel.recreateWidgets();
        updateLevelLabel();
        updateButtons();
    }

    private void updateLevelLabel() {
        LevelNavigation nav = _game.getNavigation();
        _levelLabel.setText(GameStyle.LABEL_LEVEL + (nav.getCurrentLevelIndex() + 1) + "/" + nav.getTotalLevels());
    }

    private void updateButtons() {
        GameState state = _game.getState();
        if (state.isWin()) {
            _nextLevelButton.setEnabled(_game.getNavigation().hasNextLevel());
        } else {
            _nextLevelButton.setEnabled(false);
        }
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        refresh();
    }
}