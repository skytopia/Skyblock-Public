package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class CookieFactory extends Chronicle {

    public String getName() {
        return "Cookie Factory";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Multiple(Material.INK_SACK, (short) 3, 54),
                new Multiple(Material.WHEAT, (short) 0, 54)
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
