package org.kcsup.gramersrankupcorev2.saves;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcorev2.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveManager {
    private Main main;
    private File saveData;

    public SaveManager(Main main) {
        this.main = main;
        filesCheck();
    }

    private void filesCheck() {
        String saveDataPath = main.getDataFolder() + "/saveData.json";
        saveData = new File(saveDataPath);
        if(!saveData.exists()) {
            try {
                saveData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("players", new JSONArray());

                FileWriter fileWriter = new FileWriter(saveDataPath);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        if(saveData == null) return null;

        try {
            FileReader fileReader = new FileReader(saveData);
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

        if(saveData == null || save == null) return;

        try {
            FileReader fileReader = new FileReader(saveData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray players = file.getJSONArray("players");

            for(Object o : players) {
                JSONObject jsonObject = (JSONObject) o;
                if(jsonObject.getString("uuid").equals(uuid.toString())) {
                    List<Save> savesList = getPlayerSaves(player);
                    for(Save s : savesList) {
                        if(s.getName().equals(save.getName())) {
                            savesList.remove(s);
                            break;
                        }
                    }
                    JSONArray saves = new JSONArray(savesList);
                    jsonObject.put("saves", saves);

                    if(saves.isEmpty()) {
                        for(int i = 0; i < players.length(); i++) {
                            if(players.get(i) == o) {
                                players.remove(i);
                                break;
                            }
                        }
                    }

                    FileWriter fileWriter = new FileWriter(saveData);
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

        if(saveData == null || save == null) return;

        if(getPlayerSaves(player) == null) {
            initiatePlayerSaves(player);
        }

        try {
            FileReader fileReader = new FileReader(saveData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray players = file.getJSONArray("players");

            for(Object o : players) {
                JSONObject jsonObject = (JSONObject) o;
                if(jsonObject.getString("uuid").equals(uuid.toString())) {
                    JSONArray saves = jsonObject.getJSONArray("saves");
                    saves.put(saveToJson(save));

                    FileWriter fileWriter = new FileWriter(saveData);
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
            FileReader fileReader = new FileReader(saveData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray players = file.getJSONArray("players");

            JSONObject playerObject = new JSONObject();
            playerObject.put("uuid", uuid.toString());
            playerObject.put("saves", new JSONArray());
            players.put(playerObject);

            FileWriter fileWriter = new FileWriter(saveData);
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
        saveJson.put("location", locationToJson(save.getLocation()));

        return saveJson;
    }

    private Save jsonToSave(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        String name = jsonObject.getString("name");
        Location location = jsonToLocation(jsonObject.getJSONObject("location"));

        return new Save(name, location);
    }

    public JSONObject locationToJson(Location location) {
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

    public Location jsonToLocation(JSONObject jsonObject) {
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
