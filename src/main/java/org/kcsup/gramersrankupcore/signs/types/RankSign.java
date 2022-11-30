package org.kcsup.gramersrankupcore.signs.types;

import org.bukkit.Location;
import org.kcsup.gramersrankupcore.ranks.Rank;
import org.kcsup.gramersrankupcore.signs.WarpSign;

public class RankSign extends WarpSign {
    private Rank fromRank;
    private Rank toRank;

    public RankSign(Location location, Location warp, Rank fromRank, Rank toRank) {
        super(location, warp);
        this.fromRank = fromRank;
        this.toRank = toRank;

        setType("rank");
    }

    public RankSign(Location location, Location warp, Rank fromRank, Rank toRank, String line1, String line2, String line3, String line4) {
        super(location, warp, line1, line2, line3, line4);
        this.fromRank = fromRank;
        this.toRank = toRank;

        setType("rank");
    }

    public RankSign(Location location, Location warp, Rank fromRank, Rank toRank, String[] lines) {
        super(location, warp, lines);
        this.fromRank = fromRank;
        this.toRank = toRank;

        setType("rank");
    }

    public Rank getFromRank() {
        return fromRank;
    }

    public Rank getToRank() {
        return toRank;
    }
}
