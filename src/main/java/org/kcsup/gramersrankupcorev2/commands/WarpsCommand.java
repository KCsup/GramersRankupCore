package org.kcsup.gramersrankupcorev2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.warps.Warp;

public class WarpsCommand implements CommandExecutor {
    private Main main;

    public WarpsCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
            return false;
        }

        if(main.getWarpManager().getCurrentWarps() == null) {
            sender.sendMessage(ChatColor.RED + "There aren't currently any warps.");
            return false;
        }

        StringBuilder warpsList = new StringBuilder(ChatColor.GREEN + "Current Warps: ");
        for(int i = 0; i < main.getWarpManager().getCurrentWarps().size(); i++) {
            Warp warp = main.getWarpManager().getCurrentWarps().get(i);
            if(warp == null) continue;

            if(i != main.getWarpManager().getCurrentWarps().size() - 1) {
                warpsList.append(warp.getName()).append(", ");
            } else {
                warpsList.append(warp.getName());
            }
        }

        sender.sendMessage(warpsList.toString());

        return false;
    }
}
