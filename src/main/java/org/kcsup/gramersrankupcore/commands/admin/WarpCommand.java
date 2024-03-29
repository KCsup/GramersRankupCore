package org.kcsup.gramersrankupcore.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Util;
import org.kcsup.gramersrankupcore.warps.Warp;

public class WarpCommand implements CommandExecutor {
    private final Main main;

    public WarpCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;
        if(!player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
            return false;
        }

        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid usage! /warp (warp-name)");
            return false;
        }

        String warpName = args[0];
        Warp warp = main.getWarpManager().getWarp(warpName);
        if(warp == null) {
            player.sendMessage(ChatColor.RED + "There is so warp with the name: " + warpName);
            return false;
        }

        player.sendMessage(ChatColor.GREEN + "Teleporting to warp: " + warp.getName());
        Util.updatedTeleport(player, warp.getLocation());

        return false;
    }
}
