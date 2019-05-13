package solar.rpg.skyblock.gadgets;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;

/**
 * Converts carpet into end portals on right click.
 * Converts end portals into carpet on left click.
 * Unlocking this gadget allows access to the End.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class EndPortalWand extends Gadget {

    @Override
    public String getName() {
        return "End Portal Wand";
    }

    @Override
    public int getPrice() {
        return 75000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Opens up End Portals through Carpet"};
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.END_ROD, 1);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (usable(event.getPlayer(), event.getHand())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // Convert carpet into end portal.
                if (event.getClickedBlock().getType() == Material.CARPET) {
                    event.getClickedBlock().setType(Material.ENDER_PORTAL);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_CREEPER_PRIMED, 2F, 2F);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                // Convert end portal into carpet.
                if (event.getClickedBlock().getType() == Material.ENDER_PORTAL) {
                    event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ENTITY_ENDERMITE_DEATH, 2F, 2F);
                    event.getClickedBlock().breakNaturally();
                }
            }
        }
    }
}
