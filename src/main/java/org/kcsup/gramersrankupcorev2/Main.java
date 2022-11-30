package org.kcsup.gramersrankupcorev2;

import com.sk89q.worldedit.WorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.gramersrankupcorev2.commands.*;
import org.kcsup.gramersrankupcorev2.menu.MenuManager;
import org.kcsup.gramersrankupcorev2.practice.PracticeManager;
import org.kcsup.gramersrankupcorev2.ranks.RankManager;
import org.kcsup.gramersrankupcorev2.saves.SaveManager;
import org.kcsup.gramersrankupcorev2.signs.SignManager;
import org.kcsup.gramersrankupcorev2.teams.ScoreboardManager;
import org.kcsup.gramersrankupcorev2.visibility.VisibilityManager;
import org.kcsup.gramersrankupcorev2.warps.WarpManager;

public final class Main extends JavaPlugin {
    private RankManager rankManager;
    private ScoreboardManager scoreboardManager;
    private PracticeManager practiceManager;
    private SignManager signManager;
    private SaveManager saveManager;
    private MenuManager menuManager;
    private WarpManager warpManager;
    private VisibilityManager visibilityManager;

    private WorldEdit worldEditPlugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        rankManager = new RankManager(this);
        scoreboardManager = new ScoreboardManager(this);
        practiceManager = new PracticeManager(this);
        signManager = new SignManager(this);
        saveManager = new SaveManager(this);
        menuManager = new MenuManager(this);
        warpManager = new WarpManager(this);
        visibilityManager = new VisibilityManager(this);

        rankManager.initiateAllPlayerRanks();
        scoreboardManager.reloadScoreboard();
        signManager.reloadAllSigns();

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);

        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("practice").setExecutor(new PracticeCommand(this));
        getCommand("unpractice").setExecutor(new UnpracticeCommand(this));
        getCommand("warpsign").setExecutor(new WarpSignCommand(this));

        getCommand("lobby").setExecutor(new LobbyCommand(this));

        getCommand("save").setExecutor(new SaveCommand(this));
        getCommand("saves").setExecutor(new SavesCommand(this));

        getCommand("menu").setExecutor(new MenuCommand(this));

        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("warps").setExecutor(new WarpsCommand(this));
        getCommand("warpcreate").setExecutor(new WarpCreateCommand(this));

        for(Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if(plugin instanceof WorldEdit) {
                worldEditPlugin = (WorldEdit) plugin;
            }
        }

        // TEST
//        if(menuManager.getCurrentMenus() != null) {
//            for (Menu menu : menuManager.getCurrentMenus()) {
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    player.sendMessage(menu.getName());
//                    player.sendMessage(menu.getItem().getItemMeta().getDisplayName());
//                    player.getInventory().addItem(menu.getItem());
//                }
//            }
//        }

    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public PracticeManager getPracticeManager() {
        return practiceManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public VisibilityManager getVisibilityManager() { return visibilityManager; }

    public WorldEdit getWorldEditPlugin() {
        return worldEditPlugin;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        practiceManager.setAllNotPracticing();
        visibilityManager.purgeInvisible();
    }
}
