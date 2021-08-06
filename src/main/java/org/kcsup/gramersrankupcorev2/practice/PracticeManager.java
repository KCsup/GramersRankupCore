package org.kcsup.gramersrankupcorev2.practice;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcorev2.Main;

import java.util.HashMap;

public class PracticeManager {
    private Main main;
    private HashMap<Player, Location> practicing = new HashMap<>();

    public PracticeManager(Main main) {
        this.main = main;
    }

    public ItemStack getPracticeItem() {
        String itemName = main.getConfig().getString("practice-item-name");
        if(itemName == null) return null;

        Material material = Material.matchMaterial(itemName);
        if(material == null) return null;

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Reset");
        item.setItemMeta(itemMeta);

        return item;
    }

    public Location getPlayerPracticeLocation(Player player) {
        if(isPracticing(player)) return practicing.get(player);
        else return null;
    }

    public boolean isPracticing(Player player) {
        return practicing.containsKey(player);
    }

    public void setPracticing(Player player) {
        if(!isPracticing(player)) {
            practicing.put(player, player.getLocation());
            if(getPracticeItem() != null) player.getInventory().setItem(8, getPracticeItem());
        }
    }

    public void setNotPracticing(Player player) {
        if(isPracticing(player)) {
            Location location = getPlayerPracticeLocation(player);
            if(location != null) player.teleport(location);
            practicing.remove(player);
            if(player.getInventory().getItem(8) != null) player.getInventory().setItem(8, null);
        }
    }

    public void setAllNotPracticing() {
        for(Player player : practicing.keySet()) {
            setNotPracticing(player);
            player.sendMessage(ChatColor.RED + "You were forced out of practice mode due to a reload.");
        }
    }
}
