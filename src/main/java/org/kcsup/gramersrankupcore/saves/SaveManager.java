package org.kcsup.gramersrankupcore.saves;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveManager extends Manager {

    public SaveManager(Main main) {
        super(
                main,
                "/saveData.json",
                new JSONObject().put("players", new JSONArray())
        );
    }

    public Save getPlayerSave(Player player, String name) {
        if(getPlayerSaves(player) == null) return null;

        for(Save save : getPlayerSaves(player)) {
            if(save.getName().equals(name)) return save;
        }

        return null;
    }

    public List<Save> getPlayerSaves(Player player) {
        JSONObject file = getDataFile();

        if(file == null) return null;

        List<Save> saves = new ArrayList<>();

        UUID uuid = player.getUniqueId();


        JSONArray players = file.getJSONArray("players");

        for(Object o : players) {
            JSONObject jsonObject = (JSONObject) o;
            if(jsonObject.getString("uuid").equals(uuid.toString())) {
                JSONArray jsonSaves = jsonObject.getJSONArray("saves");
                for(Object saveObject : jsonSaves) {
                    JSONObject save = (JSONObject) saveObject;
                    saves.add(jsonToSave(save));
                }
                break;
            }
        }

        if(!saves.isEmpty()) return saves;
        else return null;
    }

    public void removeSaveInstance(Player player, Save save) {
        JSONObject file = getDataFile();

        if(file == null || save == null) return;

        UUID uuid = player.getUniqueId();


        JSONArray players = file.getJSONArray("players");

        for(Object o : players) {
            JSONObject jsonObject = (JSONObject) o;
            if(jsonObject.getString("uuid").equals(uuid.toString())) {
                JSONArray savesArray = jsonObject.getJSONArray("saves");
                for(int i = 0; i < savesArray.length(); i++) {
                    JSONObject saveJson = savesArray.getJSONObject(i);
                    Save s = jsonToSave(saveJson);

                    if(s.getName().equals(save.getName())) {
                        savesArray.remove(i);
                        break;
                    }
                }
                jsonObject.put("saves", savesArray);

                if(savesArray.isEmpty()) {
                    for(int i = 0; i < players.length(); i++) {
                        if(players.get(i) == o) {
                            players.remove(i);
                            break;
                        }
                    }
                }
                file.put("players", players);

                updateDataFile(file);

                break;
            }
        }
    }

    public void storeSaveInstance(Player player, Save save) {
        JSONObject file = getDataFile();

        if(file == null || save == null) return;

        UUID uuid = player.getUniqueId();

        if(getPlayerSaves(player) == null) {
            initiatePlayerSaves(player);
        }

        JSONArray players = file.getJSONArray("players");

        for(Object o : players) {
            JSONObject jsonObject = (JSONObject) o;
            if(jsonObject.getString("uuid").equals(uuid.toString())) {
                JSONArray saves = jsonObject.getJSONArray("saves");
                saves.put(saveToJson(save));

                updateDataFile(file);

                break;
            }
        }
    }

    public void initiatePlayerSaves(Player player) {
        JSONObject file = getDataFile();

        if(file == null) return;

        UUID uuid = player.getUniqueId();

        JSONArray players = file.getJSONArray("players");

        JSONObject playerObject = new JSONObject();
        playerObject.put("uuid", uuid.toString());
        playerObject.put("saves", new JSONArray());
        players.put(playerObject);

        updateDataFile(file);
    }

    private JSONObject saveToJson(Save save) {
        if(save == null) return null;

        JSONObject saveJson = new JSONObject();
        saveJson.put("name", save.getName());
        saveJson.put("location", Util.locationToJson(save.getLocation()));

        return saveJson;
    }

    private Save jsonToSave(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        String name = jsonObject.getString("name");
        Location location = Util.jsonToLocation(jsonObject.getJSONObject("location"));

        return new Save(name, location);
    }

}
