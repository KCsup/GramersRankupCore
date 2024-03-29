package org.kcsup.gramersrankupcore.visibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;

import java.util.ArrayList;
import java.util.List;

public class VisibilityManager extends Manager {
    private final List<Player> invisible = new ArrayList<>();
    private final List<Player> onCooldown = new ArrayList<>();
    private final int visibilityItemSlot;

    public VisibilityManager(Main main) {
        super(main, null, null);

        visibilityItemSlot = main.getConfig().getInt("visibility-item-slot");
    }

    @Override
    public void shutdown() {
        purgeInvisible();
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> onCooldown.remove(player), 3 * 10);
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
