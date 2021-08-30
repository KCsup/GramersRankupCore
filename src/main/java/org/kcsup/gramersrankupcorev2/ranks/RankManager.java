package org.kcsup.gramersrankupcorev2.ranks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcorev2.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RankManager {
    private Main main;
    private File ranksFolder;
    private File rankData;

    public RankManager(Main main) {
        this.main = main;
        filesCheck();
    }

    private void filesCheck() {
        String rankFolderPath = main.getDataFolder() + "/ranks";
        ranksFolder = new File(rankFolderPath);
        if(!ranksFolder.exists()) {
            ranksFolder.mkdir();
        }

        String rankDataPath = main.getDataFolder() + "/rankData.json";
        rankData = new File(rankDataPath);
        if(!rankData.exists()) {
            try {
                rankData.createNewFile();

                JSONObject fileObject = new JSONObject();
                fileObject.put("players", new JSONArray());

                FileWriter fileWriter = new FileWriter(rankDataPath);
                fileWriter.write(fileObject.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Rank> getCurrentRanks() {
        List<Rank> currentRanks = new ArrayList<>();

        if(main == null) return null;

        if(ranksFolder.listFiles() == null) return null;
        for(File file: Objects.requireNonNull(ranksFolder.listFiles())) {
            Rank rank = fileToRank(file);
            if(rank != null) currentRanks.add(rank);
        }

        if(!currentRanks.isEmpty()) return currentRanks;
        else return null;
    }

    public List<Rank> getCurrentRanksSorted() {
        List<Rank> sortedCurrentRanks = new ArrayList<>(getCurrentRanks());
        Comparator<Rank> compareByWeight = Comparator.comparingInt(Rank::getWeight);
        sortedCurrentRanks.sort(compareByWeight.reversed());

        return sortedCurrentRanks;
    }

    public List<Rank> getCurrentRanksSortedReversed() {
        List<Rank> sortedCurrentRanks = new ArrayList<>(getCurrentRanks());
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

                String prefix = jsonRank.getString("prefix");
                char[] prefixChars = prefix.toCharArray();
                for(char c : prefixChars) {
                    String unicode = "\\u" + Integer.toHexString(c | 0x10000).substring(1);
                    if(unicode.equals("\\u00c2")) {
                        prefix = prefix.replace(String.valueOf(c), "");
                    }
                }
                if(prefix.length() > 16) prefix = null;

                String chatPrefix = jsonRank.getString("chat");
                char[] chatChars = chatPrefix.toCharArray();
                for(char c : chatChars) {
                    String unicode = "\\u" + Integer.toHexString(c | 0x10000).substring(1);
                    if(unicode.equals("\\u00c2")) {
                        chatPrefix = chatPrefix.replace(String.valueOf(c), "");
                    }
                }

                int weight = jsonRank.getInt("weight");

                return new Rank(name, prefix, chatPrefix,weight);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Rank getRank(String name) {
        if(getCurrentRanks() == null) return null;

        for(Rank rank : getCurrentRanks()) {
            if(name.equals(rank.getName())) return rank;
        }

        return null;
    }

    public Rank getRank(int weight) {
        if(getCurrentRanks() == null) return null;

        for(Rank rank : getCurrentRanks()) {
            if(weight == rank.getWeight()) return rank;
        }

        return null;
    }

    public Rank getDefaultRank() {
        return getRank(0);
    }

    public Rank getPlayerRank(Player player) {
        UUID uuid = player.getUniqueId();

        if(rankData == null) return null;

        try {
            FileReader fileReader = new FileReader(rankData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject rankJson = new JSONObject(jsonTokener);
            JSONArray rankDataArray = rankJson.getJSONArray("players");

            for (Object o : rankDataArray) {
                JSONObject jsonObject = new JSONObject(o.toString());
                if (jsonObject.getString("uuid").equals(uuid.toString())) return getRank(jsonObject.getString("rank"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void setPlayerRank(Player player, Rank rank){
        UUID uuid = player.getUniqueId();

        if(rankData == null) return;

        try {
            FileReader fileReader = new FileReader(rankData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject rankJson = new JSONObject(jsonTokener);
            JSONArray rankDataArray = rankJson.getJSONArray("players");

            for (Object o : rankDataArray) {
                JSONObject jsonObject = (JSONObject) o;
                if (jsonObject.getString("uuid").equals(uuid.toString())) {
                    jsonObject.put("rank", rank.getName());

                    FileWriter fileWriter = new FileWriter(rankData);
                    fileWriter.write(rankJson.toString());
                    fileWriter.flush();

                    break;
                }
            }
            main.getScoreboardManager().reloadScoreboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initiateAllPlayerRanks() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            initiatePlayerRank(player);
        }
    }

    public void initiatePlayerRank(Player player){
        if(getPlayerRank(player) != null) return;

        UUID uuid = player.getUniqueId();

        if(rankData == null) return;

        try {
            FileReader fileReader = new FileReader(rankData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject rankJson = new JSONObject(jsonTokener);
            JSONArray rankDataArray = rankJson.getJSONArray("players");

            if(getDefaultRank() == null) return;

            JSONObject playerObject = new JSONObject();
            playerObject.put("uuid", uuid.toString());
            playerObject.put("rank", getDefaultRank().getName());
            rankDataArray.put(playerObject);

            FileWriter fileWriter = new FileWriter(rankData);
            fileWriter.write(rankJson.toString());
            fileWriter.flush();

            main.getScoreboardManager().reloadScoreboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
