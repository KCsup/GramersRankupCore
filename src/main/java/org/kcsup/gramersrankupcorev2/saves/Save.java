package org.kcsup.gramersrankupcorev2.saves;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class Save {
    private String name;
    private Location location;

    public Save(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
