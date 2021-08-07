package ru.alskar.signitdemo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignItDemo extends JavaPlugin implements CommandExecutor {

    private final NamespacedKey keyUUID = new NamespacedKey("signit", "author-uuid");
    private final NamespacedKey keyName = new NamespacedKey("signit", "author-name");
    private final NamespacedKey keyLore = new NamespacedKey("signit", "lore-text");

    private BufferedImage catImage;

    public void onEnable() {
        this.getCommand("signdemo").setExecutor(this);
        try {
            this.catImage = ImageIO.read(new URL("https://raw.githubusercontent.com/CatAndPaste/SignItDemo/master/src/main/resources/cat128x128.png"));
            this.getServer().getLogger().info("Cat image loaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            this.catImage = null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        ItemStack mapByAlskar = signMap(UUID.fromString("324329ca-4af3-41d9-9eac-b0eb9f99df68"), "AlskarGLHF");
        ItemStack mapByTest = signMap(UUID.fromString("0da8fc8a-b11e-4ab4-8120-53b5c18a63a1"), "Test");
        ItemStack freshMap = new ItemStack(Material.FILLED_MAP);
        ItemMeta freshMapMeta = freshMap.getItemMeta();
        if (freshMapMeta != null) {
            freshMapMeta.setDisplayName("Brand New Map");
            if (catImage != null && freshMapMeta instanceof MapMeta) {
                this.getServer().getLogger().info("YO!");
                MapView mapView;
                if (Bukkit.getServer().getMap(0) != null)
                    mapView = Bukkit.getServer().getMap(0);
                else
                    mapView = Bukkit.createMap(Bukkit.getServer().getWorlds().get(0));
                mapView.getRenderers().clear();
                mapView.setScale(MapView.Scale.CLOSEST);
                mapView.addRenderer(new CatRenderer());
                ((MapMeta) freshMapMeta).setMapId(mapView.getId());
            }
            freshMap.setItemMeta(freshMapMeta);
        }
        ItemStack clean_maps = new ItemStack(Material.MAP, 8);
        ((Player) sender).getInventory().addItem(mapByAlskar, mapByTest, freshMap, clean_maps);
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
        if (catImage != null && mapMeta instanceof MapMeta) {
            MapView mapView;
            if (Bukkit.getServer().getMap(0) != null)
                mapView = Bukkit.getServer().getMap(0);
            else
                mapView = Bukkit.createMap(Bukkit.getServer().getWorlds().get(0));
            mapView.getRenderers().clear();
            mapView.setScale(MapView.Scale.CLOSEST);
            mapView.addRenderer(new CatRenderer());
            ((MapMeta) mapMeta).setMapId(mapView.getId());
        }
        map.setItemMeta(mapMeta);
        return map;
    }

    private class CatRenderer extends MapRenderer {
        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            if (catImage != null)
                mapCanvas.drawImage(0, 0, catImage);
        }
    }
}
