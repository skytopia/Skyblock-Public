package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;


/**
 * Silence passively lowers the spawn rate of monsters by 25%.
 * This effect only happens inside eligible islands' boundaries.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Silence extends Ability {

    public String getName() {
        return ChatColor.GRAY + "Silence";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Monsters will spawn 25% less often.",
                "Once obtained, the effect is permanent.",
                "\"Begone, monster thots!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BED, 1);
    }


    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        // This effect only happens in the island world.
        if (strictCheck(event.getLocation().getWorld())) return;

        // Apply the effect to monsters on eligible items 25% of the time.
        if (event.getEntity() instanceof Monster) {
            Island found = main().islands().getIslandAt(event.getLocation());
            if (eligible(found))
                if (main().rng().nextInt(4) == 2)
                    event.getEntity().remove();
        }
    }
}
