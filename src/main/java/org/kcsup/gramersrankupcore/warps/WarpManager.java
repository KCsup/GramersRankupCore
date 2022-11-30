package org.kcsup.gramersrankupcore.warps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcore.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {
    private Main main;
    private File warpData;

    public WarpManager(Main main) {
        this.main = main;
        filesCheck();
    }

    private void filesCheck() {
        String warpDataPath = main.getDataFolder() + "/warpData.json";
        warpData = new File(warpDataPath);
        if(!warpData.exists()) {
            try {
                warpData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("warps", new JSONArray());

                FileWriter fileWriter = new FileWriter(warpDataPath);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Warp getWarp(String name) {
        if(getCurrentWarps() == null) return null;

        for(Warp warp : getCurrentWarps()) {
            if(warp.getName().equals(name)) return warp;
        }

        return null;
    }

    public boolean isWarp(String name) {
        if(getCurrentWarps() == null) return false;

        for(Warp warp : getCurrentWarps()) {
            if(warp.getName().equals(name)) return true;
        }

        return false;
    }

    public List<Warp> getCurrentWarps() {
        if(warpData == null) return null;

        List<Warp> currentWarps = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(warpData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray warps = file.getJSONArray("warps");

            for(Object o : warps) {
                JSONObject warpJson = (JSONObject) o;
                Warp warp = jsonToWarp(warpJson);

                if(warp != null) currentWarps.add(warp);
            }

            if(!currentWarps.isEmpty()) return currentWarps;
            else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void storeWarpInstance(Warp warp) {
        if(warpData == null || warp == null) return;

        try {
            FileReader fileReader = new FileReader(warpData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray warps = file.getJSONArray("warps");

            JSONObject warpJson = warpToJson(warp);
            warps.put(warpJson);

            FileWriter fileWriter = new FileWriter(warpData);
            fileWriter.write(file.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject warpToJson(Warp warp) {
        if(warp == null) return null;

        JSONObject warpJson = new JSONObject();
        warpJson.put("name", warp.getName());

        JSONObject locationJson = locationToJson(warp.getLocation());
        warpJson.put("location", locationJson);

        return warpJson;
    }

    private Warp jsonToWarp(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        String name = jsonObject.getString("name");
        Location location = jsonToLocation(jsonObject.getJSONObject("location"));

        return new Warp(name, location);
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
}
