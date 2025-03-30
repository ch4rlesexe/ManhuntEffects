package org.ch4rlesexe.manhuntEffects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ManhuntEffects extends JavaPlugin {

    // tick duration
    public static final int LONG_DURATION = 100000000;

    private File playerDataFile;
    private FileConfiguration playerDataConfig;
    // using player.getPlayerProfile().getUniqueId() as the key
    private final Map<UUID, String> guiForDimension = new HashMap<>();

    @Override
    public void onEnable() {
        loadPlayerData();
        getServer().getPluginManager().registerEvents(new ManhuntEffectsListener(this), this);

        PluginCommand resetCmd = this.getCommand("resetabilities");
        if (resetCmd != null) {
            resetCmd.setExecutor(new ResetAbilitiesCommand(this));
        }
        getLogger().info("ManhuntEffects enabled!");
    }

    @Override
    public void onDisable() {
        savePlayerData();
    }

    // --- PLAYER DATA MANAGEMENT --------------------------------------------

    public void loadPlayerData() {
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData() {
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayerDataConfig() {
        return playerDataConfig;
    }

    // ---- PLAYER ABILITY -------------------------------------------

    public String getAbility(UUID id, String dimension) {
        return playerDataConfig.getString("players." + id.toString() + "." + dimension);
    }

    public void setAbility(UUID id, String dimension, String ability) {
        playerDataConfig.set("players." + id.toString() + "." + dimension, ability);
        savePlayerData();
    }

    public void resetAbilities(UUID id) {
        playerDataConfig.set("players." + id.toString(), null);
        savePlayerData();
    }

    // ---- GUI ------------------------------------------

    public void openAbilitySelectionGUI(Player player, String dimension) {
        Inventory gui = Bukkit.createInventory(null, 9, "Choose " + dimension + " ability");

        if (dimension.equalsIgnoreCase("overworld")) {
            // Overworld abilities
            ItemStack jumpBoostItem = new ItemStack(Material.FEATHER);
            ItemMeta meta = jumpBoostItem.getItemMeta();
            meta.setDisplayName("Jump Boost");
            meta.setLore(Arrays.asList("Grants a permanent Jump Boost effect."));
            jumpBoostItem.setItemMeta(meta);
            gui.setItem(2, jumpBoostItem);

            ItemStack haste1Item = new ItemStack(Material.DIAMOND_PICKAXE);
            meta = haste1Item.getItemMeta();
            meta.setDisplayName("Haste Upgrade");
            meta.setLore(Arrays.asList("Upgrades your haste effect by one level."));
            haste1Item.setItemMeta(meta);
            gui.setItem(4, haste1Item);

            ItemStack unlimitedStaminaItem = new ItemStack(Material.COOKED_BEEF);
            meta = unlimitedStaminaItem.getItemMeta();
            meta.setDisplayName("Unlimited Stamina");
            meta.setLore(Arrays.asList("Keeps your food bar full permanently."));
            unlimitedStaminaItem.setItemMeta(meta);
            gui.setItem(6, unlimitedStaminaItem);
        } else if (dimension.equalsIgnoreCase("nether")) {
            // Nether abilities
            ItemStack fireResItem = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta meta = fireResItem.getItemMeta();
            meta.setDisplayName("Fire Resistance");
            meta.setLore(Arrays.asList("Grants permanent Fire Resistance."));
            fireResItem.setItemMeta(meta);
            gui.setItem(2, fireResItem);

            ItemStack haste2Item = new ItemStack(Material.DIAMOND);
            meta = haste2Item.getItemMeta();
            meta.setDisplayName("Haste Upgrade");
            meta.setLore(Arrays.asList("Upgrades your haste effect by one level."));
            haste2Item.setItemMeta(meta);
            gui.setItem(4, haste2Item);

            ItemStack healthIncreaseItem = new ItemStack(Material.GOLDEN_APPLE);
            meta = healthIncreaseItem.getItemMeta();
            meta.setDisplayName("Health Increase (+5 Hearts)");
            meta.setLore(Arrays.asList("Increases your max health to 30 hearts."));
            healthIncreaseItem.setItemMeta(meta);
            gui.setItem(6, healthIncreaseItem);
        } else if (dimension.equalsIgnoreCase("end")) {
            // End abilities
            ItemStack speedItem = new ItemStack(Material.RABBIT_FOOT);
            ItemMeta meta = speedItem.getItemMeta();
            meta.setDisplayName("Speed I");
            meta.setLore(Arrays.asList("Grants a permanent Speed I effect."));
            speedItem.setItemMeta(meta);
            gui.setItem(2, speedItem);

            ItemStack resistanceItem = new ItemStack(Material.IRON_BLOCK);
            meta = resistanceItem.getItemMeta();
            meta.setDisplayName("Resistance I");
            meta.setLore(Arrays.asList("Grants a permanent Resistance I effect."));
            resistanceItem.setItemMeta(meta);
            gui.setItem(4, resistanceItem);

            ItemStack featherFallingItem = new ItemStack(Material.FEATHER);
            meta = featherFallingItem.getItemMeta();
            meta.setDisplayName("Feather Falling II");
            meta.setLore(Arrays.asList("Cancels all fall damage permanently."));
            featherFallingItem.setItemMeta(meta);
            gui.setItem(6, featherFallingItem);
        }

        UUID id = player.getPlayerProfile().getUniqueId();
        guiForDimension.put(id, dimension.toLowerCase());
        player.openInventory(gui);
    }

    // ---- APPLYING ABILITY EFFECTS ----------------------------------

    public void applyPlayerAbilities(Player player) {
        UUID id = player.getPlayerProfile().getUniqueId();

        // Overworld abilities
        String overworldAbility = getAbility(id, "overworld");
        if (overworldAbility != null) {
            if (overworldAbility.equalsIgnoreCase("jump_boost")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, LONG_DURATION, 1, false, false, false));
            }
        }

        // Nether abilities
        String netherAbility = getAbility(id, "nether");
        if (netherAbility != null) {
            if (netherAbility.equalsIgnoreCase("fire_resistance")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, LONG_DURATION, 0, false, false, false));
            } else if (netherAbility.equalsIgnoreCase("health_increase")) {
                // For health increase, set MAX_HEALTH
                // default max health is 20
                AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
                if (healthAttr != null && healthAttr.getBaseValue() < 30.0) {
                    healthAttr.setBaseValue(30.0);
                    if (player.getHealth() > healthAttr.getValue()) {
                        player.setHealth(healthAttr.getValue());
                    }
                }
            }
        }

        // End abilities
        String endAbility = getAbility(id, "end");
        if (endAbility != null) {
            if (endAbility.equalsIgnoreCase("speed1")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, LONG_DURATION, 0, false, false, false));
            } else if (endAbility.equalsIgnoreCase("resistance1")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, LONG_DURATION, 0, false, false, false));
            }
        }

        // Combine haste effects
        int hasteCount = 0;
        if (overworldAbility != null && overworldAbility.equalsIgnoreCase("haste1")) {
            hasteCount++;
        }
        if (netherAbility != null && netherAbility.equalsIgnoreCase("haste2")) {
            hasteCount++;
        }
        if (hasteCount > 0) {
            // The applied amplifier is hasteCount - 1 (Haste 1 for 1, Haste II for 2)
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, LONG_DURATION, hasteCount - 1, false, false, false));
        }
    }

    // Remove the health increase when resetting
    public void removeHealthIncrease(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null && healthAttr.getBaseValue() > 20.0) {
            healthAttr.setBaseValue(20.0);
        }
    }

    public Map<UUID, String> getGuiForDimensionMap() {
        return guiForDimension;
    }

    public void removeGuiEntry(UUID id) {
        guiForDimension.remove(id);
    }
}
