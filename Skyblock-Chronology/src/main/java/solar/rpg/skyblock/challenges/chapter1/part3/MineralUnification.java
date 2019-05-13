package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class MineralUnification extends Chronicle {

    public String getName() {
        return "Mineral Unification";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Single(Material.EMERALD, 16),
                new Stack(Material.DIAMOND, 1),
                new Stack(Material.GOLD_INGOT, 2),
                new Single(Material.REDSTONE_BLOCK, 32),
                new Stack(Material.LAPIS_BLOCK, 1))
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.IRON_PICKAXE, 1), ChatColor.AQUA + "Chip", ChatColor.GRAY + "Your reward for your", ChatColor.AQUA + "Unification of Minerals!"), Enchantment.MENDING, 1, Enchantment.THORNS, 2, Enchantment.DIG_SPEED, 4, Enchantment.LOOT_BONUS_BLOCKS, 2)),
                new ItemReward(ItemUtility.createPotion(PotionEffectType.FAST_DIGGING, 300 * 20, 3))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.LOOT_BONUS_BLOCKS, 2, Enchantment.ARROW_INFINITE, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STONE_PICKAXE, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
