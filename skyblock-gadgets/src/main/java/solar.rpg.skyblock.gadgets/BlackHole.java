package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.island.Gadget;

/**
 * Consuming this gadget attracts all nearby monsters.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class BlackHole extends Gadget {

    @Override
    public String getName() {
        return ChatColor.BLACK + "Black Hole";
    }

    @Override
    public int getPrice() {
        return 2500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Powerful hole in spacetime",
                "Attracts all nearby monsters"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.POWERED_MINECART, 3);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (usable(event.getPlayer(), getItemInHand(event.getPlayer(), event.getHand()))) {
            event.setCancelled(true);
            removeOne(event.getPlayer(), event.getHand());
            Location at = event.getPlayer().getLocation();
            new BukkitRunnable() {
                int count = 300;

                @Override
                public void run() {
                    if (count == 0) {
                        at.getWorld().strikeLightning(at);
                        this.cancel();
                        return;
                    }
                    count--;
                    if (count % 5 == 0) {
                        at.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 1);
                        at.getWorld().spawnParticle(Particle.SMOKE_LARGE, at, 5);
                    }
                    for (Entity nearby : at.getWorld().getNearbyEntities(at, 25, 25, 25))
                        if (nearby instanceof Monster) {
                            Location targetLoc = nearby.getLocation();
                            nearby.setVelocity(at.toVector().subtract(targetLoc.toVector()).multiply(0.33));
                        }
                }
            }.runTaskTimer(main().plugin(), 0L, 2L);
        }
    }
}