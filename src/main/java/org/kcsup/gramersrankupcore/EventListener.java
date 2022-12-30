package org.kcsup.gramersrankupcore;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.kcsup.gramersrankupcore.menu.Menu;
import org.kcsup.gramersrankupcore.ranks.Rank;
import org.kcsup.gramersrankupcore.saves.Save;
import org.kcsup.gramersrankupcore.signs.WarpSign;
import org.kcsup.gramersrankupcore.signs.types.LobbySign;
import org.kcsup.gramersrankupcore.signs.types.RankSign;
import org.kcsup.gramersrankupcore.signs.types.TutorialSign;
import org.kcsup.gramersrankupcore.util.Util;
import org.kcsup.gramersrankupcore.warps.Warp;

public class EventListener implements Listener {
    private final Main main;

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
        main.getScoreboardUtil().reloadScoreboard();

        main.getMenuManager().playerMenuCheck(player);
        main.getVisibilityUtil().visibilityItemCheck(player);
        main.getVisibilityUtil().updateInvisible();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if(main.getPractice().isPracticing(e.getPlayer())) main.getPractice().setNotPracticing(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();

        if(e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOIL) {
            e.setCancelled(true);
            return;
        }

        if(e.getItem() != null) {
            if(e.getItem().equals(main.getPractice().getPracticeItem()) && main.getPractice().isPracticing(player)) {
                Location location = main.getPractice().getPlayerPracticeLocation(player);
                if(location != null) Util.updatedTeleport(player, location);
                return;
            }

            if(e.getItem().equals(main.getVisibilityUtil().getVisibilityItem(player))) {
                main.getVisibilityUtil().toggleVisibility(player);
            }

            for(Menu menu : main.getMenuManager().getCurrentMenus()) {
                if(e.getItem().equals(menu.getItem())) {
                    player.openInventory(menu.getInventory());
                }
            }
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

                if(main.getPractice().isPracticing(player)) {
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
                    }
                    else if(playerRank.getWeight() == rankSign.getFromRank().getWeight()) {
                        String message = "&a&l\u00AB " + player.getName() + " just Ranked Up to&f " + rankSign.getToRank().getName() + "&a&l! \u00BB";
                        String rankUp = ChatColor.translateAlternateColorCodes('&', message);
                        Bukkit.broadcastMessage(rankUp);

                        main.getRankManager().setPlayerRank(player, rankSign.getToRank());
                    }
                    else player.sendMessage(ChatColor.GREEN + "Sending you to rank " + rankSign.getToRank().getName());
                } else if(warpSign instanceof LobbySign) {
                    LobbySign lobbySign = (LobbySign) warpSign;
                    Rank playerRank = main.getRankManager().getPlayerRank(player);
                    if(playerRank == null) return;

                    if(playerRank.getWeight() < lobbySign.getRequiredRank().getWeight()) {
                        player.sendMessage(ChatColor.RED + "You cannot use this sign!");
                        return;
                    }
                } else if(warpSign instanceof TutorialSign) {
                    if(main.getSignManager().isOnCooldown(player)) return;

                    TutorialSign tutorialSign = (TutorialSign) warpSign;
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', tutorialSign.getMessage()));
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);

                    main.getSignManager().addCooldown(player);
                    Bukkit.getScheduler().runTaskLater(main, () -> main.getSignManager().removeCooldown(player), 3 * 20L);
                    return;
                }

                Util.updatedTeleport(player, warpSign.getWarp());
            }
        }


    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if(e.getCurrentItem() == null) return;

        if(main.getMenuManager().isMenuItem(e.getCurrentItem())) {
            e.setCancelled(true);
            Menu menu = main.getMenuManager().getMenu(e.getCurrentItem());
            player.openInventory(menu.getInventory());
        }

        if(main.getMenuManager().isMenuInventory(e.getInventory())) {
            if(e.getInventory().getTitle().equals("Main Menu")) {
                switch (e.getCurrentItem().getType()) {
                    case WORKBENCH:
                        Warp mainLobby = main.getWarpManager().getWarp("Main_Lobby");
                        if(mainLobby != null) Util.updatedTeleport(player, mainLobby.getLocation());
                        break;
                    case PRISMARINE_SHARD:
                        player.openInventory(main.getMenuManager().getRankMenu());
                        break;
                    default:
                        break;
                }
            }
            e.setCancelled(true);
            return;
        }

        if(e.getInventory().getTitle().equals("Ranks")) {
            e.setCancelled(true);

            if(main.getPractice().isPracticing(player)) {
                player.sendMessage(ChatColor.RED + "You cannot use the ranks menu while in practice mode.");
                player.closeInventory();
                return;
            }

            String rankName;
            if(e.getCurrentItem().getItemMeta() != null)
                rankName = e.getCurrentItem().getItemMeta().getLore().get(0);
            else
                return;

            Rank rank = main.getRankManager().getRank(rankName);

            // FOR BETA ONLY
            // TODO: DELETE FOR FULL RELEASE
            if(main.getConfig().contains("rank-limit")) {
                if (rank.getWeight() > main.getConfig().getInt("rank-limit")) {
                    player.sendMessage(ChatColor.RED + "Coming soon!");
                    return;
                }
            }

            if(main.getRankManager().getPlayerRank(player).getWeight() < rank.getWeight()) {
                player.sendMessage(ChatColor.RED + "You cannot teleport to this rank.");
                return;
            } else {
                if(main.getWarpManager().isWarp(rank.getName())) {
                    Warp warp = main.getWarpManager().getWarp(rank.getName());
                    Util.updatedTeleport(player, warp.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Teleporting to rank " + rank.getName() + "!");
                    return;
                }
            }
            player.sendMessage(ChatColor.RED + "This rank is currently unavailable.");
            return;
        }

        if(e.getInventory().getName().equals(player.getName() + "'s Saves")) {
            if(e.getCurrentItem().getType().equals(Material.BOOK)) {
                for(Save s : main.getSaveManager().getPlayerSaves(player)) {
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equals(s.getName())) {
                        if(main.getPractice().isPracticing(player)) {
                            player.closeInventory();

                            player.sendMessage(ChatColor.RED + "You cannot teleport to a save while you're in practice mode!");
                            break;
                        }

                        player.closeInventory();

                        player.sendMessage(ChatColor.GREEN + "Teleporting to save: " + s.getName());
                        Util.updatedTeleport(player, s.getLocation());
                        main.getSaveManager().removeSaveInstance(player, s);
                        break;
                    }
                }
            }
            e.setCancelled(true);
            return;
        }

        if(e.getCurrentItem().equals(main.getPractice().getPracticeItem()) &&
                main.getPractice().isPracticing(player)) {
            e.setCancelled(true);
            return;
        }

        if(e.getCurrentItem().equals(main.getVisibilityUtil().getVisibilityItem(player))) {
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

        if(e.getItemDrop().getItemStack().equals(main.getPractice().getPracticeItem()) &&
                main.getPractice().isPracticing(player)) {
            e.setCancelled(true);
            return;
        }

        if(e.getItemDrop().getItemStack().equals(main.getVisibilityUtil().getVisibilityItem(player))) {
            e.setCancelled(true);
            return;
        }

        if(main.getMenuManager().isMenuItem(e.getItemDrop().getItemStack())) {
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
