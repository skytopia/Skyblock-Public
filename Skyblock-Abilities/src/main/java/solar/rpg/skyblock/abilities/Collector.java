package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;


/**
 * Collector gives all monsters a chance to drop an emerald on death.
 * This effect only happens inside eligible islands' boundaries.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Collector extends Ability {

    public String getName() {
        return ChatColor.GOLD + "Collector";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Monsters will occasionally drop",
                "emeralds upon death on your island. ",
                "\"From shags to riches!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.EMERALD, 1);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) return; // Only execute on monsters and animals.
        if (strictCheck(event.getEntity().getWorld())) return; // Don't execute if not in the islands world.

        // Run the emerald drop code if this is an eligible island.
        if (eligible(main().islands().getIslandAt(event.getEntity().getLocation())))
            if (main().rng().nextInt(50) == 33) {
                event.getEntity().getWorld().spawnParticle(Particle.SMOKE_LARGE, event.getEntity().getLocation(), 5);
                event.getDrops().add(new ItemStack(Material.EMERALD));
            }
    }
}
