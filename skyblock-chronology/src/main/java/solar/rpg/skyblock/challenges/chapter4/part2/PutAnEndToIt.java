package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class PutAnEndToIt extends Chronicle implements Live {

    public String getName() {
        return "Put An End To It";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill the Ender Dragon")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(6)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BOW, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;
        if (event.getEntity().getKiller() == null) return;
        main.challenges().complete(event.getEntity().getKiller(), this);
    }
}
