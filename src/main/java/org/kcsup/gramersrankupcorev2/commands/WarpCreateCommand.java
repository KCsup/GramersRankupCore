package org.kcsup.gramersrankupcorev2.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.warps.Warp;

public class WarpCreateCommand implements CommandExecutor {
    private Main main;

    public WarpCreateCommand(Main main) {
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

        String name = args[0];

        StringBuilder fullArgsBuilder = new StringBuilder();
        for (String arg : args) {
            fullArgsBuilder.append(arg).append(" ");
        }
        String fullArgs = fullArgsBuilder.toString();

        Location location;

        String locationArgs = fullArgs.substring(fullArgs.indexOf("{") + 1, fullArgs.indexOf("}"));
        if(locationArgs.equals("thisfixed")) {
            Location playerLocation = player.getLocation();
            playerLocation.setPitch(0);
            playerLocation.setYaw(roundToCleanPitch(playerLocation.getYaw()));
            location = playerLocation;
        } else if(locationArgs.equals("this")) {
            location = player.getLocation();
        } else {
            String[] lArgs = locationArgs.split(",");
            World world = Bukkit.getWorld(lArgs[0]);
            double x = Double.parseDouble(lArgs[1]);
            double y = Double.parseDouble(lArgs[2]);
            double z = Double.parseDouble(lArgs[3]);
            float yaw = Float.parseFloat(lArgs[4]);
            float pitch = Float.parseFloat(lArgs[5]);
            location = new Location(world, x, y, z, yaw, pitch);
        }
        Warp warp = new Warp(name, location);
        main.getWarpManager().storeWarpInstance(warp);

        return false;
    }

    private float roundToCleanPitch(float pitch) {
        float fixed = 0;
        if(pitch > -45 && pitch < 45) {
            fixed = 0;
        } else if(pitch > 45 && pitch < 135) {
            fixed = 90;
        } else if(pitch > 135 && pitch < -135) {
            fixed = 180;
        } else if(pitch > -135 && pitch < -45) {
            fixed = -90;
        }

        return fixed;
    }
}
