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
                new Stack(Material.INK_SAC, 5),
                new Stack(Material.RED_DYE, 5),
                new Stack(Material.GREEN_DYE, 5),
                new Stack(Material.PURPLE_DYE, 5),
                new Stack(Material.CYAN_DYE, 5),
                new Stack(Material.LIGHT_GRAY_DYE, 5),
                new Stack(Material.GRAY_DYE, 5),
                new Stack(Material.PINK_DYE, 5),
                new Stack(Material.LIME_DYE, 5),
                new Stack(Material.YELLOW_DYE, 5),
                new Stack(Material.LIGHT_BLUE_DYE, 5),
                new Stack(Material.MAGENTA_DYE, 5),
                new Stack(Material.ORANGE_DYE, 5)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Reputation++"),
                new TrailReward("Rainbow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LIGHT_GRAY_DYE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
