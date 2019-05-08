package solar.rpg.skyblock.challenges.chapter3.part2;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Janked extends Chronicle implements Live {

    public String getName() {
        return "Janked";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Have 12+ Simultaneous Potion Effects", "(move around to activate)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Falls under 100 blocks will not be lethal";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return ItemUtility.createPotion(PotionEffectType.POISON, 1, 0);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getActivePotionEffects().size() >= 12)
            main.challenges().award(event.getPlayer(), this);
    }
}
