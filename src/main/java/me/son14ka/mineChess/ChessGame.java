package me.son14ka.mineChess;

import me.son14ka.mineChess.items.Piece;
import org.bukkit.Location;
import java.util.UUID;

public class ChessGame {
    private final UUID gameId;
    private final Location origin;
    private final Piece[][] board = new Piece[8][8];
    private String currentTurn = "white";
    public boolean isWaitingForPromotion = false;
    private boolean isGameOver = false;

    public ChessGame(UUID gameId, Location origin) {
        this.gameId = gameId;
        this.origin = origin;
    }

    public void setWaitingForPromotion(boolean waiting) { isWaitingForPromotion = waiting; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public UUID getGameId() { return gameId; }
    public String getCurrentTurn() { return currentTurn; }
    public Location getOrigin() { return origin; }
    public Piece[][] getBoard() { return board; }

    public void switchTurn() {
        this.currentTurn = currentTurn.equals("white") ? "black" : "white";
    }

    public boolean isWaitingForPromotion() { return isWaitingForPromotion; }
}