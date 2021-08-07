package org.kcsup.gramersrankupcorev2.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcorev2.Main;
import org.kcsup.gramersrankupcorev2.saves.Save;

import java.util.List;

public class SavesCommand implements CommandExecutor {
    private Main main;

    public SavesCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;
        List<Save> saves = main.getSaveManager().getPlayerSaves(player);

        Inventory savesGui = Bukkit.createInventory(null, 27, player.getName() + "'s Saves");

        for(int i = 0; i < 3; i++) {
            try {
                Save save = saves.get(i);

                ItemStack saveItem = new ItemStack(Material.BOOK, 1);
                ItemMeta saveMeta = saveItem.getItemMeta();
                saveMeta.setDisplayName(save.getName());
                saveItem.setItemMeta(saveMeta);

                savesGui.setItem(12 + i, saveItem);
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                ItemStack noSaveItem = new ItemStack(Material.BARRIER, 1);
                ItemMeta noSaveMeta = noSaveItem.getItemMeta();
                noSaveMeta.setDisplayName(ChatColor.RED + "No Save");
                noSaveItem.setItemMeta(noSaveMeta);

                savesGui.setItem(12 + i, noSaveItem);
            }
        }

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for(int i = 0; i < savesGui.getSize(); i++) {
            if(i < 12 || i > 14) {
                savesGui.setItem(i, filler);
            }
        }

        player.openInventory(savesGui);

        return false;
    }
}
