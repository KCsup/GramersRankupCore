package org.kcsup.gramersrankupcore;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.gramersrankupcore.commands.*;
import org.kcsup.gramersrankupcore.commands.admin.*;
import org.kcsup.gramersrankupcore.menu.MenuManager;
import org.kcsup.gramersrankupcore.practice.Practice;
import org.kcsup.gramersrankupcore.ranks.RankManager;
import org.kcsup.gramersrankupcore.saves.SaveManager;
import org.kcsup.gramersrankupcore.signs.SignManager;
import org.kcsup.gramersrankupcore.teams.ScoreboardUtil;
import org.kcsup.gramersrankupcore.visibility.VisibilityUtil;
import org.kcsup.gramersrankupcore.warps.WarpManager;

public final class Main extends JavaPlugin {
    private RankManager rankManager;
    private ScoreboardUtil scoreboardUtil;
    private Practice practice;
    private SignManager signManager;
    private SaveManager saveManager;
    private MenuManager menuManager;
    private WarpManager warpManager;
    private VisibilityUtil visibilityUtil;

    private WorldEditPlugin worldEditPlugin;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        rankManager = new RankManager(this);
        scoreboardUtil = new ScoreboardUtil(this);
        practice = new Practice(this);
        signManager = new SignManager(this);
        saveManager = new SaveManager(this);
        menuManager = new MenuManager(this);
        warpManager = new WarpManager(this);
        visibilityUtil = new VisibilityUtil(this);

        rankManager.initiateAllPlayerRanks();
        scoreboardUtil.reloadScoreboard();
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
            if(plugin instanceof WorldEditPlugin) {
                worldEditPlugin = (WorldEditPlugin) plugin;
            }
        }

    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public ScoreboardUtil getScoreboardUtil() {
        return scoreboardUtil;
    }

    public Practice getPractice() {
        return practice;
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

    public VisibilityUtil getVisibilityUtil() { return visibilityUtil; }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    @Override
    public void onDisable() {
        practice.setAllNotPracticing();
        visibilityUtil.purgeInvisible();
        signManager.clearCooldown();
    }
}
