package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Junior extends Chronicle {

    public String getName() {
        return "Junior";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Dedication Skill Lv. 20+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return main.xp().quickLevel(island.skills().dedication()) >= 20;
            }
        }, new CBCrit("Participation in 150+ Minigames") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().participations() >= 150;
            }
        }, new CBCrit("25+ Gold Medals Earnt") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().gold() >= 25;
            }
        }, new CBCrit("Island Value Level 2500+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.getValue() >= 2500;
            }
        }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(9)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.JUNGLE_SAPLING);
    }

    public boolean isRepeatable() {
        return false;
    }
}
