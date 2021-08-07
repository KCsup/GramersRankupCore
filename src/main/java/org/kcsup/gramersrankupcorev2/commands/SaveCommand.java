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

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Incorrect Usage! /save (name)");
            return false;
        }

        if(main.getPracticeManager().isPracticing(player)) {
            player.sendMessage(ChatColor.RED + "You cannot create a save while in practice mode!");
            return false;
        }

        List<Save> saves = main.getSaveManager().getPlayerSaves(player);

        StringBuilder name = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            if(i == args.length - 1) name.append(args[i]);
            else name.append(args[i]).append(" ");
        }
        if(name.length() > 16) {
            player.sendMessage(ChatColor.RED + "The name of the save must be 16 characters or less.");
            return false;
        }

        if(saves != null){
            if(saves.size() == 3) {
                player.sendMessage(ChatColor.RED + "You already have 3 saves stored!");
                return false;
            }

            for(Save s : main.getSaveManager().getPlayerSaves(player)) {
                if(s.getName().equals(name.toString())) {
                    player.sendMessage(ChatColor.RED + "You already have a save stored under this name.");
                    return false;
                }
            }
        }

        Save save = new Save(name.toString(), player.getLocation());
        main.getSaveManager().storeSaveInstance(player, save);
        player.sendMessage(ChatColor.GREEN + "Saved you current location to the save: " + name);
        player.teleport(player.getWorld().getSpawnLocation());

        return false;
    }
}
