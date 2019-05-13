package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.IslandReputationCalculateEvent;
import solar.rpg.skyblock.island.Ability;


/**
 * Reputation+ permanently increases island visitor score by 50.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class ReputationPlus extends Ability {

    public String getName() {
        return ChatColor.LIGHT_PURPLE + "Reputation+";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Permanently increases your island's",
                "reputation score by 50. This effect",
                "stacks with other similar effects!",
                "\"Hottest island on the block!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SHULKER_SHELL, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCalculate(IslandReputationCalculateEvent event) {
        if (eligible(event.getIsland()))
            event.setCalculated(event.getCalculated() + 50);
    }
}
