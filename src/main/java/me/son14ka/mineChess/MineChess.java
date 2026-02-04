package me.son14ka.mineChess;

import me.son14ka.mineChess.items.ChessBoardItem;
import me.son14ka.mineChess.items.ChessBookItem;
import me.son14ka.mineChess.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class MineChess extends JavaPlugin {


    private GameManager gameManager;
    public static NamespacedKey BOARD_ITEM_KEY;
    public static NamespacedKey BOOK_ITEM_KEY;

    @Override
    public void onEnable() {
        saveResource("messages_uk.yml", false);
        saveResource("messages_en.yml", false);
        BOARD_ITEM_KEY = new NamespacedKey(this, "chess_board_item");
        BOOK_ITEM_KEY = new NamespacedKey(this, "chess_book_item");

        gameManager = new GameManager(this);

        registerListeners();
        registerRecipes();
        getLogger().info("MineChess has loaded.");
    }

    void registerListeners(){
        getServer().getPluginManager().registerEvents(new BoardInCraftListener(), this);
        getServer().getPluginManager().registerEvents(new BookClickListener(), this);
        getServer().getPluginManager().registerEvents(new AvoidInVanillaCraftsListener(), this);
        getServer().getPluginManager().registerEvents(new BoardClickListener(this, gameManager),this);
        getServer().getPluginManager().registerEvents(new BoardBreakListener(this, gameManager),this);
        getServer().getPluginManager().registerEvents(new BoardPlaceListener(this, gameManager), this);
    }

    void registerRecipes(){
        ItemStack boardIcon = ChessBoardItem.createTemplate();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "chess_board"), boardIcon);
        RecipeChoice choices = new RecipeChoice.MaterialChoice(
                Material.ACACIA_PLANKS, Material.OAK_PLANKS,
                Material.BAMBOO_PLANKS, Material.CHERRY_PLANKS,
                Material.BIRCH_PLANKS, Material.CRIMSON_PLANKS,
                Material.JUNGLE_PLANKS, Material.SPRUCE_PLANKS,
                Material.MANGROVE_PLANKS, Material.WARPED_PLANKS,
                Material.PALE_OAK_PLANKS, Material.DARK_OAK_PLANKS);

        recipe.shape(
                "SSS",
                "WWW",
                "SSS"
        );

        recipe.setIngredient('W', choices);
        recipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(recipe);

        ItemStack tutorialItem = ChessBookItem.create();

        ShapelessRecipe tutorialRecipe = new ShapelessRecipe(
                new NamespacedKey(this, "chess_tutorial_recipe"),
                tutorialItem
        );

        tutorialRecipe.addIngredient(
                new RecipeChoice.ExactChoice(
                        ChessBoardItem.createTemplate()));
        tutorialRecipe.addIngredient(
                new RecipeChoice.ExactChoice(
                        ItemStack.of(Material.BOOK)));

        Bukkit.addRecipe(tutorialRecipe);
    }

    @Override
    public void onDisable() {
        getLogger().info("MineChess has unloaded.");
    }
}
