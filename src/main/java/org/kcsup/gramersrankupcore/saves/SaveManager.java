package org.kcsup.gramersrankupcore.saves;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.util.Util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        List<Save> saves = new ArrayList<>();

        UUID uuid = player.getUniqueId();

        if(dataFile == null) return null;

        try {
            FileReader fileReader = new FileReader(dataFile);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeSaveInstance(Player player, Save save) {
        UUID uuid = player.getUniqueId();

        if(dataFile == null || save == null) return;

        try {
            FileReader fileReader = new FileReader(dataFile);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
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

                    FileWriter fileWriter = new FileWriter(dataFile);
                    fileWriter.write(file.toString());
                    fileWriter.flush();

                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeSaveInstance(Player player, Save save) {
        UUID uuid = player.getUniqueId();

        if(dataFile == null || save == null) return;

        if(getPlayerSaves(player) == null) {
            initiatePlayerSaves(player);
        }

        try {
            FileReader fileReader = new FileReader(dataFile);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray players = file.getJSONArray("players");

            for(Object o : players) {
                JSONObject jsonObject = (JSONObject) o;
                if(jsonObject.getString("uuid").equals(uuid.toString())) {
                    JSONArray saves = jsonObject.getJSONArray("saves");
                    saves.put(saveToJson(save));

                    FileWriter fileWriter = new FileWriter(dataFile);
                    fileWriter.write(file.toString());
                    fileWriter.flush();

                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initiatePlayerSaves(Player player) {
        UUID uuid = player.getUniqueId();

        try {
            FileReader fileReader = new FileReader(dataFile);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray players = file.getJSONArray("players");

            JSONObject playerObject = new JSONObject();
            playerObject.put("uuid", uuid.toString());
            playerObject.put("saves", new JSONArray());
            players.put(playerObject);

            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(file.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
