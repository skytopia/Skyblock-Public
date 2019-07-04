package solar.rpg.skyblock.abilities;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.Main;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Title;

import java.util.HashMap;
import java.util.UUID;

/**
 * Huge Damage allows massive damage to be dealt with fists.
 * If enough damage is dealt with fists in a short time,
 * the damage will be upscaled by 50x and the meter resets.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class HugeDamage extends Ability {

    /* Keeps track of rage buildup for players.  */
    private final HashMap<UUID, Integer> combo;

    public HugeDamage() {
        combo = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                // Deplete rage over time.
                if (!combo.isEmpty())
                    for (UUID next : combo.keySet()) {
                        int combos = combo.get(next);
                        if (combos == 1)
                            combo.remove(next);
                        else
                            combo.put(next, combos - 1);
                    }
            }
        }.runTaskTimer(Main.instance, 0L, 20L);
    }

    public String getName() {
        return ChatColor.RED + "" + ChatColor.BOLD + "Huge Fist Damage";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.GREEN + "Combat advantage!",
                "Striking enemies in quick ",
                "succession will build up rage!",
                "",
                "Once enough rage is built up,",
                "fist damage will deal 50x damage.",
                "\"BOOM GOES THE FIST, NO JOKE\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.TNT, 1);
    }

    @EventHandler
    public void onHugeDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return; // Ignore cancelled.
        if (!(event.getDamager() instanceof Player)) return; // Ignore non-player damagers.
        if (!check(event.getEntity().getWorld())) return;
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            // If we're dealing with a NON-PLAYER entity that IS alive...
            Island is = main().islands().getIsland(event.getDamager().getUniqueId()); // Get the damager's island.
            if (is != null) { // If they have an island..
                if (eligible(is)) { // And it is eligible..
                    // Do huge damage procedure!
                    if (((Player) event.getDamager()).getInventory().getItemInMainHand().getType() != Material.AIR)
                        combo.remove(event.getDamager().getUniqueId()); // Remove combo if they didn't use their fists.
                    else {
                        Location eye = ((LivingEntity) event.getEntity()).getEyeLocation();
                        // Check combo.
                        int combos = combo.getOrDefault(event.getDamager().getUniqueId(), 0);
                        eye.getWorld().playSound(eye, Sound.BLOCK_NOTE_BLOCK_BASS, 2F, 1F + (0.25F * (combos - 1)));
                        combo.put(event.getDamager().getUniqueId(), combos + 1); // Increment combo.
                        if (combos >= 3) { // If combo amount is more than 4 hits..
                            // DO WOMBO COMBO DAMAGE.
                            combo.remove(event.getDamager().getUniqueId());
                            eye.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, eye, 1);
                            eye.getWorld().playSound(eye, Sound.ENTITY_GENERIC_EXPLODE, 2F, 1.1F);
                            event.setDamage(event.getDamage() * 50);
                            Title.showTitle((Player) event.getDamager(), "", ChatColor.GREEN + "** HUGE DAMAGE **", 20, 20, 10);
                        }
                    }
                }
            }
        }
    }
}
