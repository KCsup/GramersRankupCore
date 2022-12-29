package org.kcsup.gramersrankupcore.menu;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu {
    private final String name;
    private final int size;
    private final Inventory inventory;
    private final ItemStack item;

    public Menu(String name, int size, Inventory inventory, ItemStack item) {
        this.name = name;
        this.size = size;
        this.inventory = inventory;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getTranslatedName() {
        return ChatColor.translateAlternateColorCodes('&', getName());
    }

    public int getSize() {
        return size;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getItem() {
        return item;
    }
}
