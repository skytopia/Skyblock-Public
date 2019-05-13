package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerMinigameCooldownCalculateEvent;
import solar.rpg.skyblock.island.Ability;

import java.util.concurrent.TimeUnit;


/**
 * Cooldown+ permanently decreases minigame cooldown by 30 seconds.
 * Note: This effect applies to an individual player's island, and
 * not the island that is hosting the minigame.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class CooldownPlus extends Ability {

    public String getName() {
        return ChatColor.LIGHT_PURPLE + "Cooldown+";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Permanently decreases waiting time",
                "between minigames by 30 seconds.",
                "",
                "This effect stacks with other",
                "similar ability effects!",
                "\"Impatient+!\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COMPASS, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCalculate(PlayerMinigameCooldownCalculateEvent event) {
        if (eligible(main().islands().getIsland(event.getPlayer().getUniqueId())))
            event.setCooldown(event.getCooldown() - TimeUnit.SECONDS.toMillis(30));
    }
}
