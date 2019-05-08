package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Final;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.type.Chapter;
import solar.rpg.skyblock.util.ItemUtility;

public class Chapter2 extends Chronicle implements Final {

    public String getName() {
        return "Chapter 2 Finale";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{
                new CBCrit("Complete all Chapter 2 Chronicles") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return island.chronicles().hasChapter(Chapter.CHAPTER2);
                    }
                },
                new CBCrit("Building Skill Lv. 10+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().building()) >= 10;
                    }
                },
                new CBCrit("Popularity Skill 2+") {
                    @Override
                    public boolean has(Island island, Player toCheck) {
                        return main.xp().quickLevel(island.skills().popularity()) >= 2;
                    }
                }
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.REGENERATION, 1200 * 20, 5), 5)),
                new GadgetReward("End Portal Wand")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CAKE);
    }

    public boolean isRepeatable() {
        return false;
    }
}
