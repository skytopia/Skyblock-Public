package solar.rpg.skyblock.challenges.chapter4.part1;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class NightShift extends Chronicle {

    public String getName() {
        return "Night Shift";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.MOSSY_COBBLESTONE, 27),
                new Stack(Material.ROTTEN_FLESH, 9),
                new Stack(Material.BONE, 9),
                new Stack(Material.STRING, 3),
                new Stack(Material.GUNPOWDER, 3),
                new Stack(Material.ENDER_PEARL, 3)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MoneyReward(main.getEconomy(), 50000),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createSpawnEgg(EntityType.VILLAGER), 2)),
                new ItemReward(new ItemStack(Material.SPAWNER, 1))
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.changeSize(ItemUtility.createSpawnEgg(EntityType.VILLAGER), 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MOSSY_COBBLESTONE);
    }

    public boolean isRepeatable() {
        return true;
    }
}
