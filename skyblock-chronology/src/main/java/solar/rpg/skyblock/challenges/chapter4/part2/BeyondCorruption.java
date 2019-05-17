package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Stack;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class BeyondCorruption extends Chronicle {

    public String getName() {
        return "Beyond Corruption";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Stack(Material.MYCEL, (short) 0, 64)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(5);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Mycelium";
                    }
                },
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(11);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Red Mushroom";
                    }
                },
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(10);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Brown Mushroom";
                    }
                },
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MYCEL);
    }

    public boolean isRepeatable() {
        return false;
    }
}
