package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;

public class Mastermind extends Chronicle implements Live {

    public String getName() {
        return "Mastermind";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Yellow Medal in a Minigame", "(a perfect score)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Minigame cooldowns are reduced by 30 seconds";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_TRAPDOOR);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerCompleteMinigameEvent event) {
        if (event.getMinigame() instanceof FlawlessEnabled)
            if (event.getScore() >= ((FlawlessEnabled) event.getMinigame()).getFlawless()) {
                main.challenges().award(event.getPlayer(), this);
            }
    }
}
