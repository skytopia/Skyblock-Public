package solar.rpg.skyblock.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.util.Utility;

/**
 * Half a second after being launched, this arrow will increase its
 * velocity and change its trajectory to aim at a nearby monster.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class HomingArrow extends ArrowGadget {

    @Override
    public String getName() {
        return ChatColor.RED + "Homing Arrow";
    }

    @Override
    public int getPrice() {
        return 7500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Targets the heads of nearby monsters",
                "Becomes high velocity after targeting"
        };
    }

    @Override
    public ItemStack getIcon() {
        return ItemUtility.changeSize(ItemUtility.createTippedArrow(PotionEffectType.SATURATION, 20 * 120, 0), 10);
    }

    @EventHandler
    public void onClick(ProjectileLaunchEvent event) {
        arrowCheck(event.getEntity(), () -> Bukkit.getScheduler().runTaskLater(main().plugin(), () -> {
            for (Entity targetable : event.getEntity().getNearbyEntities(20, 20, 20))
                if (Utility.isHostile(targetable)) {
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.5F, 1.5F);
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.5F, 0.65F);
                    event.getEntity().setVelocity(((Monster) targetable).getEyeLocation().toVector().subtract(event.getEntity().getLocation().toVector()).multiply(0.3));
                    return;
                }
        }, 10L));
    }
}
