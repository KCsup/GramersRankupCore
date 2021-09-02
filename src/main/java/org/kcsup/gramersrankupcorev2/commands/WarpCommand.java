package org.kcsup.gramersrankupcorev2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.warps.Warp;

public class WarpCommand implements CommandExecutor {
    private Main main;

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
        player.teleport(warp.getLocation());

        return false;
    }
}
