package org.ch4rlesexe.manhuntEffects;

// IMPORT BUKKIT!!!!
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Map;
import java.util.UUID;

public class ManhuntEffectsListener implements Listener {
    private final ManhuntEffects plugin;

    public ManhuntEffectsListener(ManhuntEffects plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.applyPlayerAbilities(player);
            checkAndPrompt(player);
        }, 20L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.applyPlayerAbilities(player);
        checkAndPrompt(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.applyPlayerAbilities(player);
            checkAndPrompt(player);
        }, 20L);
    }

    // can't close gui til you choose something
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        if (title.startsWith("Choose ")) {
            // if they haven't chosen an ability for that dimension, open gui again and again
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                UUID id = player.getPlayerProfile().getUniqueId();
                if (plugin.getGuiForDimensionMap().containsKey(id)) {
                    String dimension = plugin.getGuiForDimensionMap().get(id);
                    plugin.openAbilitySelectionGUI(player, dimension);
                }
            }, 1L);
        }
    }

    // gui click
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        String title = event.getView().getTitle();
        if (title.startsWith("Choose ")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot < 0 || slot > 8) return;
            Map<UUID, String> guiMap = plugin.getGuiForDimensionMap();
            UUID id = player.getPlayerProfile().getUniqueId();
            if (!guiMap.containsKey(id)) return;
            String dimension = guiMap.get(id);
            String chosenAbility = null;

            if (dimension.equalsIgnoreCase("overworld")) {
                if (slot == 2) chosenAbility = "jump_boost";
                else if (slot == 4) chosenAbility = "haste1";
                else if (slot == 6) chosenAbility = "unlimited_stamina";
            } else if (dimension.equalsIgnoreCase("nether")) {
                if (slot == 2) chosenAbility = "fire_resistance";
                else if (slot == 4) chosenAbility = "haste2";
                else if (slot == 6) chosenAbility = "health_increase";
            } else if (dimension.equalsIgnoreCase("end")) {
                if (slot == 2) chosenAbility = "speed1";
                else if (slot == 4) chosenAbility = "resistance1";
                else if (slot == 6) chosenAbility = "nofall";
            }

            if (chosenAbility != null) {
                plugin.setAbility(id, dimension, chosenAbility);
                player.sendMessage(ChatColor.GREEN + "You have selected: " + chosenAbility.replace("_", " "));
                plugin.applyPlayerAbilities(player);
                plugin.removeGuiEntry(id);
                player.closeInventory();
            }
        }
    }

    // keep the people with unlimited stamina fed
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        String overworldAbility = plugin.getAbility(player.getPlayerProfile().getUniqueId(), "overworld");
        if (overworldAbility != null && overworldAbility.equalsIgnoreCase("unlimited_stamina")) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    // NO FALL DAMAGE!!!!
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != DamageCause.FALL) return;

        Player player = (Player) event.getEntity();
        String endAbility = plugin.getAbility(player.getPlayerProfile().getUniqueId(), "end");
        if (endAbility != null && endAbility.equalsIgnoreCase("nofall")) {
            event.setCancelled(true);
        }
    }

    // see if the player has selected an ability for that dimension. If they haven't open the gui
    private void checkAndPrompt(Player player) {
        String dimension = null;
        World.Environment env = player.getWorld().getEnvironment();
        switch (env) {
            case NORMAL:
                dimension = "overworld";
                break;
            case NETHER:
                dimension = "nether";
                break;
            case THE_END:
                dimension = "end";
                break;
            default:
                break;
        }
        UUID id = player.getPlayerProfile().getUniqueId();
        if (dimension != null && plugin.getAbility(id, dimension) == null) {
            plugin.openAbilitySelectionGUI(player, dimension);
        }
    }
}
