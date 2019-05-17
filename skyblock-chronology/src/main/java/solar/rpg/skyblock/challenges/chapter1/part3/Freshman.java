package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Freshman extends Chronicle {

    public String getName() {
        return "Freshman";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Dedication Skill Lv. 5+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return main.xp().quickLevel(island.skills().dedication()) >= 5;
            }
        }, new CBCrit("Participation in 15+ Minigames") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().participations() >= 15;
            }
        }, new CBCrit("1+ Gold Medals Earnt") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.stats().gold() >= 1;
            }
        }, new CBCrit("Island Value Level 250+") {
            @Override
            public boolean has(Island island, Player toCheck) {
                return island.getValue() >= 250;
            }
        }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(7)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SAPLING);
    }

    public boolean isRepeatable() {
        return false;
    }
}
