package org.kcsup.gramersrankupcore.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kcsup.gramersrankupcore.Main;
import org.kcsup.gramersrankupcore.menu.Menu;

public class MenuCommand implements CommandExecutor {
    private final Main main;

    public MenuCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;
        if(!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Invalid usage!");
            return false;
        }

        int size = Integer.parseInt(args[0]);

        StringBuilder fullArgsBuilder = new StringBuilder();
        for (String arg : args) {
            fullArgsBuilder.append(arg).append(" ");
        }
        String fullArgs = fullArgsBuilder.toString();

        String name = fullArgs.substring(fullArgs.indexOf("[") + 1, fullArgs.indexOf("]"));

        String itemArgs = fullArgs.substring(fullArgs.indexOf("{") + 1, fullArgs.indexOf("}"));
        String[] iArgs = itemArgs.split(",");
        Material mat = Material.matchMaterial(iArgs[0]);
        if(mat == null) {
            player.sendMessage(ChatColor.RED + "Invalid Material!");
            return false;
        }
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(iArgs[1]);
        item.setItemMeta(meta);

        Menu menu = new Menu(name, size, Bukkit.createInventory(null, size, name), item);
        main.getMenuManager().storeMenuInstance(menu);

        return false;
    }
}
