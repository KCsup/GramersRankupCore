package org.kcsup.gramersrankupcore.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.ranks.Rank;

import java.util.List;

public class ScoreboardManager {
    private Main main;
    private Scoreboard scoreboard;

    public ScoreboardManager(Main main) {
        this.main = main;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void reloadScoreboard() {
//        for(Player player : Bukkit.getOnlinePlayers()) {
//            Scoreboard pScoreboard = player.getScoreboard();
//            Team team = pScoreboard.getPlayerTeam(player);
//            if(team != null) team.removePlayer(player);
//        }

        if(Bukkit.getScoreboardManager() != null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        } else return;

        List<Rank> sortedCurrentRanks = main.getRankManager().getCurrentRanksSorted();

        for(Rank rank : sortedCurrentRanks) {
            String rankWeight;
            int rankWeightInt = sortedCurrentRanks.indexOf(rank);
            if(rankWeightInt < 10) rankWeight = "0" + rankWeightInt;
            else rankWeight = String.valueOf(rankWeightInt);

            Team team = scoreboard.registerNewTeam(rankWeight + rank.getName());
            team.setDisplayName(rank.getName());
            String prefix = rank.getPrefix();
            if(prefix != null) team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
            for(Player player : Bukkit.getOnlinePlayers()) {
                Rank playerRank = main.getRankManager().getPlayerRank(player);
                if(playerRank == null) continue;

                if(playerRank.getName().equals(team.getDisplayName())) {
                    team.addPlayer(player);
                }
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()) player.setScoreboard(scoreboard);
    }

    public void resetPlayerScoreboard(Player player) {
        Scoreboard pScoreboard = player.getScoreboard();
        Team team = pScoreboard.getPlayerTeam(player);
        if(team != null) team.removePlayer(player);

        player.setScoreboard(main.getServer().getScoreboardManager().getMainScoreboard());
    }
}
