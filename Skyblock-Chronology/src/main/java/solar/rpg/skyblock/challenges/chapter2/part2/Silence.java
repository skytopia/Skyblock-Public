package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Silence extends Chronicle implements Live {

    public String getName() {
        return "Silence";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Break a natural Mob Spawner")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Silence")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MOSSY_COBBLESTONE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.MOB_SPAWNER)
            if (isAnyIslandWorld(event.getBlock().getWorld()))
                if (((CreatureSpawner) event.getBlock().getState()).getSpawnedType() != EntityType.PIG)
                    if (main().islands().getIsland(event.getPlayer().getUniqueId()) != null)
                        main().challenges().complete(event.getPlayer(), this);
    }
}
