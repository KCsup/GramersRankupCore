package org.kcsup.gramersrankupcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class Util {

    public static JSONObject locationToJson(Location location) {
        if(location == null) return null;

        JSONObject locationJson = new JSONObject();
        locationJson.put("world", location.getWorld().getName());
        locationJson.put("x", location.getX());
        locationJson.put("y", location.getY());
        locationJson.put("z", location.getZ());
        locationJson.put("yaw", location.getYaw());
        locationJson.put("pitch", location.getPitch());

        return locationJson;
    }

    public static Location jsonToLocation(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        World world = Bukkit.getWorld(jsonObject.getString("world"));
        double x = jsonObject.getDouble("x");
        double y = jsonObject.getDouble("y");
        double z = jsonObject.getDouble("z");
        float yaw = jsonObject.getFloat("yaw");
        float pitch = jsonObject.getFloat("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean isLocationIgnoringYawPitch(Location location, Location equals) {
        return location.getX() == equals.getX() &&
                location.getY() == equals.getY() &&
                location.getZ() == equals.getZ();
    }

    public static void updatedTeleport(Player player, Location location) {
        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        if(!location.getChunk().isLoaded()) location.getChunk().load();
    }
}
