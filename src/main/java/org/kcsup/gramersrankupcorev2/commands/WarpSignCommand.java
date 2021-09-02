package org.kcsup.gramersrankupcorev2.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.ranks.Rank;
import org.kcsup.gramersrankupcorev2.signs.WarpSign;
import org.kcsup.gramersrankupcorev2.signs.types.LobbySign;
import org.kcsup.gramersrankupcorev2.signs.types.RankSign;
import org.kcsup.gramersrankupcorev2.signs.types.TutorialSign;
import org.kcsup.gramersrankupcorev2.warps.Warp;

public class WarpSignCommand implements CommandExecutor {
    private Main main;

    public WarpSignCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;
        if(!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
            return false;
        }

        WorldEditPlugin worldEditPlugin = main.getWorldEditPlugin();
        if(worldEditPlugin == null) {
            player.sendMessage(ChatColor.RED + "WorldEdit is not installed on this server. This command doesn't work!");
            return false;
        }

        Selection selection = worldEditPlugin.getSelection(player);
        if(selection == null) return false;

        if(isLocationIgnoringYawPitch(selection.getMaximumPoint(), selection.getMinimumPoint())) {
            try {
                WarpSign warpSign = null;
                Warp w = null;
                Location location = selection.getMaximumPoint();
                Location warp = null;
                Block block = location.getBlock();

                StringBuilder fullArgsBuilder = new StringBuilder();
                for (String arg : args) {
                    fullArgsBuilder.append(arg).append(" ");
                }
                String fullArgs = fullArgsBuilder.toString();
                if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
                    if (args.length < 1) return false;
                    String locationArgs = fullArgs.substring(fullArgs.indexOf("{") + 1, fullArgs.indexOf("}"));
                    if (locationArgs.equalsIgnoreCase("this")) {
                        warp = player.getLocation();
                    } else if(locationArgs.equalsIgnoreCase("spawn")) {
                        warp = player.getWorld().getSpawnLocation();
                    } else if(locationArgs.contains("warp:")) {
                        String warpName = locationArgs.replace("warp:", "");
                        if(main.getWarpManager().isWarp(warpName)) {
                            w = main.getWarpManager().getWarp(warpName);
                            warp = w.getLocation();
                        }
                    }

                    if (warp == null) return false;

                    String type = args[0];
                    String dividedArgs = fullArgs.substring(fullArgs.indexOf("[") + 1, fullArgs.indexOf("]"));
                    if (type.equalsIgnoreCase("default")) {
                        warpSign = new WarpSign(location, warp, null, "Warp To:", dividedArgs, null);

                    } else if (type.equalsIgnoreCase("rank")) {
                        String[] rankNames = dividedArgs.split(",");
                        if (rankNames.length > 2) return false;

                        Rank fromRank = main.getRankManager().getRank(rankNames[0]);
                        Rank toRank = main.getRankManager().getRank(rankNames[1]);

                        if (fromRank == null || toRank == null) return false;

                        String line1 = "&aYou Completed";
                        String line2 = "&aRank&f " + fromRank.getName();
                        String line3 = "&bClick this Sign to";
                        String line4 = "&bRank Up to&f " + toRank.getName() + "&b!";

                        warpSign = new RankSign(location, warp, fromRank, toRank, line1, line2, line3, line4);
                    } else if (type.equalsIgnoreCase("lobby")) {
                        Rank rank = main.getRankManager().getRank(dividedArgs);
                        if (rank == null) return false;

                        String line = "Rank&f " + rank.getName();

                        warpSign = new LobbySign(location, warp, rank, null, "Warp To:", line, null);
                    } else if (type.equalsIgnoreCase("tutorial")) {
                        String[] tutorialInfo = dividedArgs.split(",");
                        if (tutorialInfo.length > 2) return false;

                        String name = tutorialInfo[0];
                        String message = tutorialInfo[1];

                        warpSign = new TutorialSign(location, message, null, "Tutorial For:", name, null);
                    }
                    if(warpSign != null) {
                        main.getSignManager().storeSignInstance(warpSign);
                        if(w != null) {
                            main.getSignManager().setSignLocationToWarp(warpSign.getLocation(), w);
                        }
                    }
                }
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "You used this command incorrectly. Fuck you.");
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.RED + "Selection is more than one block.");
        }



        return false;
    }

    private boolean isLocationIgnoringYawPitch(Location location, Location equals) {
        return location.getX() == equals.getX() &&
                location.getY() == equals.getY() &&
                location.getZ() == equals.getZ();
    }
}
