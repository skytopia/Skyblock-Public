package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.event.PlayerExperienceOreRainEent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Capitalist extends Chronicle implements Live {

    public String getName() {
        return "Capitalist";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Experience the occurrence of Ore Rain")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.changeSize(ItemUtility.createEnchantBook(Enchantment.VANISHING_CURSE, 1, Enchantment.LOOT_BONUS_BLOCKS, 7, Enchantment.PROTECTION_ENVIRONMENTAL, 9, Enchantment.FIRE_ASPECT, 5, Enchantment.KNOCKBACK, 3), 2))
        };
    }

    public ItemStack getIcon() {
        return ItemUtility.createSplashPotion(PotionEffectType.BLINDNESS, 0, 0);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerExperienceOreRainEent event) {
        main.challenges().award(event.getPlayer(), this);
    }
}
