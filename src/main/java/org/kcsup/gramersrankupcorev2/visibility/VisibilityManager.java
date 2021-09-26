package org.kcsup.gramersrankupcorev2.visibility;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcorev2.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisibilityManager {
    private Main main;
    private List<Player> invisible = new ArrayList<>();
    private List<Player> onCooldown = new ArrayList<>();
    private int visibilityItemSlot;

    public VisibilityManager(Main main) {
        this.main = main;
        visibilityItemSlot = main.getConfig().getInt("visibility-item-slot");
    }

    public ItemStack getVisibilityItem(Player player) {
        String itemName;
        if(isInvisible(player)) {
            itemName = main.getConfig().getString("visibility-off-item-name");
        } else {
            itemName = main.getConfig().getString("visibility-on-item-name");
        }
        if(itemName == null) return null;

        Material material = Material.matchMaterial(itemName);
        if(material == null) return null;

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Visibility");
        item.setItemMeta(itemMeta);

        return item;
    }

    public void toggleVisibility(Player player) {
        if(onCooldown.contains(player)) {
            player.sendMessage(ChatColor.RED + "You must wait 3 seconds before toggling player visibility.");
            return;
        }

        if(isInvisible(player)) {
            setVisible(player);
        } else {
            setInvisible(player);
        }
        onCooldown.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            onCooldown.remove(player);
        }, 3 * 10);
    }

    private void setInvisible(Player player) {
        if(isInvisible(player)) return;

        invisible.add(player);
        for(Player online : Bukkit.getOnlinePlayers()) {
            if(player.canSee(online)) player.hidePlayer(online);
        }
        player.sendMessage(ChatColor.GREEN + "Players Hidden");
        player.getInventory().setItem(visibilityItemSlot, getVisibilityItem(player));
    }

    private void setVisible(Player player) {
        if(!isInvisible(player)) return;

        invisible.remove(player);
        for(Player online: Bukkit.getOnlinePlayers()) {
            if(!player.canSee(online)) player.showPlayer(online);
        }
        player.sendMessage(ChatColor.GREEN + "Payers Shown");
        player.getInventory().setItem(visibilityItemSlot, getVisibilityItem(player));
    }

    public void updateInvisible() {
        if(invisible.isEmpty()) return;

        for(Player player : invisible) {
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(player.canSee(online)) player.hidePlayer(online);
            }
        }
    }

    public void purgeInvisible() {
        if(invisible.isEmpty()) return;

        for(Player player : invisible) {
            setVisible(player);
            player.sendMessage(ChatColor.RED + "You were forced into toggled visibility due to a server reload.");
        }
    }

    public boolean isInvisible(Player player) {
        return invisible.contains(player);
    }

    public void visibilityItemCheck(Player player) {
        ItemStack itemStack = getVisibilityItem(player);
        if(player.getInventory().getItem(visibilityItemSlot) == null ||
                !player.getInventory().getItem(visibilityItemSlot).equals(itemStack))
            player.getInventory().setItem(visibilityItemSlot, itemStack);
    }
}
