package solar.rpg.skyblock.challenges.chapter2.part1;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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

public class DangerZone extends Chronicle {

    public String getName() {
        return "Danger Zone";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.MAGMA, 4),
                new Stack(Material.TNT, 2),
                new Stack(Material.FIREWORK_CHARGE, 1),
                new Stack(Material.FIREBALL, 1),
                new Single(Material.ANVIL, 16),
                new Single(Material.END_CRYSTAL, 3))
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.WHITE_SHULKER_BOX, 1)),
                new ItemReward(new ItemStack(Material.TOTEM, 1)),
                new ItemReward(ItemUtility.createPotion(PotionEffectType.ABSORPTION, 900 * 20, 5)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createEnchantBook(Enchantment.PROTECTION_EXPLOSIONS, 5, Enchantment.LOOT_BONUS_MOBS, 4), 2))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createSpawnEgg(EntityType.CREEPER))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.TNT, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
