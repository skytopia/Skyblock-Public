package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Perpetuity extends Chronicle {

    public String getName() {
        return "Perpetuity";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.LAPIS_BLOCK, 27),
                new Stack(Material.REDSTONE_BLOCK, 27),
                new Stack(Material.COAL_BLOCK, 27),
                new Stack(Material.IRON_BLOCK, 9),
                new Stack(Material.GOLD_BLOCK, 9),
                new Stack(Material.DIAMOND_BLOCK, 7),
                new Stack(Material.EMERALD_BLOCK, 2)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MoneyReward(main.getEconomy(), 123456),
                new GadgetReward("Drillpeck Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LEAD);
    }

    public boolean isRepeatable() {
        return false;
    }
}
