package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Gadget;
import solar.rpg.skyblock.island.Island;

/**
 * Consuming this gadget makes the next n amount
 * of ore generations 100% guaranteed to an ore.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class OreSplosion extends Gadget {

    @Override
    public String getName() {
        return ChatColor.WHITE + "Ore\'Splosion!";
    }

    @Override
    public int getPrice() {
        return 2500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{
                "Boosts ore generation chance to 100%",
                "The effect can be stacked"
        };
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.PRISMARINE_CRYSTALS, 1);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (usable(event.getPlayer(), getItemInHand(event.getPlayer(), event.getHand()))) {
            // We can safely get their island without a check,
            // Since the gadget is confiscated if they do not have an island.
            Island found = main().islands().getIsland(event.getPlayer().getUniqueId());
            removeOne(event.getPlayer(), event.getHand());
            found.addSplosions(15 + main().rng().nextInt(10));
            found.actions().messageAll(ChatColor.GRAY + "" + ChatColor.ITALIC + "Your next " + found.getSplosions() + " generations will be boosted!");
        }
    }
}