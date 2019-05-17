package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;

/**
 * Consuming this fish provides 10 minutes of
 * protection against the effects of the Nether.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class FireProofSalmon extends Gadget {

    @Override
    public String getName() {
        return ChatColor.UNDERLINE + "Fire-Proof Salmon";
    }

    @Override
    public int getPrice() {
        return 7500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Consumption negates effects of the Nether",
                "The effect lasts for 10 minutes"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.RAW_FISH, 2, (short) 1);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (usable(event.getPlayer(), event.getItem())) {
            main().nether().increaseFireproofing(event.getPlayer().getUniqueId(), 120);
            event.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, event.getPlayer().getLocation(), 20);
        }
    }
}
