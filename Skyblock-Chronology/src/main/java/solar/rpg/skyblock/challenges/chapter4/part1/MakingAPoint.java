package solar.rpg.skyblock.challenges.chapter4.part1;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class MakingAPoint extends Chronicle {

    public String getName() {
        return "Making A Point";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Multiple(Material.CACTUS, (short) 0, 54)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MoneyReward(main.getEconomy(), 25000)
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new Reward() {
                    @Override
                    public void reward(Island island, UUID toReward) {
                        island.milestones().add(5);
                    }

                    @Override
                    public String getReward() {
                        return "Displayed as a milestone to visitors";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CACTUS);
    }

    public boolean isRepeatable() {
        return true;
    }
}
