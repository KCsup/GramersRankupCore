package org.kcsup.gramersrankupcore.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class WarpSign {
    private final Location location;
    private final Location warp;
    private String[] lines = new String[4];
    private Sign sign;

    private String type;

    public WarpSign(Location location, Location warp) {
        this.location = location;
        this.warp = warp;
        lines = new String[4];

        if(location.getBlock().getType() == Material.WALL_SIGN ||
            location.getBlock().getType() == Material.SIGN_POST) {
            sign = (Sign) location.getBlock().getState();
            reloadSign();
        }

        this.type = "default";
    }

    public WarpSign(Location location, Location warp, String line1, String line2, String line3, String line4) {
        this.location = location;
        this.warp = warp;
        lines[0] = line1;
        lines[1] = line2;
        lines[2] = line3;
        lines[3] = line4;

        if(location.getBlock().getType() == Material.WALL_SIGN ||
                location.getBlock().getType() == Material.SIGN_POST) {
            sign = (Sign) location.getBlock().getState();
            reloadSign();
        }

        this.type = "default";
    }

    public WarpSign(Location location, Location warp, String[] lines) {
        this.location = location;
        this.warp = warp;
        this.lines = lines;

        if(location.getBlock().getType() == Material.WALL_SIGN ||
                location.getBlock().getType() == Material.SIGN_POST) {
            sign = (Sign) location.getBlock().getState();
            reloadSign();
        }

        this.type = "default";
    }

    public Location getLocation() {
        return location;
    }

    public Location getWarp() {
        return warp;
    }

    public void reloadSign() {
        if(sign == null) return;

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line != null) sign.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
            else sign.setLine(i, null);
        }
        sign.update();
    }

    public String[] getLines() {
        return lines;
    }

    public String getLineAt(int index) {
        if(index >= 4 || index < 0) return null;

        return lines[index];
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
