package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class ShinyFarming extends Chronicle {

    public String getName() {
        return "Shiny Farming";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.EMERALD, 3),
                new Stack(Material.GOLDEN_CARROT, 2),
                new Stack(Material.GLISTERING_MELON_SLICE, 2),
                new Stack(Material.BLAZE_POWDER, 2),
                new Single(Material.ENDER_EYE, 48),
                new Single(Material.GHAST_TEAR, 12))
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.IRON_PICKAXE, 1), ChatColor.BOLD + "Ore Bane", ChatColor.GRAY + "Good luck getting those shinies!"), Enchantment.THORNS, 4, Enchantment.DIG_SPEED, 6, Enchantment.LOOT_BONUS_BLOCKS, 3, Enchantment.DAMAGE_ARTHROPODS, 9))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.MENDING, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.GOLD_NUGGET, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
