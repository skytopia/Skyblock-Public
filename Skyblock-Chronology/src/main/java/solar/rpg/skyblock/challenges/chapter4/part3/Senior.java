package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Senior extends Chronicle {

    public String getName() {
        return "Senior";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Dedication Skill Lv. 30+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return main.xp().quickLevel(island.skills().dedication()) >= 30;
            }
        }, new CBCrit("Participation in 300+ Minigames") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().participations() >= 300;
            }
        }, new CBCrit("50+ Gold Medals Earnt") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().gold() >= 50;
            }
        }, new CBCrit("5+ Yellow Medals Earnt") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().flawless() >= 5;
            }
        }, new CBCrit("Island Value Level 5000+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.getValue() >= 5000;
            }
        }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(10)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SAPLING, 1, (short) 2);
    }

    public boolean isRepeatable() {
        return false;
    }
}
