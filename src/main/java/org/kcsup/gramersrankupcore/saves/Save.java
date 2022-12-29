package org.kcsup.gramersrankupcore.saves;

import org.bukkit.Location;

public class Save {
    private final String name;
    private final Location location;

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
