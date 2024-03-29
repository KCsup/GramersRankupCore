package org.kcsup.gramersrankupcore.signs;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.ranks.Rank;
import org.kcsup.gramersrankupcore.signs.types.LobbySign;
import org.kcsup.gramersrankupcore.signs.types.RankSign;
import org.kcsup.gramersrankupcore.signs.types.TutorialSign;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.util.Util;
import org.kcsup.gramersrankupcore.warps.Warp;

import java.util.ArrayList;
import java.util.List;

public class SignManager extends Manager {

    private final List<Player> signCooldown;

    public SignManager(Main main) {
        super(
                main,
                "/signData.json",
                new JSONObject().put("signs", new JSONArray())
        );

        signCooldown = new ArrayList<>();
    }

    @Override
    public void startup() {
        reloadAllSigns();
    }

    @Override
    public void shutdown() {
        clearCooldown();
    }

    public boolean isOnCooldown(Player player) {
        return signCooldown.contains(player);
    }

    public void addCooldown(Player player) {
        if(!isOnCooldown(player)) signCooldown.add(player);
    }

    public void removeCooldown(Player player) {
        signCooldown.remove(player);
    }

    public void clearCooldown() {
        for(Player p : signCooldown) removeCooldown(p);
    }

    public void storeSignInstance(WarpSign warpSign) {
        JSONObject file = getDataFile();

        if(file == null || warpSign == null) return;

        JSONArray signs = file.getJSONArray("signs");

        JSONObject sign = new JSONObject();
        sign.put("type", warpSign.getType());

        JSONObject signInfo = new JSONObject();

        Location location = warpSign.getLocation();
        JSONObject locationJson = Util.locationToJson(location);
        signInfo.put("location", locationJson);

        Location warp = warpSign.getWarp();
        if(warp == null) signInfo.put("warp", JSONObject.NULL);
        else {
            JSONObject warpJson = Util.locationToJson(warp);
            signInfo.put("warp", warpJson);
        }


        JSONArray linesArray = new JSONArray(warpSign.getLines());
        signInfo.put("lines", linesArray);

        if(warpSign instanceof RankSign) {
            RankSign rankSign = (RankSign) warpSign;
            Rank fromRank = rankSign.getFromRank();
            signInfo.put("fromRank", fromRank.getName());

            Rank toRank = rankSign.getToRank();
            signInfo.put("toRank", toRank.getName());
        } else if(warpSign instanceof LobbySign) {
            LobbySign lobbySign = (LobbySign) warpSign;
            Rank requiredRank = lobbySign.getRequiredRank();
            signInfo.put("requiredRank", requiredRank.getName());
        } else if(warpSign instanceof TutorialSign) {
            TutorialSign tutorialSign = (TutorialSign) warpSign;
            String message = tutorialSign.getMessage();
            signInfo.put("message", message);
        }

        sign.put("info", signInfo);
        signs.put(sign);

        updateDataFile(file);
    }

    public WarpSign jsonToSign(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        JSONObject signInfo = jsonObject.getJSONObject("info");
        Location location = Util.jsonToLocation(signInfo.getJSONObject("location"));

        Location warp;
        Object warpJ = signInfo.get("warp");
        if(warpJ.getClass().equals(JSONObject.NULL.getClass())) {
            warp = null;
        } else {
            JSONObject warpJson = (JSONObject) warpJ;
            if (warpJson.has("warp")) {
                String warpName = warpJson.getString("warp");
                if (main.getWarpManager().isWarp(warpName)) {
                    Warp w = main.getWarpManager().getWarp(warpName);
                    warp = w.getLocation();
                } else {
                    warp = null;
                }
            } else {
                warp = Util.jsonToLocation(warpJson);
            }
        }

        JSONArray jsonLines = signInfo.getJSONArray("lines");
        String[] lines  = new String[4];
        for(int i = 0; i < jsonLines.length(); i++) {
            Object o = jsonLines.get(i);
            if(o.getClass() == String.class) {
                String s = String.valueOf(o);
                lines[i] = s;
            } else {
                lines[i] = null;
            }
        }

        String type = jsonObject.getString("type");

        switch (type) {
            case "default":
                return new WarpSign(location, warp, lines);
            case "rank":
                Rank fromRank = main.getRankManager().getRank(signInfo.getString("fromRank"));
                Rank toRank = main.getRankManager().getRank(signInfo.getString("toRank"));
                return new RankSign(location, warp, fromRank, toRank, lines);
            case "lobby":
                Rank requiredRank = main.getRankManager().getRank(signInfo.getString("requiredRank"));
                return new LobbySign(location, warp, requiredRank, lines);
            case "tutorial":
                String message = signInfo.getString("message");
                return new TutorialSign(location, message, lines);
            default:
                return null;
        }
    }

    public List<WarpSign> getCurrentSigns() {
        JSONObject file = getDataFile();

        if(file == null) return null;

        List<WarpSign> currentSigns = new ArrayList<>();

        JSONArray signs = file.getJSONArray("signs");

        for(Object sign : signs) {
            JSONObject jsonSign = new JSONObject(sign.toString());
            WarpSign warpSign = jsonToSign(jsonSign);

            if(warpSign != null) currentSigns.add(warpSign);
        }

        if(!currentSigns.isEmpty()) return currentSigns;
        else return null;
    }

    public void setSignLocationToWarp(Location location, Warp warp) {
        JSONObject file = getDataFile();

        if(file == null || warp == null) return;

        JSONArray signs = file.getJSONArray("signs");

        for(Object sign : signs) {
            JSONObject jsonSign = (JSONObject) sign;
            JSONObject signLocationJson = jsonSign.getJSONObject("info").getJSONObject("location");
            Location signLocation = Util.jsonToLocation(signLocationJson);
            if(Util.isLocationIgnoringYawPitch(signLocation, location)) {
                JSONObject signInfo = jsonSign.getJSONObject("info");

                JSONObject warpJson = new JSONObject();
                warpJson.put("warp", warp.getName());

                signInfo.put("warp", warpJson);

                updateDataFile(file);

                break;
            }
        }
    }

    public WarpSign getSign(Location location) {
        if(getCurrentSigns() == null) return null;

        for(WarpSign sign : getCurrentSigns()) {
            if(Util.isLocationIgnoringYawPitch(location, sign.getLocation())) return sign;
        }

        return null;
    }

    public boolean isSign(Location location) {
        if(getCurrentSigns() == null) return false;

        for(WarpSign sign : getCurrentSigns()) {
            if(Util.isLocationIgnoringYawPitch(location, sign.getLocation())) return true;
        }

        return false;
    }

    public void reloadAllSigns() {
        for(WarpSign sign : getCurrentSigns()) {
            sign.reloadSign();
        }
    }


}
