package org.kcsup.gramersrankupcore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcore.Main;

public class UnpracticeCommand implements CommandExecutor {
    private final Main main;

    public UnpracticeCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;
        if(main.getPracticeManager().isPracticing(player)) {
            main.getPracticeManager().setNotPracticing(player);
            player.sendMessage(ChatColor.GREEN + "Exited practice mode!");
        } else {
            player.sendMessage(ChatColor.RED + "You aren't currently in practice mode!");
        }

        return false;
    }
}
