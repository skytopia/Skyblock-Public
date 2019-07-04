package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Chlorophyll extends Chronicle {

    public String getName() {
        return "Chlorophyll";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new ItemCrit(
                        new Stack(Material.CARROT, 5),
                        new Stack(Material.BAKED_POTATO, 5),
                        new Single(Material.RED_MUSHROOM, 48),
                        new Single(Material.BROWN_MUSHROOM, 48),
                        new Single(Material.BREAD, 48),
                        new Single(Material.PUMPKIN_PIE, 48)
                )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.HEALTH_BOOST, 600 * 20, 4)),
                new ItemReward(new ItemStack(Material.MYCELIUM, 2)),
                new GadgetReward("Terra Rose")
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.HEALTH_BOOST, 300 * 20, 2)),
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.GLISTERING_MELON_SLICE, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
