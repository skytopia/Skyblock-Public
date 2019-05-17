package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Consolidation extends Chronicle {

    public String getName() {
        return "Consolidation";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new CBCrit("Participate in one Minigame") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return island.stats().participations() >= 1;
                    }
                },
                new CBCrit("Level up any Skill") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return (main().xp().quickLevel(island.skills().building()) >= 1
                                || main().xp().quickLevel(island.skills().combat()) >= 1
                                || main().xp().quickLevel(island.skills().handiness()) >= 1
                                || main().xp().quickLevel(island.skills().dedication()) >= 1);
                    }
                }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.SPEED, 20 * 600, 1)),
                new ItemReward(new ItemStack(Material.ENDER_PEARL, 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.COOKIE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
