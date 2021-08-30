package org.kcsup.gramersrankupcorev2.menu;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.ranks.Rank;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {
    private Main main;
    private File menuData;

    public MenuManager(Main main) {
        this.main = main;
        filesCheck();
    }

    private void filesCheck() {
        String menuDataPath = main.getDataFolder() + "/menuData.json";
        menuData = new File(menuDataPath);
        if(!menuData.exists()) {
            try {
                menuData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("menus", new JSONArray());

                FileWriter fileWriter = new FileWriter(menuDataPath);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playerMenuCheck(Player player) {
        Inventory pInventory = player.getInventory();
        Menu mainMenu = getMenu("Main Menu");
        if(mainMenu == null) return;

        if(pInventory.getItem(8) == null || !pInventory.getItem(8).equals(mainMenu.getItem()))
        player.getInventory().setItem(8, mainMenu.getItem());
    }

    public Menu getMenu(String name) {
        if(getCurrentMenus() == null) return null;

        for(Menu menu : getCurrentMenus()) {
            if(menu.getName().equals(name) || menu.getTranslatedName().equals(name)) return menu;
        }

        return null;
    }

    public Menu getMenu(ItemStack itemStack) {
        if(getCurrentMenus() == null) return null;

        for (Menu menu : getCurrentMenus()) {
            if(menu.getItem().equals(itemStack)) return menu;
        }

        return null;
    }

    public List<Menu> getCurrentMenus() {
        List<Menu> menus = new ArrayList<>();

        if(menuData == null) return null;

        try {
            FileReader fileReader = new FileReader(menuData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray jsonMenus = file.getJSONArray("menus");

            for(Object o : jsonMenus) {
                JSONObject jsonMenu = (JSONObject) o;
                Menu menu = jsonToMenu(jsonMenu);

                if(menu != null) menus.add(menu);
            }

            if(!menus.isEmpty()) return menus;
            else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void storeMenuInstance(Menu menu) {
        if(menuData == null || menu == null) return;

        try {
            FileReader fileReader = new FileReader(menuData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray menus = file.getJSONArray("menus");

            JSONObject jsonMenu = menuToJson(menu);
            menus.put(jsonMenu);

            FileWriter fileWriter = new FileWriter(menuData);
            fileWriter.write(file.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMenuInventory(Inventory inventory) {
        if(getCurrentMenus() == null) return false;

        for(Menu menu : getCurrentMenus()) {
            if(menu.getInventory().getTitle().equals(inventory.getTitle())) return true;
        }

        return false;
    }

    public boolean isMenuItem(ItemStack itemStack) {
        if(getCurrentMenus() == null) return false;

        for(Menu menu : getCurrentMenus()) {
            if(menu.getItem().equals(itemStack)) return true;
        }

        return false;
    }

    private Pair<ItemStack, Integer> jsonToInventoryItem(JSONObject jsonObject) {
        String matName = jsonObject.getString("item");
        String itemName = jsonObject.getString("name");
        Material mat = Material.matchMaterial(matName);
        ItemStack item;
        if(mat != null) {
            item = new ItemStack(mat, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
            item.setItemMeta(meta);

            int slot = jsonObject.getInt("slot");
            return new Pair<>(item, slot);
        }
        return null;
    }

    private Menu jsonToMenu(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        String name = ChatColor.translateAlternateColorCodes('&', jsonObject.getString("name"));
        int size = jsonObject.getInt("size");

        JSONArray jsonInventory = jsonObject.getJSONArray("inventory");
        Inventory inventory = Bukkit.createInventory(null, size, name);
        for(Object o : jsonInventory) {
            JSONObject jsonItem = (JSONObject) o;
            Pair<ItemStack, Integer> inventoryItem = jsonToInventoryItem(jsonItem);
            if(inventoryItem != null)
            inventory.setItem(inventoryItem.getValue(), inventoryItem.getKey());
        }

        JSONObject jsonItem = jsonObject.getJSONObject("item");
        String matName = jsonItem.getString("item");
        String itemName = jsonItem.getString("name");
        Material mat = Material.matchMaterial(matName);
        ItemStack item;
        if(mat != null) {
            item = new ItemStack(mat, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
            item.setItemMeta(meta);
        }
        else item = null;

        return new Menu(name, size, inventory, item);
    }

    private JSONObject menuToJson(Menu menu) {
        if(menu == null) return null;

        JSONObject menuJson = new JSONObject();
        menuJson.put("name", menu.getName());
        menuJson.put("size", menu.getSize());

        JSONArray inventory = new JSONArray();
        for(int i = 0; i < menu.getSize(); i++) {
            ItemStack item = menu.getInventory().getItem(i);
            if(item == null) continue;

            JSONObject jsonItem = new JSONObject();
            jsonItem.put("item", item.getType().name());
            jsonItem.put("name", item.getItemMeta().getDisplayName());
            jsonItem.put("slot", i);

            inventory.put(jsonItem);
        }

//        for(ItemStack item : menu.getInventory().getContents()) {
//            if(item == null) continue;
//
//            JSONObject jsonItem = new JSONObject();
//            jsonItem.put("item", item.getType().name());
//            jsonItem.put("name", item.getItemMeta().getDisplayName());
//
//            inventory.put(jsonItem);
//        }

        menuJson.put("inventory", inventory);

        JSONObject item = new JSONObject();
        item.put("item", menu.getItem().getType().name());
        item.put("name", menu.getItem().getItemMeta().getDisplayName());
        menuJson.put("item", item);

        return menuJson;
    }

    public Inventory getRankMenu() {
        List<Rank> ranks = main.getRankManager().getCurrentRanksSortedReversed();
        if(ranks == null) return null;

        Inventory inventory = Bukkit.createInventory(null, 36, "Ranks");
        for(int i = 0; i < ranks.size(); i++) {
            Rank rank = ranks.get(i);

            ItemStack rankItem = new ItemStack(Material.NAME_TAG);
            ItemMeta itemMeta = rankItem.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rank.getChatPrefix()));
            List<String> lore = new ArrayList<>();
            lore.add(rank.getName());
            itemMeta.setLore(lore);
            rankItem.setItemMeta(itemMeta);

            int slot = i + 10;
            while(slot % 9 == 0 || slot % 9 == 8 || inventory.getItem(slot) != null) {
                slot++;
            }
            if(slot > 25) continue;
            // TODO: pages system for when we have more than 14 ranks

            inventory.setItem(slot, rankItem);
        }

        return inventory;
    }
}
