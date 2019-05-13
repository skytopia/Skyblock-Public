package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.event.PlayerForumCheckStatsEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class GetInvolved extends Chronicle implements Live {

    public String getName() {
        return "Get Involved!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Become a forum member (/register!)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.SPEED, 20 * 600, 1)),
                new ItemReward(new ItemStack(Material.ENDER_PEARL, 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LONG_GRASS, 1, (short) 2);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onRegister(PlayerForumCheckStatsEvent event) {
        main().challenges().complete(event.getPlayer(), this);
    }
}
