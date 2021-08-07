package ru.alskar.signitdemo;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignItDemo extends JavaPlugin implements CommandExecutor {

    private final NamespacedKey keyUUID = new NamespacedKey("signit", "author-uuid");
    private final NamespacedKey keyName = new NamespacedKey("signit", "author-name");
    private final NamespacedKey keyLore = new NamespacedKey("signit", "lore-text");

    public void onEnable() {
        this.getCommand("signdemo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        ItemStack map_by_alskar = signMap(UUID.fromString("324329ca-4af3-41d9-9eac-b0eb9f99df68"), "AlskarGLHF");
        ItemStack map_by_test = signMap(UUID.fromString("0da8fc8a-b11e-4ab4-8120-53b5c18a63a1"), "Test");
        ItemStack fresh_map = new ItemStack(Material.FILLED_MAP);
        ItemMeta fresh_map_meta = fresh_map.getItemMeta();
        if (fresh_map_meta != null) {
            fresh_map_meta.setDisplayName("Brand New Map");
            fresh_map.setItemMeta(fresh_map_meta);
        }
        ItemStack clean_maps = new ItemStack(Material.MAP, 8);
        ((Player) sender).getInventory().addItem(map_by_alskar, map_by_test, fresh_map, clean_maps);
        return true;
    }

    private ItemStack signMap(UUID uuid, String authorName) {
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        ItemMeta mapMeta = map.getItemMeta();
        PersistentDataContainer container;
        if (mapMeta == null)
            return new ItemStack(Material.AIR);
        container = mapMeta.getPersistentDataContainer();
        container.set(this.keyUUID, new PersistentUUID(), uuid);
        container.set(this.keyName, PersistentDataType.STRING, authorName);
        String loreText = MessageFormat.format("§7Signed by §6{0}", authorName);
        List<String> lore = ((lore = mapMeta.getLore()) != null) ? lore : new ArrayList<>();
        lore.add(loreText);
        mapMeta.setLore(lore);
        container.set(this.keyLore, PersistentDataType.STRING, ChatColor.stripColor(loreText));
        mapMeta.setDisplayName("Map by " + authorName);
        map.setItemMeta(mapMeta);
        return map;
    }
}
