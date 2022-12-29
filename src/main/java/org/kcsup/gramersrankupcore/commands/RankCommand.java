package org.kcsup.gramersrankupcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.ranks.Rank;

public class RankCommand implements CommandExecutor {
    private final Main main;

    public RankCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You must be an admin to use this command!");
                return false;
            }

            Player player = Bukkit.getOfflinePlayer(args[0]).getPlayer();
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Either this is an invalid player, or they are not online right now!");
                return false;
            }

            Rank rank = null;
            for(Rank r : main.getRankManager().getCurrentRanks()) {
                if(r.getName().equalsIgnoreCase(args[1])) rank = r;
            }

            if (rank == null) {
                sender.sendMessage(ChatColor.RED + "Invalid rank!");
                return false;
            }

            main.getRankManager().setPlayerRank(player, rank);
            sender.sendMessage(ChatColor.GREEN + "Changed " + player.getName() + "'s Rank to " + rank.getName() + "!");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder rankList = new StringBuilder(ChatColor.GREEN + "Current Ranks:\n" + ChatColor.WHITE);
                for (Rank rank : main.getRankManager().getCurrentRanksSorted()) {
                    rankList.append(ChatColor.GREEN).append(rank.getName()).append(ChatColor.RESET).append(" (")
                        .append(ChatColor.translateAlternateColorCodes('&', rank.getPrefix())).append(ChatColor.RESET)
                        .append(") [").append(ChatColor.translateAlternateColorCodes('&', rank.getChatPrefix()))
                        .append(ChatColor.RESET).append("]\n");
                }
                sender.sendMessage(rankList.toString());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid Usage.");
        }
        return false;
    }
}
