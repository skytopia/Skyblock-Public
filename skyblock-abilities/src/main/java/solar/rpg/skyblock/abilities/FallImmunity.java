package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.util.Title;


/**
 * Fall Immunity protects death from falls under 100 blocks.
 * Players with this ability will always survive with 1/2 a heart.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class FallImmunity extends Ability {

    public String getName() {
        return ChatColor.YELLOW + "Fall Immunity";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Falls under 100 blocks",
                "will never kill you.",
                "\"Enderpearls too?!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.GOLDEN_BOOTS, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallProt(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!check(event.getEntity().getWorld())) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamage() > ((Player) event.getEntity()).getHealth()) {
            if (event.getEntity().getFallDistance() < 100) {
                if (eligible(main().islands().getIsland(event.getEntity().getUniqueId()))) { // And it is eligible..
                    // Do fall protection code!
                    ((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.5F, 0.5F);
                    Title.showTitle((Player) event.getEntity(), "", ChatColor.RED + "** NON-LETHAL **", 5, 0, 5);
                    ((Player) event.getEntity()).setHealth(1);
                    event.setDamage(0);
                }
            }
        }
    }
}