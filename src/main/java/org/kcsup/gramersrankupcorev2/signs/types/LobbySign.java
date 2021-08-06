package org.kcsup.gramersrankupcorev2.signs.types;

import org.bukkit.Location;
import org.kcsup.gramersrankupcorev2.ranks.Rank;
import org.kcsup.gramersrankupcorev2.signs.WarpSign;

public class LobbySign extends WarpSign {
    private Rank requiredRank;

    public LobbySign(Location location, Location warp, Rank requiredRank) {
        super(location, warp);
        this.requiredRank = requiredRank;

        setType("lobby");
    }

    public LobbySign(Location location, Location warp, Rank requiredRank, String line1, String line2, String line3, String line4) {
        super(location, warp, line1, line2, line3, line4);
        this.requiredRank = requiredRank;

        setType("lobby");
    }

    public LobbySign(Location location, Location warp, Rank requiredRank, String[] lines) {
        super(location, warp, lines);
        this.requiredRank = requiredRank;

        setType("lobby");
    }

    public Rank getRequiredRank() {
        return requiredRank;
    }
}
