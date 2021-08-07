package org.kcsup.gramersrankupcorev2;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.kcsup.gramersrankupcorev2.ranks.Rank;
import org.kcsup.gramersrankupcorev2.saves.Save;
import org.kcsup.gramersrankupcorev2.signs.WarpSign;
import org.kcsup.gramersrankupcorev2.signs.types.LobbySign;
import org.kcsup.gramersrankupcorev2.signs.types.RankSign;
import org.kcsup.gramersrankupcorev2.signs.types.TutorialSign;

public class EventListener implements Listener {
    private Main main;

    public EventListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            String playerPing = "@" + player.getName().toLowerCase();
            if(e.getMessage().toLowerCase().contains(playerPing)) {
                String fixedMessage = e.getMessage();
                int i = e.getMessage().toLowerCase().indexOf(playerPing);
                fixedMessage = fixedMessage.replace(fixedMessage.substring(i, i + playerPing.length()),
                        ChatColor.translateAlternateColorCodes('&', "&e@" + player.getName() + "&r"));
                e.setMessage(fixedMessage);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10f, 1f);
            }
        }

        String format = main.getConfig().getString("chat-format");

        if(format == null) return;

        String chatFormat = ChatColor.translateAlternateColorCodes('&', format);
        Rank rank = main.getRankManager().getPlayerRank(e.getPlayer());
        if(rank == null) return;
        String prefix = rank.getChatPrefix();

        if (prefix == null) {
            e.setFormat(chatFormat);
        } else {
            e.setFormat(ChatColor.translateAlternateColorCodes('&', prefix) + chatFormat);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPlayedBefore()) {
            String message = ChatColor.translateAlternateColorCodes('&', "&a&l\u00AB " + player.getName() + " Joined for the First Time! \u00BB");
            Bukkit.broadcastMessage(message);
        }
        main.getRankManager().initiatePlayerRank(player);
        main.getScoreboardManager().reloadScoreboard();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if(main.getPracticeManager().isPracticing(e.getPlayer())) main.getPracticeManager().setNotPracticing(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();

        if(e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
            e.setCancelled(true);
            return;
        }

        if(e.getItem() != null && e.getItem().equals(main.getPracticeManager().getPracticeItem()) && main.getPracticeManager().isPracticing(player)) {
            Location location = main.getPracticeManager().getPlayerPracticeLocation(player);
            if(location != null) player.teleport(location);
            return;
        }

        if(e.getClickedBlock() != null) {
            if(e.getClickedBlock().getType().equals(Material.DRAGON_EGG) &&
                !e.getPlayer().isOp()) {
                e.setCancelled(true);
                return;
            }
            
            Location location = e.getClickedBlock().getLocation();
            if(main.getSignManager().isSign(location)) {
                e.setCancelled(true);

                if(main.getPracticeManager().isPracticing(player)) {
                    player.sendMessage(ChatColor.RED + "You cannot use a warp sign while in practice mode!");
                    return;
                }
                WarpSign warpSign = main.getSignManager().getSign(location);
                if(warpSign instanceof RankSign) {
                    RankSign rankSign = (RankSign) warpSign;
                    Rank playerRank = main.getRankManager().getPlayerRank(player);
                    if(playerRank == null) return;

                    if(playerRank.getWeight() < rankSign.getFromRank().getWeight()) {
                        player.sendMessage(ChatColor.RED + "You cannot use this sign!");
                        return;
                    } else if(playerRank.getWeight() == rankSign.getFromRank().getWeight()) {
                        String message = "&a&l\u00AB " + player.getName() + " just Ranked Up to&f " + rankSign.getToRank().getName() + "&a&l! \u00BB";
                        String rankUp = ChatColor.translateAlternateColorCodes('&', message);
                        Bukkit.broadcastMessage(rankUp);

                        main.getRankManager().setPlayerRank(player, rankSign.getToRank());
                    }
                } else if(warpSign instanceof LobbySign) {
                    LobbySign lobbySign = (LobbySign) warpSign;
                    Rank playerRank = main.getRankManager().getPlayerRank(player);
                    if(playerRank == null) return;

                    if(playerRank.getWeight() < lobbySign.getRequiredRank().getWeight()) {
                        player.sendMessage(ChatColor.RED + "You cannot use this sign!");
                        return;
                    }
                } else if(warpSign instanceof TutorialSign) {
                    TutorialSign tutorialSign = (TutorialSign) warpSign;
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', tutorialSign.getMessage()));
                    return;
                }
                player.teleport(warpSign.getWarp());
            }
        }


    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null) return;

        if(e.getInventory().getName().equals(player.getName() + "'s Saves")) {
            if(e.getCurrentItem().getType().equals(Material.BOOK)) {
                for(Save s : main.getSaveManager().getPlayerSaves(player)) {
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equals(s.getName())) {
                        if(main.getPracticeManager().isPracticing(player)) {
                            player.closeInventory();

                            player.sendMessage(ChatColor.RED + "You cannot teleport to a save while you're in practice mode!");
                            break;
                        }

                        player.closeInventory();

                        player.sendMessage(ChatColor.GREEN + "Teleporting to save: " + s.getName());
                        player.teleport(s.getLocation());
                        main.getSaveManager().removeSaveInstance(player, s);
                        break;
                    }
                }
            }
            e.setCancelled(true);
            return;
        }

        if(e.getCurrentItem().equals(main.getPracticeManager().getPracticeItem()) &&
                main.getPracticeManager().isPracticing(player)) {
            e.setCancelled(true);
            return;
        }

        if(!player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if(e.getItemDrop() == null) return;

        if(e.getItemDrop().getItemStack().equals(main.getPracticeManager().getPracticeItem()) &&
                main.getPracticeManager().isPracticing(player)) {
            e.setCancelled(true);
            return;
        }

        if(!player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if(!player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            switch (e.getCause()) {
                case FALL:
                case LAVA:
                case DROWNING:
                case CONTACT:
                    e.setCancelled(true);
                    break;
                case FIRE:
                case FIRE_TICK:
                    player.setFireTicks(0);
                    e.setCancelled(true);
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }
}
