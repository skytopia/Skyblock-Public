package solar.rpg.skyblock.challenges.chapter5.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Final;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.type.Chapter;

public class Chapter5 extends Chronicle implements Final {

    public String getName() {
        return "Chapter 5 Finale";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new CBCrit("Complete all Chapter 5 Chronicles") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return island.chronicles().hasChapter(Chapter.CHAPTER5);
                    }
                },
                new CBCrit("Building Skill Lv. 40+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().building()) >= 50;
                    }
                },
                new CBCrit("Popularity Skill 10+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().popularity()) >= 10;
                    }
                }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                //TODO: Add shit
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CAKE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
