package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;
import solar.rpg.skyblock.util.Title;

/**
 * You can mount any entity while holding this gadget.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class AnySaddle extends Gadget {

    @Override
    public String getName() {
        return "Any-Saddle";
    }

    @Override
    public int getPrice() {
        return 15000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Giddy-up, bitch!"};
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.SADDLE);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (usable(event.getPlayer(), event.getHand())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            if (event.getRightClicked().getPassengers().size() == 0) {
                event.getRightClicked().addPassenger(event.getPlayer());
                Title.showTitle(event.getPlayer(), "", ChatColor.GOLD + "** MOUNT **", 5, 0, 5);
            }
        }
    }
}
