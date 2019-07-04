package solar.rpg.skyblock.challenges.chapter2.part1;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Deforestation extends Chronicle {

    public String getName() {
        return "Deforestation";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Single(Material.OAK_SAPLING, 48),
                new Single(Material.SPRUCE_SAPLING, 48),
                new Single(Material.BIRCH_SAPLING, 48),
                new Single(Material.JUNGLE_SAPLING, 48),
                new Single(Material.DARK_OAK_SAPLING, 48)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.color(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1), ChatColor.GREEN + "Spiny Chestplate", "From the arsenal of Mother Nature.", "Grass just wants to grow around this!"), Enchantment.DURABILITY, 999, Enchantment.MENDING, 1, Enchantment.THORNS, 6), Color.GREEN)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createSplashPotion(PotionEffectType.POISON, 900 * 20, 2), 2)),
                new MoneyReward(main.getEconomy(), 25000)
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createSplashPotion(PotionEffectType.POISON, 300 * 20, 2)),
                new MoneyReward(main.getEconomy(), 7500)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ROSE_BUSH);
    }

    public boolean isRepeatable() {
        return true;
    }
}
