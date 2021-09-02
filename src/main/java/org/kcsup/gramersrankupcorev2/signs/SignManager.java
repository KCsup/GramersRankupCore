package org.kcsup.gramersrankupcorev2.signs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.*;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.ranks.Rank;
import org.kcsup.gramersrankupcorev2.signs.types.LobbySign;
import org.kcsup.gramersrankupcorev2.signs.types.RankSign;
import org.kcsup.gramersrankupcorev2.signs.types.TutorialSign;
import org.kcsup.gramersrankupcorev2.warps.Warp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SignManager {
    private Main main;
    private File signData;

    public SignManager(Main main) {
        this.main = main;
        filesCheck();
    }

    private void filesCheck() {
        String signDataPath = main.getDataFolder() + "/signData.json";
        signData = new File(signDataPath);
        if(!signData.exists()) {
            try {
                signData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("signs", new JSONArray());

                FileWriter fileWriter = new FileWriter(signDataPath);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void storeSignInstance(WarpSign warpSign) {
        if(signData == null || warpSign == null) return;

        try {
            FileReader fileReader = new FileReader(signData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray signs = file.getJSONArray("signs");

            JSONObject sign = new JSONObject();
            sign.put("type", warpSign.getType());

            JSONObject signInfo = new JSONObject();

            Location location = warpSign.getLocation();
            JSONObject locationJson = locationToJson(location);
            signInfo.put("location", locationJson);

            Location warp = warpSign.getWarp();
            if(warp == null) signInfo.put("warp", JSONObject.NULL);
            else {
                JSONObject warpJson = locationToJson(warp);
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

            FileWriter fileWriter = new FileWriter(signData);
            fileWriter.write(file.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject locationToJson(Location location) {
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

    private Location jsonToLocation(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        World world = Bukkit.getWorld(jsonObject.getString("world"));
        double x = jsonObject.getDouble("x");
        double y = jsonObject.getDouble("y");
        double z = jsonObject.getDouble("z");
        float yaw = jsonObject.getFloat("yaw");
        float pitch = jsonObject.getFloat("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public WarpSign jsonToSign(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        JSONObject signInfo = jsonObject.getJSONObject("info");
        Location location = jsonToLocation(signInfo.getJSONObject("location"));

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
                warp = jsonToLocation(warpJson);
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
        List<WarpSign> currentSigns = new ArrayList<>();

        if(signData == null) return null;

        try {
            FileReader fileReader = new FileReader(signData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);

            JSONArray signs = file.getJSONArray("signs");

            for(Object sign : signs) {
                JSONObject jsonSign = new JSONObject(sign.toString());
                WarpSign warpSign = jsonToSign(jsonSign);

                if(warpSign != null) currentSigns.add(warpSign);
            }

            if(!currentSigns.isEmpty()) return currentSigns;
            else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSignLocationToWarp(Location location, Warp warp) {
        if(signData == null || warp == null) return;

        try {
            FileReader fileReader = new FileReader(signData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);

            JSONArray signs = file.getJSONArray("signs");

            for(Object sign : signs) {
                JSONObject jsonSign = (JSONObject) sign;
                JSONObject signLocationJson = jsonSign.getJSONObject("info").getJSONObject("location");
                Location signLocation = jsonToLocation(signLocationJson);
                if(isLocationIgnoringYawPitch(signLocation, location)) {
                    JSONObject signInfo = jsonSign.getJSONObject("info");

                    JSONObject warpJson = new JSONObject();
                    warpJson.put("warp", warp.getName());

                    signInfo.put("warp", warpJson);

                    FileWriter fileWriter = new FileWriter(signData);
                    fileWriter.write(file.toString());
                    fileWriter.flush();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WarpSign getSign(Location location) {
        if(getCurrentSigns() == null) return null;

        for(WarpSign sign : getCurrentSigns()) {
            if(isLocationIgnoringYawPitch(location, sign.getLocation())) return sign;
        }

        return null;
    }

    public boolean isSign(Location location) {
        if(getCurrentSigns() == null) return false;

        for(WarpSign sign : getCurrentSigns()) {
            if(isLocationIgnoringYawPitch(location, sign.getLocation())) return true;
        }

        return false;
    }

    public void reloadAllSigns() {
        for(WarpSign sign : getCurrentSigns()) {
            sign.reloadSign();
        }
    }

    private boolean isLocationIgnoringYawPitch(Location location, Location equals) {
        return location.getX() == equals.getX() &&
                location.getY() == equals.getY() &&
                location.getZ() == equals.getZ();
    }
}
