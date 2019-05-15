package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.stored.Settings;

/**
 * Visioned stops the blinding effect from affecting
 * the player entering the End. The other effects will
 * still apply, however.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Visioned extends Ability {

    public String getName() {
        return ChatColor.GREEN + "" + ChatColor.ITALIC + "Visioned";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.BLUE + "Active ability!",
                "You no longer suffer from the ill",
                "effect of blindness by entering the End.",
                "\"Starving included\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_PORTAL_FRAME, 1);
    }

    @EventHandler
    public void onSwap(PlayerChangedWorldEvent event) {
        // Remove potion effects when leaving the End.
        if (event.getFrom().getName().equals(Settings.ADMIN_WORLD_ID + "_end")) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.HUNGER)) {
                event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
                event.getPlayer().sendMessage(ChatColor.RED + "You no longer feel starved beyond comprehension..");
            }
            if (event.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS))
                event.getPlayer().removePotionEffect(PotionEffectType.WEAKNESS);
            if (event.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
                event.getPlayer().sendMessage(ChatColor.RED + "You regain the eyesight you lost upon entering..");
            }
        }

        // Apply relevant potion effects when entering the End.
        if (event.getPlayer().getWorld().getName().equals(Settings.ADMIN_WORLD_ID + "_end") && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
            event.getPlayer().sendMessage(ChatColor.RED + "You suddenly feel starved beyond comprehension..");
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 999999 * 20, 3));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999 * 20, 1));

            Island found = main().islands().getIsland(event.getPlayer().getUniqueId());
            if (found == null || !eligible(found)) {
                event.getPlayer().sendMessage(ChatColor.RED + "Your eyes are strained by the thick haze..");
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999 * 20, 0));
            }
        }
    }
}
