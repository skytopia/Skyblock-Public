package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Sophomore extends Chronicle {

    public String getName() {
        return "Sophomore";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Dedication Skill Lv. 10+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return main.xp().quickLevel(island.skills().dedication()) >= 10;
            }
        }, new CBCrit("Participation in 50+ Minigames") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().participations() >= 50;
            }
        }, new CBCrit("10+ Gold Medals Earnt") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().gold() >= 10;
            }
        }, new CBCrit("Island Value Level 750+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.getValue() >= 750;
            }
        }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(8)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SAPLING, 1, (short) 1);
    }

    public boolean isRepeatable() {
        return false;
    }
}
