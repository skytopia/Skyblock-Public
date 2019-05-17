package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Title;


/**
 * Stun allows a player to stun an enemy, preventing movement.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Stun extends Ability {

    public String getName() {
        return ChatColor.RED + "Stun";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.GREEN + "Combat advantage!",
                "Striking an enemy may stun them.",
                "A stunned enemy is unable to move.",
                "\"All they can do.. is.. look..\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BARRIER, 1);
    }

    @EventHandler
    public void onStun(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return; // Ignore cancelled.
        if (!(event.getDamager() instanceof Player)) return; // Ignore non-player damagers.
        if (!check(event.getEntity().getWorld())) return;
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            // If we're dealing with a NON-PLAYER entity that IS alive...
            Island is = main().islands().getIsland(event.getDamager().getUniqueId()); // Get the damager's island.
            if (is != null) { // If they have an island..
                if (eligible(is)) { // And it is eligible..
                    // Do stun procedure!
                    if (main().rng().nextInt(4) == 2) { // If the 25% chance holds true..
                        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PARROT_HURT, 2F, 2F);
                        event.getEntity().getWorld().spawnParticle(Particle.BARRIER, ((LivingEntity) event.getEntity()).getEyeLocation(), 1);
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, main().rng().nextInt(5) * 20 + 5 * 20, 100));
                        Title.showTitle((Player) event.getDamager(), "", ChatColor.GREEN + "** STUN **", 10, 0, 10);
                    }
                }
            }
        }
    }
}
