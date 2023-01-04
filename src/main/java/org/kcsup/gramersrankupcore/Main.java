package org.kcsup.gramersrankupcore;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.gramersrankupcore.commands.*;
import org.kcsup.gramersrankupcore.commands.admin.*;
import org.kcsup.gramersrankupcore.menu.MenuManager;
import org.kcsup.gramersrankupcore.practice.PracticeManager;
import org.kcsup.gramersrankupcore.ranks.RankManager;
import org.kcsup.gramersrankupcore.saves.SaveManager;
import org.kcsup.gramersrankupcore.signs.SignManager;
import org.kcsup.gramersrankupcore.teams.ScoreboardManager;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.visibility.VisibilityManager;
import org.kcsup.gramersrankupcore.warps.WarpManager;

import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {
    private final Set<Manager> managers = new HashSet<>();

    private RankManager rankManager;
    private ScoreboardManager scoreboardManager;
    private PracticeManager practiceManager;
    private SignManager signManager;
    private SaveManager saveManager;
    private MenuManager menuManager;
    private WarpManager warpManager;
    private VisibilityManager visibilityManager;

    private WorldEditPlugin worldEditPlugin;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        rankManager = (RankManager) newManager(new RankManager(this));
        scoreboardManager = (ScoreboardManager) newManager(new ScoreboardManager(this));
        practiceManager = (PracticeManager) newManager(new PracticeManager(this));
        signManager = (SignManager) newManager(new SignManager(this));
        saveManager = (SaveManager) newManager(new SaveManager(this));
        menuManager = (MenuManager) newManager(new MenuManager(this));
        warpManager = (WarpManager) newManager(new WarpManager(this));
        visibilityManager = (VisibilityManager) newManager(new VisibilityManager(this));

        managers.forEach(Manager::startup);

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
            if(plugin instanceof WorldEditPlugin)
                worldEditPlugin = (WorldEditPlugin) plugin;
        }

    }

    public Set<Manager> getManagers() {
        return managers;
    }

    private Manager newManager(Manager manager) {
        managers.add(manager);
        return manager;
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

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    @Override
    public void onDisable() {
        managers.forEach(Manager::shutdown);
    }
}
