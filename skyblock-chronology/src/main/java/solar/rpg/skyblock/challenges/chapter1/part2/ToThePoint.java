package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class ToThePoint extends Chronicle {

    public String getName() {
        return "To The Point";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new ItemCrit(
                        new Single(Material.CACTUS, 32),
                        new Single(Material.SUGAR_CANE, 32),
                        new Single(Material.VINE, 8)
                )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.REGENERATION, 20 * 240, 1)),
                new ItemReward(ItemUtility.createPotion(PotionEffectType.JUMP, 20 * 240, 1)),
                new ItemReward(new ItemStack(Material.DEAD_BUSH, 16)),
                new ItemReward(new ItemStack(Material.DISPENSER, 8))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.DEAD_BUSH, 16)),
                new ItemReward(new ItemStack(Material.DISPENSER, 4))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LARGE_FERN);
    }

    public boolean isRepeatable() {
        return true;
    }
}
