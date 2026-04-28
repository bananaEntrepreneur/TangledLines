package view.style;

import java.awt.Color;

public final class GameStyle {
    public static final int NODE_RADIUS = 12;
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;

    public static final Color EDGE_COLOR = new Color(100, 149, 237);
    public static final Color NODE_COLOR = new Color(30, 144, 255);
    public static final Color NODE_HOVER_COLOR = new Color(255, 165, 0);
    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250);

    public static final Color WIN_STATUS_COLOR = Color.GREEN;
    public static final Color LOSE_STATUS_COLOR = Color.RED;
    public static final Color ALL_COMPLETE_STATUS_COLOR = Color.GREEN;
    public static final Color DEFAULT_STATUS_COLOR = Color.BLACK;

    public static final int STATUS_FONT_SIZE = 14;
    public static final int GAME_OVER_FONT_SIZE = 24;
    public static final String STATUS_FONT_NAME = "Arial";

    public static final String WINDOW_TITLE = "Tangled Lines";
    public static final String LABEL_LEVEL = "Level ";
    public static final String LABEL_MOVES = "Moves: ";
    public static final String BUTTON_NEXT_LEVEL = "Next Level";
    public static final String BUTTON_RESTART = "Restart Level";
    public static final String STATUS_ALL_COMPLETE = "ALL LEVELS COMPLETE!";
    public static final String STATUS_LEVEL_COMPLETE = "LEVEL COMPLETE!";
    public static final String STATUS_GAME_OVER = "GAME OVER";

}
