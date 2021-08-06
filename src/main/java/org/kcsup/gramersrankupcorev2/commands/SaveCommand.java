package org.kcsup.gramersrankupcorev2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.saves.Save;

import java.util.List;

public class SaveCommand implements CommandExecutor {
    private Main main;

    public SaveCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;

        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect Usage! /save (name)");
            return false;
        }

        List<Save> saves = main.getSaveManager().getPlayerSaves(player);

        String name = args[0];
        Save save = new Save(name, player.getLocation());

        if(saves != null){
            if(saves.size() == 3) {
                player.sendMessage(ChatColor.RED + "You already have 3 saves stored!");
                return false;
            }

            for(Save s : main.getSaveManager().getPlayerSaves(player)) {
                if(s.getName().equals(name)) {
                    player.sendMessage(ChatColor.RED + "You already have a save stored under this name.");
                    return false;
                }
            }
        }

        main.getSaveManager().storeSaveInstance(player, save);
        player.sendMessage(ChatColor.GREEN + "Saved you current location to the save: " + name);

        return false;
    }
}
