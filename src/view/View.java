package view;

import model.game.Game;
import model.listeners.NodeChangeListener;

import java.awt.geom.Point2D;
import model.units.Node;

public class View implements NodeChangeListener {
    private final GameFrame _frame;
    private final Game _game;

    public View(Game game) {
        _game = game;
        _frame = new GameFrame(game);
    }

    public void show() {
        _frame.setVisible(true);
    }

    public void close() {
        _frame.dispose();
    }

    @Override
    public void onNodeMoved(Node node, Point2D oldPosition, Point2D newPosition) {
        _frame.refresh();
        
        if (_game.isGameOver()) {
            String message = _game.isWin() ? "Congratulations! You won!" : "Game Over! You ran out of moves.";
            String title = _game.isWin() ? "Victory" : "Defeat";
            int type = _game.isWin() ? 
                javax.swing.JOptionPane.INFORMATION_MESSAGE : 
                javax.swing.JOptionPane.WARNING_MESSAGE;
            
            javax.swing.JOptionPane.showMessageDialog(_frame, message, title, type);
        }
    }
}
