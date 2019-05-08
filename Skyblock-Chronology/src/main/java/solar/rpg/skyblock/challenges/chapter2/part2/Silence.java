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
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.stored.Settings;

public class Silence extends Chronicle implements Live {

    public String getName() {
        return "Silence";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Break a natural Mob Spawner")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Mobs are less likely to spawn on your island";
                    }
                }
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
        if (event.getBlock().getWorld().getName().startsWith(Settings.ADMIN_WORLD_ID))
            if (event.getBlock().getType() == Material.MOB_SPAWNER) {
                CreatureSpawner state = (CreatureSpawner) event.getBlock().getState();
                if (state.getSpawnedType() != EntityType.PIG)
                    if (main().islands().getIsland(event.getPlayer().getUniqueId()) != null)
                        main().challenges().award(event.getPlayer(), this);
            }
    }
}
