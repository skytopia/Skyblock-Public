package solar.rpg.skyblock.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.util.Title;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Parry allows a player to knock back an enemy very far.
 * If a player blocks an attack with a shield and quickly strikes
 * back, the knockback factor is increased substantially.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Parry extends Ability {

    /* Holds a list of players who are eligible to parry a mob. */
    private final ArrayList<UUID> parry;

    public Parry() {
        parry = new ArrayList<>();
    }

    public String getName() {
        return ChatColor.GREEN + "Parry";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.GREEN + "Combat advantage!",
                "Block an enemy's attack and",
                "strike back immediately to parry.",
                "",
                "If successful, your attack's",
                "knockback is increased by 5x.",
                "\"Useful on schoolyard bullies\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.TOTEM, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onParrySetup(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!check(event.getEntity().getWorld())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && ((Player) event.getEntity()).isBlocking()) {
            Island is = main().islands().getIsland(event.getEntity().getUniqueId()); // Get the damaged's island.
            if (is != null)  // If they have an island...
                if (eligible(is))  // And it is eligible..
                    // Do parry setup procedure!
                    if (parry.contains(event.getEntity().getUniqueId())) {
                        parry.remove(event.getEntity().getUniqueId());
                        ((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ENTITY_BAT_HURT, 2F, 2F);
                        Title.showTitle((Player) event.getEntity(), "", ChatColor.RED + "** PARRY FAIL **", 5, 0, 5);
                    } else {
                        parry.add(event.getEntity().getUniqueId());
                        Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
                            if (!parry.contains(event.getEntity().getUniqueId())) return;
                            parry.remove(event.getEntity().getUniqueId());
                            ((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ENTITY_BAT_HURT, 0.5F, 2F);
                            Title.showTitle((Player) event.getEntity(), "", ChatColor.RED + "** PARRY FAIL **", 5, 0, 5);
                        }, 15L);
                    }
        }
    }

    @EventHandler
    public void onParry(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return; // Ignore cancelled.
        if (!(event.getDamager() instanceof Player)) return; // Ignore non-player damagers.
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            // If we're dealing with a NON-PLAYER entity that IS alive...
            Island is = main().islands().getIsland(event.getDamager().getUniqueId()); // Get the damager's island.
            if (is != null)  // If they have an island..
                if (eligible(is)) { // And it is eligible..
                    // Do parry procedure!
                    if (parry.contains(event.getDamager().getUniqueId())) {
                        parry.remove(event.getDamager().getUniqueId());
                        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_COW_STEP, 2F, 2F);
                        Title.showTitle((Player) event.getDamager(), "", ChatColor.GREEN + "** PARRY **", 9, 5, 9);
                        event.getEntity().setVelocity(event.getDamager().getLocation().getDirection().setY(0).normalize().multiply(5));
                    }
                }
        }
    }
}
