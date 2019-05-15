package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;
import solar.rpg.skyblock.util.LocationUtil;

/**
 * Throwing the bobber into the ground and right clicking
 * will launch the player towards its location.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class GrappleHook extends Gadget {

    @Override
    public String getName() {
        return ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Grappling Hook!";
    }

    @Override
    public int getPrice() {
        return 5000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Pulls you towards your hook if",
                "it is firmly planted in a block"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FISHING_ROD, 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void fishing(PlayerFishEvent event) {
        if (event.getCaught() != null) return;
        ItemStack inHand = event.getPlayer().getInventory().getItemInMainHand();
        // Fishing can only be done in the main hand.
        if (usable(event.getPlayer(), inHand)) {
            Player player = event.getPlayer();
            Location location = player.getLocation();
            Location bobber = event.getHook().getLocation();
            if (event.getState() == PlayerFishEvent.State.IN_GROUND || isLocationNearBlock(bobber))
                if (inHand.getType() == Material.FISHING_ROD) {
                    player.setFallDistance(0);
                    player.playSound(location, Sound.ENTITY_FIREWORK_SHOOT, 4, 4);
                    player.setVelocity(bobber.toVector().subtract(player.getLocation().toVector()).multiply(0.225));
                    inHand.setDurability((short) (inHand.getDurability() + 5));
                }
        }
    }

    /**
     * @param loc Location of bobber.
     * @return True if it is sitting on a non-hollow block.
     */
    private boolean isLocationNearBlock(Location loc) {
        return !LocationUtil.HOLLOW_MATERIALS.contains(loc.getBlock().getTypeId()) || !LocationUtil.HOLLOW_MATERIALS.contains(loc.getBlock().getRelative(BlockFace.DOWN).getTypeId());
    }
}
