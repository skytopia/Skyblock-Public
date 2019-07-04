package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class TreeFeller extends Chronicle {

    public String getName() {
        return "Tree Feller";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new ItemCrit(
                        new Stack(Material.OAK_LOG, 4),
                        new Stack(Material.OAK_LEAVES, 1)
                )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.STONE_AXE, 1), "The Lumberjack"), Enchantment.DAMAGE_ALL, 2, Enchantment.DIG_SPEED, 3))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.FAST_DIGGING, 20 * 240, 0)),
                new ItemReward(new ItemStack(Material.APPLE, 5))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STONE_AXE);
    }

    public boolean isRepeatable() {
        return true;
    }
}
