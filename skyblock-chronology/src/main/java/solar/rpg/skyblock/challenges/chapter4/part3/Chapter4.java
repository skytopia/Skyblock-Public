package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Final;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.reward.TrailReward;
import solar.rpg.skyblock.island.chronology.type.Chapter;

public class Chapter4 extends Chronicle implements Final {

    public String getName() {
        return "Chapter 4 Finale";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new CBCrit("Complete all Chapter 4 Chronicles") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return island.chronicles().hasChapter(Chapter.CHAPTER4);
                    }
                },
                new CBCrit("Building Skill Lv. 30+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().building()) >= 30;
                    }
                },
                new CBCrit("Popularity Skill 8+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().popularity()) >= 8;
                    }
                }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new TrailReward("Eminence"),
                new GadgetReward("Aeon Potion")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CAKE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
