package solar.rpg.skyblock.challenges.chapter2.part1;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class BadChoices extends Chronicle implements Live {

    public String getName() {
        return "Bad Choices";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Build The Wither on Your Island")};
    }

    public Reward[] getReward() {
        return new Reward[]{new GadgetReward("Aeon Block")};
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_NUGGET);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Wither)) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_WITHER) return;
        Island found = main().islands().getIslandAt(event.getLocation());
        if (found != null)
            if (!found.chronicles().has(getName()))
                for (Entity nearby : event.getEntity().getNearbyEntities(7, 7, 7))
                    if (found.members().isMember(nearby.getUniqueId()))
                        main().challenges().award((Player) nearby, this);
    }
}
