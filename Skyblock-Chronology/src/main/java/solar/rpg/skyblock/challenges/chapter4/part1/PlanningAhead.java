package solar.rpg.skyblock.challenges.chapter4.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class PlanningAhead extends Chronicle implements Live {

    public String getName() {
        return "Planning Ahead";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'RFW',", "With 1-2 participants only", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Unlocks \"Uppercut\" Combat Skill";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.PAPER, 1, (short) 0);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isGold() && event.getParticipants() <= 2)
            if (event.getMinigame().getName().equals("RFW"))
                main().challenges().award(event.getPlayer(), this);
    }
}
