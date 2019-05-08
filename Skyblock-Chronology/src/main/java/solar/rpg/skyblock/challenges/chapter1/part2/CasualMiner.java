package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class CasualMiner extends Chronicle {

    public String getName() {
        return "Casual Miner";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new ItemCrit(
                        new Multiple(Material.COBBLESTONE, 16),
                        new Multiple(Material.DIRT, 3),
                        new Multiple(Material.SAND, 3),
                        new Multiple(Material.GRAVEL, 3),
                        new Multiple(Material.COAL, 1)
                )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.SILK_TOUCH, 1)),
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.DIG_SPEED, 3)),
                new GadgetReward("Ore'Splosion!")
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.SILK_TOUCH, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COBBLESTONE, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
