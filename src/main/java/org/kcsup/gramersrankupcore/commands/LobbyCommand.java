package org.kcsup.gramersrankupcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Util;
import org.kcsup.gramersrankupcore.warps.Warp;

public class LobbyCommand implements CommandExecutor {
    private final Main main;

    public LobbyCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;
        Warp mainLobby = main.getWarpManager().getWarp("Main_Lobby");
        if(mainLobby != null)
            Util.updatedTeleport(player, mainLobby.getLocation());

        return false;
    }
}
