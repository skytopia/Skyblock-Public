package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.reward.TrailReward;

public class Skittles extends Chronicle {

    public String getName() {
        return "Skittles";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.INK_SACK, (short) 0, 5),
                new Stack(Material.INK_SACK, (short) 1, 5),
                new Stack(Material.INK_SACK, (short) 2, 5),
                new Stack(Material.INK_SACK, (short) 5, 5),
                new Stack(Material.INK_SACK, (short) 6, 5),
                new Stack(Material.INK_SACK, (short) 7, 5),
                new Stack(Material.INK_SACK, (short) 8, 5),
                new Stack(Material.INK_SACK, (short) 9, 5),
                new Stack(Material.INK_SACK, (short) 10, 5),
                new Stack(Material.INK_SACK, (short) 11, 5),
                new Stack(Material.INK_SACK, (short) 12, 5),
                new Stack(Material.INK_SACK, (short) 13, 5),
                new Stack(Material.INK_SACK, (short) 14, 5)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Reputation++"),
                new TrailReward("Rainbow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.INK_SACK, 1, (short) 7);
    }

    public boolean isRepeatable() {
        return false;
    }
}
