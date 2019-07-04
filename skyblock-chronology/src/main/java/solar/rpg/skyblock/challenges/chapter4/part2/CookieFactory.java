package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class CookieFactory extends Chronicle {

    public String getName() {
        return "Cookie Factory";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.COCOA, 54),
                new Stack(Material.WHEAT, (short) 0, 54)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Blood Cookie")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COOKIE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
