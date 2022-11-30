package org.kcsup.gramersrankupcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcore.Main;

public class PracticeCommand implements CommandExecutor {
    private Main main;

    public PracticeCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;
        if(!main.getPracticeManager().isPracticing(player)) {
            main.getPracticeManager().setPracticing(player);
            player.sendMessage(ChatColor.GREEN + "Entered practice mode!");
        } else {
            player.sendMessage(ChatColor.RED + "You are already in practice mode!");
        }

        return false;
    }
}
