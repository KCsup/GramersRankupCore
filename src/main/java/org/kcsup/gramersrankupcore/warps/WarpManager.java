package org.kcsup.gramersrankupcore.warps;

import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.util.Util;

import java.util.ArrayList;
import java.util.List;

public class WarpManager extends Manager {

    public WarpManager(Main main) {
        super(
                main,
                "/warpData.json",
                new JSONObject().put("warps", new JSONArray())
        );
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
        JSONObject file = getDataFile();

        if(file == null) return null;

        List<Warp> currentWarps = new ArrayList<>();

        JSONArray warps = file.getJSONArray("warps");

        for(Object o : warps) {
            JSONObject warpJson = (JSONObject) o;
            Warp warp = jsonToWarp(warpJson);

            if(warp != null) currentWarps.add(warp);
        }

        if(!currentWarps.isEmpty()) return currentWarps;
        else return null;
    }

    public void storeWarpInstance(Warp warp) {
        JSONObject file = getDataFile();

        if(file == null || warp == null) return;

        JSONArray warps = file.getJSONArray("warps");

        JSONObject warpJson = warpToJson(warp);
        warps.put(warpJson);

        updateDataFile(file);
    }

    private JSONObject warpToJson(Warp warp) {
        if(warp == null) return null;

        JSONObject warpJson = new JSONObject();
        warpJson.put("name", warp.getName());

        JSONObject locationJson = Util.locationToJson(warp.getLocation());
        warpJson.put("location", locationJson);

        return warpJson;
    }

    private Warp jsonToWarp(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        String name = jsonObject.getString("name");
        Location location = Util.jsonToLocation(jsonObject.getJSONObject("location"));

        return new Warp(name, location);
    }
}
