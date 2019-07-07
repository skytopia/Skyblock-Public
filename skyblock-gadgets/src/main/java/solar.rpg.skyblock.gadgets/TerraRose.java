package solar.rpg.skyblock.gadgets;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;

/**
 * Turns all variants of dirt into grass.
 * Can be used infinitely without consumption.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class TerraRose extends Gadget {

    @Override
    public String getName() {
        return "Terra Rose";
    }

    @Override
    public int getPrice() {
        return 5000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Plant it on dirt",
                "Creates grass instantly"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.POPPY);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        ItemStack inHand = getItemInHand(event.getPlayer(), event.getHand());
        if (usable(event.getPlayer(), inHand)) {
            event.setCancelled(true);
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (!event.getClickedBlock().getType().equals(Material.DIRT)) return;
            event.getPlayer().updateInventory();
            event.getClickedBlock().setType(Material.GRASS_BLOCK);
        }
    }
}
