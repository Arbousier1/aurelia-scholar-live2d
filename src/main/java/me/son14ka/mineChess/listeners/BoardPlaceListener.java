package me.son14ka.mineChess.listeners;

import me.son14ka.mineChess.ChessGame;
import me.son14ka.mineChess.GameManager;
import me.son14ka.mineChess.MineChess;
import me.son14ka.mineChess.items.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.UUID;

public class BoardPlaceListener implements Listener {

    private final MineChess plugin;

    private final GameManager gameManager;

    public BoardPlaceListener(MineChess plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlaceFrame(HangingPlaceEvent event) {
        ItemStack item = event.getItemStack();
        if (item == null || item.getType() != Material.ITEM_FRAME) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "chess_board_item");

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            event.getEntity().remove();

            Location startLoc = event.getBlock().getLocation();
            ChessGame game = gameManager.createGame(startLoc);

            buildChessBoard(startLoc, game);
        }
    }

    private void buildChessBoard(Location baseLoc, ChessGame game) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Location cellLoc = baseLoc.clone().add(col/4.0, 0, row/4.0);
                Material material = (row + col) % 2 == 0 ? Material.BIRCH_PLANKS : Material.DARK_OAK_PLANKS;

                spawnCell(cellLoc, material, row, col, game.getGameId());
            }
        }

        setupInitialPieces(game);
    }

    private void spawnCell(Location loc, Material mat, int row, int col, UUID gameId) {
        loc.getWorld().spawn(loc, BlockDisplay.class, display -> {
            display.setBlock(mat.createBlockData());
            Transformation trafo = display.getTransformation();
            trafo.getScale().set(0.25f, 0.05f, 0.25f);
            display.setTransformation(trafo);

            display.getPersistentDataContainer().set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, gameId.toString());
        });

        Location cellLoc = loc.clone().add(0.125, 0.05, 0.125);
        loc.getWorld().spawn(cellLoc, Interaction.class, interaction -> {
            interaction.setInteractionWidth(0.25f);
            interaction.setInteractionHeight(0.05f);

            var pdc = interaction.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "chess_row"), PersistentDataType.INTEGER, row);
            pdc.set(new NamespacedKey(plugin, "chess_col"), PersistentDataType.INTEGER, col);
            pdc.set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, gameId.toString());
        });
    }

    private void spawnPiece(ChessGame game, int row, int col, int cmd, boolean isWhite) {
        Location baseLoc = game.getOrigin();
        Location pieceLoc = baseLoc.clone().add(col / 4.0 + 0.125, 0.137, row / 4.0 + 0.125);

        Piece pieceLogic = createPieceObject(cmd, isWhite);

        game.getBoard()[row][col] = pieceLogic;

        if (cmd == 2) pieceLoc.setYaw(-90f);
        else if (cmd == 8) pieceLoc.setYaw(90f);
        else pieceLoc.setYaw(isWhite ? 0f : 180f);

        pieceLoc.getWorld().spawn(pieceLoc, ItemDisplay.class, display -> {
            ItemStack item = new ItemStack(Material.TORCH);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(cmd);
                item.setItemMeta(meta);
            }
            display.setItemStack(item);

            Transformation trafo = display.getTransformation();
            trafo.getScale().set(0.25f, 0.25f, 0.25f);
            display.setTransformation(trafo);

            var pdc = display.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, game.getGameId().toString());
            pdc.set(new NamespacedKey(plugin, "piece_row"), PersistentDataType.INTEGER, row);
            pdc.set(new NamespacedKey(plugin, "piece_col"), PersistentDataType.INTEGER, col);
        });
    }

    private Piece createPieceObject(int cmd, boolean isWhite) {
        int type = isWhite ? cmd : cmd - 6;

        return switch (type) {
            case 1 -> new Pawn(isWhite);
            case 2 -> new Knight(isWhite);
            case 3 -> new Bishop(isWhite);
            case 4 -> new Rook(isWhite);
            case 5 -> new Queen(isWhite);
            case 6 -> new King(isWhite);
            default -> null;
        };
    }

    private void setupInitialPieces(ChessGame game) {
        int[] backRowTemplate = {4, 2, 3, 6, 5, 3, 2, 4};

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int cmd = -1;
                boolean isWhite = (row < 2);

                if (row == 1) cmd = 1;
                else if (row == 6) cmd = 7;
                else if (row == 0) cmd = backRowTemplate[col];
                else if (row == 7) cmd = backRowTemplate[col] + 6;

                if (cmd != -1) {
                    spawnPiece(game, row, col, cmd, isWhite);
                }
            }
        }
    }
}
