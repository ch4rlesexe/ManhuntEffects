package org.ch4rlesexe.manhuntEffects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class ResetAbilitiesCommand implements CommandExecutor {
    private final ManhuntEffects plugin;

    public ResetAbilitiesCommand(ManhuntEffects plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console must specify a player to reset.");
                return true;
            }
            target = (Player) sender;
        } else {
            if (!sender.hasPermission("manhunteffects.reset.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reset other players' abilities.");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
        }

        UUID id = target.getPlayerProfile().getUniqueId();
        plugin.resetAbilities(id);
        plugin.removeHealthIncrease(target); // resets MAX_HEALTH to 20.0

        // Remove potion effects applied from the ManhuntEffects class
        target.removePotionEffect(PotionEffectType.JUMP_BOOST);
        target.removePotionEffect(PotionEffectType.HASTE);
        target.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        target.removePotionEffect(PotionEffectType.SPEED);
        target.removePotionEffect(PotionEffectType.RESISTANCE);

        target.sendMessage(ChatColor.GREEN + "Your abilities and effects have been reset. Please reselect them.");

        // Re-open the selection GUI for the current dimension
        String dimension = null;
        World.Environment env = target.getWorld().getEnvironment();
        if (env == World.Environment.NORMAL) {
            dimension = "overworld";
        } else if (env == World.Environment.NETHER) {
            dimension = "nether";
        } else if (env == World.Environment.THE_END) {
            dimension = "end";
        }

        if (dimension != null) {
            plugin.openAbilitySelectionGUI(target, dimension);
        }
        return true;
    }
}
