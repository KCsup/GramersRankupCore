package org.kcsup.gramersrankupcore.levels;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class Level {

    private final Player owner;
    private final World world;
    private final int worldId;

    public Level(Player owner, int worldId) {
        this.owner = owner;
        this.worldId = worldId;

        WorldCreator wc = new WorldCreator("(" + owner.getUniqueId().toString() + ")" + worldId);

        wc.type(WorldType.FLAT);
        wc.generatorSettings("2;0;1;"); // Creates void world
        world = wc.createWorld();

        world.setDifficulty(Difficulty.PEACEFUL);
        world.setSpawnLocation(0, 0, 0);

        for(int x = -1; x <= 1; x++)
            for(int z = -1; z <= 1; z++)
                world.getBlockAt(x, 0, z).setType(Material.STONE); // Create 3x3 at world spawn

    }

    public Level(Player owner, int worldId, World world) {
        this.owner = owner;
        this.worldId = worldId;
        this.world =   world;
    }

    public Player getOwner() {
        return owner;
    }

    public World getWorld() {
        return world;
    }

    public int getWorldId() {
        return worldId;
    }
}
