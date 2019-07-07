package solar.rpg.skyblock.gadgets;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;

/**
 * This class contains code to discern gadget arrows
 * more quickly and cut down on duplicated lines.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
abstract class ArrowGadget extends Gadget {

    /**
     * Checks that an entity (projectile) is this specific gadget.
     *
     * @param en    The entity to check.
     * @param after The code to run afterwards if the shooter is eligible.
     */
    void arrowCheck(Entity en, Runnable after) {
        if (!isCorrectArrow(en)) return;
        if (!(((Projectile) en).getShooter() instanceof Player)) return;
        Player shooter = (Player) ((Projectile) en).getShooter();
        ItemStack arrow = shooter.getInventory().getItem(shooter.getInventory().first(getIcon().getType()));
        if (usable(shooter, arrow)) after.run();
    }

    /**
     * Checks that an arrow projectile has specific metadata.
     *
     * @param proj  The projectile to check.
     * @param meta  The metadata tag to check for.
     * @param after The code to run afterwards if eligible.
     */
    void arrowMetaCheck(Entity proj, String meta, Runnable after) {
        if (!isCorrectArrow(proj)) return;
        if (!(((Projectile) proj).getShooter() instanceof Player)) return;
        if (proj.hasMetadata(meta)) after.run();
    }

    /**
     * @return True if this entity matches the gadget's arrow type.
     */
    private boolean isCorrectArrow(Entity en) {
        switch (getIcon().getType()) {
            case SPECTRAL_ARROW:
                return en instanceof SpectralArrow;
            case TIPPED_ARROW:
            case ARROW:
                return en instanceof Arrow;
            default:
                throw new IllegalStateException("Arrow gadget is not arrow type?");
        }
    }
}
