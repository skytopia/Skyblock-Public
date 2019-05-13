package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;

/**
 * Visioned stops the blinding effect from affecting
 * the player entering the End. The other effects will
 * still apply, however.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class SuperGliding extends Ability {

    public String getName() {
        return ChatColor.GRAY + "" + ChatColor.ITALIC + "Super Gliding";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.BLUE + "Active ability!",
                "While gliding with Elytra, tap sneak",
                "to instantly gain upward momentum.",
                "\"Airshow wherever, whenever!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ELYTRA, 1);
    }

    @EventHandler
    public void onElytra(PlayerToggleSneakEvent event) {
        if (!event.getPlayer().isGliding()) return;
        if (event.getPlayer().isSneaking()) return;
        Island found = main().islands().getIsland(event.getPlayer().getUniqueId());
        if (found == null || !eligible(found)) return;
        Vector velocity = event.getPlayer().getVelocity();
        if (velocity.getY() >= 2) return;
        event.getPlayer().setVelocity(new Vector(velocity.getX(), velocity.getY() + 0.5, velocity.getZ()));
    }
}
