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

public class WholesaleMeats extends Chronicle {

    public String getName() {
        return "Wholesale Meats";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.PORKCHOP, 9),
                new Stack(Material.COD, 9),
                new Stack(Material.BEEF, 9),
                new Stack(Material.CHICKEN, 9),
                new Stack(Material.MUTTON, 9),
                new Stack(Material.RABBIT, 2),
                new Stack(Material.SALMON, 1)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MoneyReward(main.getEconomy(), 200000),
                new GadgetReward("Poké Ball")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COOKED_BEEF);
    }

    public boolean isRepeatable() {
        return false;
    }
}
