package solar.rpg.skyblock.challenges.chapter5.part1;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.reward.TrailReward;

public class Multimillionaire extends Chronicle {

    public String getName() {
        return "Multi-Millionaire";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Have over 3,000,000 ƒ on hand") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return main.getEconomy().has(toCheck, 3000000);
            }
        }};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new TrailReward("Riches")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.EMERALD);
    }

    public boolean isRepeatable() {
        return false;
    }
}
