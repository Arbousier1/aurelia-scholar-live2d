package me.son14ka.mineChess;

import me.son14ka.mineChess.items.Bishop;
import me.son14ka.mineChess.items.King;
import me.son14ka.mineChess.items.Knight;
import me.son14ka.mineChess.items.Piece;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.son14ka.mineChess.items.Piece.isSquareAttacked;
import static me.son14ka.mineChess.listeners.BoardClickListener.getMsg;

public class GameManager {
    private final MineChess plugin;
    private final Map<UUID, ChessGame> activeGames = new HashMap<>();

    public GameManager(MineChess plugin) {
        this.plugin = plugin;
    }

    public ChessGame createGame(Location origin) {
        UUID gameId = UUID.randomUUID();
        ChessGame game = new ChessGame(gameId, origin);
        activeGames.put(gameId, game);
        return game;
    }

    public ChessGame getGame(UUID gameId) {
        return activeGames.get(gameId);
    }

    public void cleanupGame(UUID gameId, Location loc) {
        activeGames.remove(gameId);

        loc.getWorld().getNearbyEntities(loc, 3, 2, 3).forEach(entity -> {
            var pdc = entity.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "game_id");

            if (pdc.has(key, PersistentDataType.STRING)) {
                String idStr = pdc.get(key, PersistentDataType.STRING);
                if (idStr != null && idStr.equals(gameId.toString())) {
                    entity.remove();
                }
            }
        });
    }

    public static void broadcastToGame(ChessGame game, String message) {
        double radius = 3.0;
        Location center = game.getOrigin();

        center.getWorld().getNearbyPlayers(center, radius).forEach(player -> {
            player.sendMessage(message);
        });
    }

    public void checkGameState(ChessGame game, Player player, MineChess plugin) {
        boolean isWhiteTurn = game.getCurrentTurn().equals("white");

        if (isCheckmate(game, isWhiteTurn)) {
            String winner = isWhiteTurn ? getMsg(player, "black", plugin) : getMsg(player, "white", plugin);
            String msg = getMsg(player, "mate_broadcast", plugin).replace("%winner%", winner);
            broadcastToGame(game, msg);
        }
    }

    public static boolean isCheckmate(ChessGame game, boolean isWhite) {
        int[] kingPos = findKing(game.getBoard(), isWhite);
        if (!isSquareAttacked(game.getBoard(), kingPos[0], kingPos[1], isWhite)) {
            return false;
        }

        Piece[][] board = game.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board[r][c];

                if (piece != null && piece.isWhite == isWhite) {
                    List<int[]> moves = piece.getAvailableCells(board, r, c);

                    for (int[] move : moves) {
                        if (!leavesKingInCheck(game, r, c, move[0], move[1])) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean isInCheck(ChessGame game, boolean isWhite) {
        Piece[][] board = game.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.isWhite == isWhite) {
                    return isSquareAttacked(board, r, c, isWhite);
                }
            }
        }
        return false;
    }

    public static boolean hasInsufficientMaterial(ChessGame game) {
        List<Piece> activePieces = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = game.getBoard()[r][c];
                if (p != null) {
                    activePieces.add(p);
                }
            }
        }

        if (activePieces.size() == 2) {
            return true;
        }

        if (activePieces.size() == 3) {
            for (Piece p : activePieces) {
                if (p instanceof Knight || p instanceof Bishop) {
                    return true;
                }
            }
        }

        return false;
    }

    private static int[] findKing(Piece[][] board, boolean isWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] instanceof King && board[r][c].isWhite == isWhite) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public static boolean leavesKingInCheck(ChessGame game, int fromR, int fromC, int toR, int toC) {
        Piece[][] board = game.getBoard();
        Piece movingPiece = board[fromR][fromC];
        Piece targetPiece = board[toR][toC];
        boolean originalHasMoved = movingPiece.hasMoved;

        board[toR][toC] = movingPiece;
        board[fromR][fromC] = null;

        int[] kingPos = findKing(board, movingPiece.isWhite);

        boolean stillInCheck = isSquareAttacked(board, kingPos[0], kingPos[1], movingPiece.isWhite);

        board[fromR][fromC] = movingPiece;
        board[toR][toC] = targetPiece;
        movingPiece.setHasMoved(originalHasMoved);

        return stillInCheck;
    }

    public static boolean hasLegalMoves(ChessGame game, boolean isWhite) {
        Piece[][] board = game.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board[r][c];
                if (piece != null && piece.isWhite == isWhite) {
                    List<int[]> moves = piece.getAvailableCells(board, r, c);
                    for (int[] move : moves) {
                        if (!leavesKingInCheck(game, r, c, move[0], move[1])) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}