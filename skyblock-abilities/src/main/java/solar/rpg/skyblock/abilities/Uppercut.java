package solar.rpg.skyblock.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Title;

/**
 * Uppercut allows a player to launch an enemy upwards.
 * This is done by jumping and striking an enemy.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Uppercut extends Ability {

    public String getName() {
        return ChatColor.GREEN + "Uppercut";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.GREEN + "Combat advantage!",
                "Jump up and strike an",
                "enemy to launch them high.",
                "",
                "\"If your strike didn't",
                "kill, the fall will!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LEAD, 1);
    }

    @EventHandler
    public void onUppercut(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return; // Ignore cancelled.
        if (!(event.getDamager() instanceof Player)) return; // Ignore non-player damagers.
        if (!check(event.getEntity().getWorld())) return;
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            // If we're dealing with a NON-PLAYER entity that IS alive...
            Island is = main().islands().getIsland(event.getDamager().getUniqueId()); // Get the damager's island.
            if (is != null) { // If they have an island..
                if (eligible(is)) { // And it is eligible..
                    // Perform uppercut if player is moving up!
                    if (event.getDamager().getVelocity().getY() > 0) {
                        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.25F, 1.25F);
                        Title.showTitle((Player) event.getDamager(), "", ChatColor.GREEN + "** UPPERCUT **", 9, 5, 9);
                        Bukkit.getScheduler().runTask(main().plugin(), () -> event.getEntity().setVelocity(new Vector(event.getEntity().getVelocity().getX(), event.getEntity().getVelocity().getY() * 2.5 + 1, event.getEntity().getVelocity().getZ())));
                    }
                }
            }
        }
    }
}
