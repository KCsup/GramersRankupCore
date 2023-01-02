package org.kcsup.gramersrankupcore.ranks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RankManager extends Manager {

    private final File ranksFolder;
    private final List<Rank> currentRanks;

    public RankManager(Main main) {
        super(
                main,
                "/rankData.json",
                new JSONObject().put("players", new JSONArray())
        );

        String rankFolderPath = main.getDataFolder() + "/ranks";
        ranksFolder = new File(rankFolderPath);
        if(!ranksFolder.exists()) {
            ranksFolder.mkdir();
        }

        currentRanks = new ArrayList<>();
        for(File file: Objects.requireNonNull(ranksFolder.listFiles())) {
            Rank rank = fileToRank(file);
            if(rank != null) currentRanks.add(rank);
        }
    }

    public List<Rank> getCurrentRanks() {
        return currentRanks;
    }

    public List<Rank> getCurrentRanksSorted() {
        List<Rank> sortedCurrentRanks = new ArrayList<>(currentRanks);
        Comparator<Rank> compareByWeight = Comparator.comparingInt(Rank::getWeight);
        sortedCurrentRanks.sort(compareByWeight.reversed());

        return sortedCurrentRanks;
    }

    public List<Rank> getCurrentRanksSortedReversed() {
        List<Rank> sortedCurrentRanks = new ArrayList<>(currentRanks);
        Comparator<Rank> compareByWeight = Comparator.comparingInt(Rank::getWeight);
        sortedCurrentRanks.sort(compareByWeight);

        return sortedCurrentRanks;
    }

    public Rank fileToRank(File file) {
        if(file.getName().endsWith(".json")) {
            try {
                if(ranksFolder == null) return null;

                FileReader fileReader = new FileReader(ranksFolder + "/" + file.getName());
                JSONTokener jsonTokener = new JSONTokener(fileReader);
                JSONObject jsonRank = new JSONObject(jsonTokener);

                String name = jsonRank.getString("name");

                String prefix = removeExtraCharacters(jsonRank.getString("prefix"));

                String chatPrefix = removeExtraCharacters(jsonRank.getString("chat"));

                int weight = jsonRank.getInt("weight");

                return new Rank(name, prefix, chatPrefix,weight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String removeExtraCharacters(String original) {
        String out = original;
        char[] chatChars = out.toCharArray();
        for(char c : chatChars) {
            String unicode = "\\u" + Integer.toHexString(c | 0x10000).substring(1);
            if(unicode.equals("\\u00c2")) {
                out = out.replace(String.valueOf(c), "");
            }
        }

        return out;
    }

    public Rank getRank(String name) {
        if(currentRanks.isEmpty()) return null;

        for(Rank rank : currentRanks) {
            if(name.equals(rank.getName())) return rank;
        }

        return null;
    }

    public Rank getRank(int weight) {
        if(currentRanks == null) return null;

        for(Rank rank : currentRanks) {
            if(weight == rank.getWeight()) return rank;
        }

        return null;
    }

    public Rank getDefaultRank() {
        return getRank(0);
    }

    public Rank getPlayerRank(Player player) {
        JSONObject file = getDataFile();

        if(file == null) return null;

        UUID uuid = player.getUniqueId();

        JSONArray rankDataArray = file.getJSONArray("players");

        for (Object o : rankDataArray) {
            JSONObject jsonObject = new JSONObject(o.toString());
            if (jsonObject.getString("uuid").equals(uuid.toString())) return getRank(jsonObject.getString("rank"));
        }

        return null;
    }

    public void setPlayerRank(Player player, Rank rank){
        JSONObject file = getDataFile();

        if(file == null) return;

        UUID uuid = player.getUniqueId();

        JSONArray rankDataArray = file.getJSONArray("players");

        for (Object o : rankDataArray) {
            JSONObject jsonObject = (JSONObject) o;
            if (jsonObject.getString("uuid").equals(uuid.toString())) {
                jsonObject.put("rank", rank.getName());

                updateDataFile(file);

                break;
            }
        }
        main.getScoreboardUtil().reloadScoreboard();
    }

    public void initiateAllPlayerRanks() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            initiatePlayerRank(player);
        }
    }

    public void initiatePlayerRank(Player player){
        JSONObject file = getDataFile();

        if(file == null || getPlayerRank(player) != null) return;

        UUID uuid = player.getUniqueId();

        JSONArray rankDataArray = file.getJSONArray("players");

        if(getDefaultRank() == null) return;

        JSONObject playerObject = new JSONObject();
        playerObject.put("uuid", uuid.toString());
        playerObject.put("rank", getDefaultRank().getName());
        rankDataArray.put(playerObject);

        updateDataFile(file);

        main.getScoreboardUtil().reloadScoreboard();
    }
}
