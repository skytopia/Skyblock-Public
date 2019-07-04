package solar.rpg.skyblock.abilities;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Title;


/**
 * Creeper Parry allows a player to deflect & amplify a
 * creeper explosion onto nearby enemies.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class CreeperParry extends Ability {

    public String getName() {
        return ChatColor.DARK_GREEN + "Creeper Parry";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.GREEN + "Combat advantage!",
                "Block a creeper's explosion",
                "to deflect the blast.",
                "",
                "All nearby monsters received",
                "amplified explosion damage.",
                "\"How does that even work?\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DROPPER, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreeperParry(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!check(event.getEntity().getWorld())) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && ((Player) event.getEntity()).isBlocking()) {
            Island is = main().islands().getIsland(event.getEntity().getUniqueId()); // Get the damaged's island.
            if (is != null)  // If they have an island...
                if (eligible(is)) { // And it is eligible..
                    Title.showTitle((Player) event.getEntity(), "", ChatColor.GREEN + "** DEFLECT **", 12, 0, 12);
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1F, 1.175F);
                    Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                        for (Entity nearby : event.getEntity().getNearbyEntities(10, 5, 10))
                            if (nearby instanceof Monster) {
                                ((Monster) nearby).damage(event.getDamage() * 2);
                                nearby.getWorld().playSound(nearby.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5F, 0.75F);
                                nearby.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, nearby.getLocation(), 2);
                            }
                    }, 8L);
                }
        }
    }
}
