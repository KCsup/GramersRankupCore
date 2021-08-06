package org.kcsup.gramersrankupcorev2.signs.types;

import org.bukkit.Location;
import org.kcsup.gramersrankupcorev2.signs.WarpSign;

public class TutorialSign extends WarpSign {
    private String message;

    public TutorialSign(Location location, String message) {
        super(location, null);
        this.message = message;

        setType("tutorial");
    }

    public TutorialSign(Location location, String message, String line1, String line2, String line3, String line4) {
        super(location, null, line1, line2, line3, line4);
        this.message = message;

        setType("tutorial");
    }

    public TutorialSign(Location location, String message, String[] lines) {
        super(location, null, lines);
        this.message = message;

        setType("tutorial");
    }

    public String getMessage() {
        return message;
    }
}
