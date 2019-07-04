package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.island.Island;

/**
 * Herobrine Eyes allows players to look at endermen without
 * causing them to become aggressive.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class HerobrineEyes extends Ability {


    public String getName() {
        return ChatColor.WHITE + "" + ChatColor.ITALIC + "Herobrine Eyes";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.YELLOW + "Passive ability!",
                "Looking at endermen will no longer",
                "cause them to become hostile.",
                "\"Watcha lookin\' at?\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_EYE, 1);
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Enderman)) return;
        if (!(event.getTarget() instanceof Player)) return;
        if (event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER) return;
        Island is = main().islands().getIsland(event.getTarget().getUniqueId());
        if (is != null && eligible(is))
            event.setCancelled(true);
    }
}
