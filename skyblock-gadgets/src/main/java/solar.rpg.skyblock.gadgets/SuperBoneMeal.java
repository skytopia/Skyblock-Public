package solar.rpg.skyblock.gadgets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;

/**
 * This bone meal can be used infinitely without consumption.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class SuperBoneMeal extends Gadget {

    @Override
    public String getName() {
        return ChatColor.BOLD + "Super Bone Meal";
    }

    @Override
    public int getPrice() {
        return 25000;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Can be used infinitely",
                "Not suitable for vegans"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BONE_MEAL);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack inHand = getItemInHand(event.getPlayer(), event.getHand());
        if (usable(event.getPlayer(), inHand)) {
            inHand.setAmount(2);
            Bukkit.getScheduler().runTaskLater(main().plugin(), () -> inHand.setAmount(1), 1L);
            event.getPlayer().getWorld().spawnParticle(Particle.FLAME, event.getClickedBlock().getLocation().clone().add(0.5, 1.5, 0.5), 1);
        }
    }
}
