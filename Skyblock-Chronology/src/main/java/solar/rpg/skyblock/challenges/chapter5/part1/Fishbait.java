package solar.rpg.skyblock.challenges.chapter5.part1;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class Fishbait extends Chronicle {

    public String getName() {
        return "Fishbait";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.RAW_FISH, 54),
                new Stack(Material.RAW_FISH, (short) 1, 12),
                new Stack(Material.RAW_FISH, (short) 2, 1),
                new Stack(Material.RAW_FISH, (short) 3, 6)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.potion().add(1);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Potion with Luck";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.FISHING_ROD);
    }

    public boolean isRepeatable() {
        return false;
    }
}
