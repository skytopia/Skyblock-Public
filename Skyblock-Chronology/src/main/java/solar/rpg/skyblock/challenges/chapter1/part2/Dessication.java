package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Dessication extends Chronicle {

    public String getName() {
        return "Dessication";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new ItemCrit(
                        new Single(Material.SKULL_ITEM, 1, (short) 1),
                        new Single(Material.MAGMA_CREAM, 8),
                        new Multiple(Material.NETHERRACK, 4),
                        new Single(Material.GLOWSTONE, 8)
                )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.GOLD_SWORD, 1), "Ghost Buster"), Enchantment.DAMAGE_UNDEAD, 8, Enchantment.KNOCKBACK, 4)),
                new GadgetReward("Aeon Milk Bucket")
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.GOLD_SWORD, 1), "Ghost Buster II"), Enchantment.DAMAGE_UNDEAD, 10, Enchantment.KNOCKBACK, 6, Enchantment.FIRE_ASPECT, 2)),
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COAL, 1, (short) 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
