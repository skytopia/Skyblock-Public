package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Perpetuity extends Chronicle {

    public String getName() {
        return "Perpetuity";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Multiple(Material.LAPIS_BLOCK, (short) 0, 27),
                new Multiple(Material.REDSTONE_BLOCK, (short) 0, 27),
                new Multiple(Material.COAL_BLOCK, (short) 0, 27),
                new Multiple(Material.IRON_BLOCK, (short) 0, 9),
                new Multiple(Material.GOLD_BLOCK, (short) 0, 9),
                new Multiple(Material.DIAMOND_BLOCK, (short) 0, 7),
                new Multiple(Material.EMERALD_BLOCK, (short) 0, 2)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MoneyReward(main.getEconomy(), 123456),
                new GadgetReward("Drillpeck Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LEASH);
    }

    public boolean isRepeatable() {
        return false;
    }
}
