package org.kcsup.gramersrankupcore.practice;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.util.Manager;
import org.kcsup.gramersrankupcore.util.Util;

import java.util.HashMap;

public class PracticeManager extends Manager {
    private final HashMap<Player, Location> practicing = new HashMap<>();
    private final int practiceItemSlot;

    public PracticeManager(Main main) {
        super(main, null, null);
        practiceItemSlot = main.getConfig().getInt("practice-item-slot");
    }

    @Override
    public void shutdown() {
        setAllNotPracticing();
    }

    public int getPracticeItemSlot() {
        return practiceItemSlot;
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
            if(getPracticeItem() != null) player.getInventory().setItem(getPracticeItemSlot(), getPracticeItem());
        }
    }

    public void setNotPracticing(Player player) {
        if(isPracticing(player)) {
            Location location = getPlayerPracticeLocation(player);
            if(location != null) Util.updatedTeleport(player, location);
            practicing.remove(player);
            if(player.getInventory().getItem(getPracticeItemSlot()) != null) player.getInventory().setItem(getPracticeItemSlot(), null);
        }
    }

    public void setAllNotPracticing() {
        for(Player player : practicing.keySet()) {
            setNotPracticing(player);
            player.sendMessage(ChatColor.RED + "You were forced out of practice mode due to a reload.");
        }
    }
}
