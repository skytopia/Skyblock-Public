package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Single;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class HeadHunt extends Chronicle {

    public String getName() {
        return "Head Hunt";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Single(Material.SKULL_ITEM, 3, (short) 5)
        )};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(13);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Purpur Slab";
                    }
                },
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CHORUS_PLANT, 1);
    }

    public boolean isRepeatable() {
        return false;
    }
}
