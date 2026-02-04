package me.son14ka.mineChess.listeners;

import me.son14ka.mineChess.ChessGame;
import me.son14ka.mineChess.GameManager;
import me.son14ka.mineChess.MineChess;
import me.son14ka.mineChess.items.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class BoardClickListener implements Listener {

    private final MineChess plugin;
    private final GameManager gameManager;

    private final Map<UUID, int[]> selectedCells = new HashMap<>();
    private final Map<UUID, List<ItemDisplay>> activeHighlights = new HashMap<>();

    public BoardClickListener(MineChess plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBoardClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction interaction)) return;

        var pdc = interaction.getPersistentDataContainer();
        Player player = event.getPlayer();
        NamespacedKey promotionKey = new NamespacedKey(plugin, "promotion_cmd");

        if (interaction.getPersistentDataContainer().has(promotionKey, PersistentDataType.INTEGER)) {
            int cmd = pdc.get(promotionKey, PersistentDataType.INTEGER);
            int row = pdc.get(new NamespacedKey(plugin, "promotion_row"), PersistentDataType.INTEGER);
            int col = pdc.get(new NamespacedKey(plugin, "promotion_col"), PersistentDataType.INTEGER);
            UUID gameId = UUID.fromString(pdc.get(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING));

            ChessGame game = gameManager.getGame(gameId);
            if (game == null) return;

            completePromotion(game, row, col, cmd);

            cleanupPromotionEntities(interaction.getWorld(), gameId);

            game.setWaitingForPromotion(false);
            game.switchTurn();

            gameManager.checkGameState(game, player, plugin);
        } else {
            NamespacedKey gameKey = new NamespacedKey(plugin, "game_id");
            if (!pdc.has(gameKey, PersistentDataType.STRING)) return;

            UUID gameId = UUID.fromString(pdc.get(gameKey, PersistentDataType.STRING));
            ChessGame game = gameManager.getGame(gameId);
            if (game == null) return;

            if (game.isGameOver()) {
                event.getPlayer().sendMessage(getMsg(player, "game_over", plugin));
                return;
            }
            if (game.isWaitingForPromotion()) {
                event.getPlayer().sendMessage(getMsg(player, "promotion_choice", plugin));
                return;
            }

            int row = pdc.get(new NamespacedKey(plugin, "chess_row"), PersistentDataType.INTEGER);
            int col = pdc.get(new NamespacedKey(plugin, "chess_col"), PersistentDataType.INTEGER);

            handleInteraction(player, game, row, col);
        }
    }

    private void handleInteraction(Player player, ChessGame game, int row, int col) {
        UUID uuid = player.getUniqueId();
        Piece[][] board = game.getBoard();

        int[] selected = selectedCells.get(uuid);

        if (!selectedCells.containsKey(uuid)) {
            Piece piece = board[row][col];
            if (piece == null) return;

            String color = piece.isWhite ? "white" : "black";
            if (!color.equals(game.getCurrentTurn())) {
                String playerColor = game.getCurrentTurn().equals("white") ? getMsg(player, "white", plugin) : getMsg(player, "black", plugin);
                player.sendMessage(getMsg(player, "turn_info", plugin).replace("%color%", playerColor));
                return;
            }

            selectedCells.put(uuid, new int[]{row, col});
            highlightMoves(player, game, piece.getAvailableCells(board, row, col));
        } else {
            int selRow = selected[0];
            int selCol = selected[1];

            if (selRow == row && selCol == col) {
                selectedCells.remove(uuid);
                clearHighlights(player);
                return;
            }

            Piece movingPiece = board[selRow][selCol];

            Piece targetPiece = board[row][col];
            if (targetPiece != null && targetPiece.isWhite == movingPiece.isWhite) {
                clearHighlights(player);
                selectedCells.put(uuid, new int[]{row, col});
                highlightMoves(player, game, targetPiece.getAvailableCells(board, row, col));
                return;
            }

            List<int[]> availableMoves = movingPiece.getAvailableCells(board, selRow, selCol);
            if (isMoveValid(availableMoves, row, col)) {

                if (GameManager.leavesKingInCheck(game, selRow, selCol, row, col)) {
                    player.sendMessage(getMsg(player, "check_warning", plugin));
                    return;
                }

                selectedCells.remove(uuid);
                clearHighlights(player);
                executeMove(game, selRow, selCol, row, col, player);
            } else {
                player.sendMessage(getMsg(player, "wrong_cell", plugin));
            }
        }
    }

    private boolean isMoveValid(List<int[]> moves, int targetRow, int targetCol) {
        for (int[] move : moves) {
            if (move[0] == targetRow && move[1] == targetCol) return true;
        }
        return false;
    }

    private void executeMove(ChessGame game, int fromR, int fromC, int toR, int toC, Player player) {
        Piece piece = game.getBoard()[fromR][fromC];

        boolean wasCapture = game.getBoard()[toR][toC] != null;

        game.getBoard()[toR][toC] = piece;
        game.getBoard()[fromR][fromC] = null;
        piece.setHasMoved(true);

        movePieceEntity(game, fromR, fromC, toR, toC, wasCapture);

        piece = game.getBoard()[toR][toC];
        if (piece instanceof Pawn && (toR == 7 || toR == 0)) {
            checkPromotion(game, toR, toC);
        } else {
            game.switchTurn();
            boolean nextPlayerIsWhite = game.getCurrentTurn().equals("white");

            boolean hasMoves = GameManager.hasLegalMoves(game, nextPlayerIsWhite);
            boolean inCheck = GameManager.isInCheck(game, nextPlayerIsWhite);

            if (!hasMoves) {
                game.setGameOver(true);

                String msg;
                if (inCheck) {
                    String winner = nextPlayerIsWhite ? getMsg(player, "black", plugin) : getMsg(player, "white", plugin);
                    msg = getMsg(player, "mate_broadcast", plugin).replace("%winner%", winner);
                    spawnVictoryFireworks(game.getOrigin());
                } else {
                    msg = getMsg(player, "stalemate_broadcast", plugin);
                    spawnAmbientSmoke(game.getOrigin());
                }

                GameManager.broadcastToGame(game, msg);
            } else if (GameManager.hasInsufficientMaterial(game)) {
                game.setGameOver(true);
                spawnAmbientSmoke(game.getOrigin());

                String msg = getMsg(player, "insufficient_material", plugin);
                GameManager.broadcastToGame(game, msg);
            } else if (inCheck) {
                String colorName = nextPlayerIsWhite ? getMsg(player, "white", plugin) : getMsg(player, "black", plugin);
                String msg = getMsg(player, "check_notification", plugin).replace("%color%", colorName);

                GameManager.broadcastToGame(game, msg);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BONE_BLOCK_PLACE, 1.0f, 1.0f);
            }
        }
    }

    private void spawnVictoryFireworks(Location origin) {
        Color black = Color.BLACK;
        Color white = Color.WHITE;

        for (int i = 0; i < 5; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                double xOffset = Math.random() * 2.0;
                double zOffset = Math.random() * 2.0;
                Location fireworkLoc = origin.clone().add(xOffset, 0.5, zOffset);

                fireworkLoc.getWorld().spawn(fireworkLoc, org.bukkit.entity.Firework.class, fw -> {
                    org.bukkit.inventory.meta.FireworkMeta meta = fw.getFireworkMeta();

                    org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder()
                            .with(org.bukkit.FireworkEffect.Type.STAR)
                            .withColor(black)
                            .withFade(white)
                            .trail(true)
                            .flicker(true)
                            .build();

                    meta.addEffect(effect);
                    meta.setPower(1);
                    fw.setFireworkMeta(meta);

                    fw.detonate();
                });

                fireworkLoc.getWorld().playSound(fireworkLoc, org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.3f, 1.0f);

            }, i * 10L);
        }
    }

    private void spawnAmbientSmoke(Location origin) {
        int pulses = 8;

        for (int i = 0; i < pulses; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                origin.getWorld().spawnParticle(
                        Particle.CAMPFIRE_COSY_SMOKE,
                        origin, 3, 0.1, 0.1, 0.1, 0.1);

                origin.getWorld().playSound(
                        origin,
                        Sound.BLOCK_FIRE_EXTINGUISH, 0.3f, 0.8f);

            }, i * 4L);
        }
    }

    private void movePieceEntity(ChessGame game, int fromR, int fromC, int toR, int toC, boolean isCapture) {
        Location oldLoc = game.getOrigin().clone().add(fromC / 4.0 + 0.125, 0.137, fromR / 4.0 + 0.125);

        oldLoc.getWorld().getNearbyEntities(oldLoc, 0.1, 0.5, 0.1).stream()
                .filter(e -> e instanceof ItemDisplay)
                .findFirst()
                .ifPresent(entity -> {
                    Location newLoc = game.getOrigin().clone().add(toC / 4.0 + 0.125, 0.137, toR / 4.0 + 0.125);
                    newLoc.setYaw(entity.getLocation().getYaw());

                    entity.teleport(newLoc);
                    entity.getWorld().playSound(newLoc, Sound.BLOCK_WOOD_PLACE, 1.0f, 1.0f);

                    var pdc = entity.getPersistentDataContainer();
                    pdc.set(new NamespacedKey(plugin, "piece_row"), PersistentDataType.INTEGER, toR);
                    pdc.set(new NamespacedKey(plugin, "piece_col"), PersistentDataType.INTEGER, toC);

                    if (isCapture) {
                        removeCapturedPiece(entity, newLoc);
                    }
                });
    }

    private void removeCapturedPiece(Entity movingPiece, Location loc) {
        loc.getWorld().getNearbyEntities(loc, 0.1, 0.5, 0.1).stream()
                .filter(e -> e instanceof ItemDisplay && !e.equals(movingPiece))
                .filter(e -> e.getPersistentDataContainer().has(new NamespacedKey(plugin, "piece_row"), PersistentDataType.INTEGER))
                .findFirst()
                .ifPresent(captured -> {
                    Location capturedLoc = captured.getLocation().add(0, 0.1, 0);

                    captured.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, capturedLoc, 10, 0.1, 0.1, 0.1, 0.05);
                    captured.getWorld().playSound(capturedLoc, Sound.ENTITY_GENERIC_HURT, 1.0f, 1.0f);

                    captured.remove();
                });
    }

    private void highlightMoves(Player player, ChessGame game, List<int[]> moves) {
        clearHighlights(player);

        List<ItemDisplay> highlights = new ArrayList<>();

        for (int[] move : moves) {
            int r = move[0];
            int c = move[1];

            Location loc = game.getOrigin().clone().add(c / 4.0 + 0.125, 0.051, r / 4.0 + 0.125);

            loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
                ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);

                display.setItemStack(item);

                var pdc = display.getPersistentDataContainer();
                pdc.set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, game.getGameId().toString());
                pdc.set(new NamespacedKey(plugin, "is_highlight"), PersistentDataType.BYTE, (byte) 1);

                Transformation trafo = display.getTransformation();
                trafo.getScale().set(0.25f, 0.25f, 0.25f);
                trafo.getLeftRotation().rotateX((float) Math.toRadians(90));
                display.setTransformation(trafo);

                display.setVisibleByDefault(false);
                player.showEntity(plugin, display);

                highlights.add(display);
            });
        }
        activeHighlights.put(player.getUniqueId(), highlights);
    }

    private void clearHighlights(Player player) {
        List<ItemDisplay> highlights = activeHighlights.remove(player.getUniqueId());
        if (highlights != null) {
            highlights.forEach(Entity::remove);
        }
    }

    private void checkPromotion(ChessGame game, int row, int col) {
        Piece piece = game.getBoard()[row][col];
        if (!(piece instanceof Pawn)) return;

        boolean isWhite = piece.isWhite;
        if ((isWhite && row == 7) || (!isWhite && row == 0)) {
            spawnPromotionChoices(game, row, col, isWhite);
            game.setWaitingForPromotion(true);
        }
    }

    private void spawnPromotionChoices(ChessGame game, int row, int col, boolean isWhite) {
        Location baseLoc = game.getOrigin().clone().add(col / 4.0 + 0.125, 1.0, row / 4.0 + 0.125);

        int[] types = {5, 4, 3, 2};
        if (!isWhite) { for(int i=0; i<4; i++) types[i] += 6; }

        for (int i = 0; i < 4; i++) {
            Location choiceLoc = baseLoc.clone().add((i - 1.5) * 0.4, 0, 0);
            int cmd = types[i];

            choiceLoc.getWorld().spawn(choiceLoc, ItemDisplay.class, display -> {
                ItemStack item = new ItemStack(Material.TORCH);
                var meta = item.getItemMeta();
                meta.setCustomModelData(cmd);
                item.setItemMeta(meta);
                display.setItemStack(item);

                Transformation trafo = display.getTransformation();
                trafo.getScale().set(0.25f, 0.25f, 0.25f);
                display.setTransformation(trafo);

                display.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_promotion_item"), PersistentDataType.BYTE, (byte) 1);
                display.getPersistentDataContainer().set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, game.getGameId().toString());
            });

            choiceLoc.getWorld().spawn(choiceLoc.clone().add(0, -0.15, 0), Interaction.class, inter -> {
                inter.setInteractionWidth(0.25f);
                inter.setInteractionHeight(0.6f);

                var pdc = inter.getPersistentDataContainer();
                pdc.set(new NamespacedKey(plugin, "promotion_cmd"), PersistentDataType.INTEGER, cmd);
                pdc.set(new NamespacedKey(plugin, "promotion_row"), PersistentDataType.INTEGER, row);
                pdc.set(new NamespacedKey(plugin, "promotion_col"), PersistentDataType.INTEGER, col);
                pdc.set(new NamespacedKey(plugin, "game_id"), PersistentDataType.STRING, game.getGameId().toString());
            });
        }
    }

    private void cleanupPromotionEntities(@NotNull World world, UUID gameId) {
        String idString = gameId.toString();
        NamespacedKey gameKey = new NamespacedKey(plugin, "game_id");
        NamespacedKey promoItemKey = new NamespacedKey(plugin, "is_promotion_item");
        NamespacedKey promoCmdKey = new NamespacedKey(plugin, "promotion_cmd");

        for (Entity entity : world.getEntities()) {
            var pdc = entity.getPersistentDataContainer();

            if (pdc.has(gameKey, PersistentDataType.STRING)) {
                String storedId = pdc.get(gameKey, PersistentDataType.STRING);

                if (idString.equals(storedId)) {
                    if (pdc.has(promoItemKey, PersistentDataType.BYTE) ||
                            pdc.has(promoCmdKey, PersistentDataType.INTEGER)) {

                        entity.remove();
                    }
                }
            }
        }
    }

    private void completePromotion(ChessGame game, int row, int col, int cmd) {
        boolean isWhite = cmd <= 6;

        Piece newPiece = createPieceFromCmd(cmd, isWhite);
        game.getBoard()[row][col] = newPiece;

        Location loc = game.getOrigin().clone().add(col / 4.0 + 0.125, 0.137, row / 4.0 + 0.125);

        loc.getWorld().getNearbyEntities(loc, 0.1, 0.5, 0.1).stream()
                .filter(e -> e instanceof ItemDisplay)
                .findFirst()
                .ifPresent(entity -> {
                    ItemDisplay display = (ItemDisplay) entity;
                    ItemStack item = display.getItemStack();
                    if (item == null) return;

                    var meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(cmd);
                        item.setItemMeta(meta);
                        display.setItemStack(item);
                    }
                });
    }

    private Piece createPieceFromCmd(int cmd, boolean isWhite) {
        int type = isWhite ? cmd : cmd - 6;

        return switch (type) {
            case 2 -> new Knight(isWhite);
            case 3 -> new Bishop(isWhite);
            case 4 -> new Rook(isWhite);
            default -> new Queen(isWhite);
        };
    }

    public static String getMsg(Player player, String path, MineChess plugin) {
        String locale = player.getLocale().startsWith("uk") ? "uk" : "en";

        FileConfiguration config = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "messages_" + locale + ".yml")
        );

        String message = config.getString("messages." + path, "Missing key: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}