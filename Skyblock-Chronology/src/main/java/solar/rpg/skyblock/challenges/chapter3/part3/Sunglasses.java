package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.util.ItemUtility;

public class Sunglasses extends Chronicle implements Live {

    public String getName() {
        return "Sunglasses";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Yellow Medal in 'Minesweeper'", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createSpawnEgg(EntityType.CREEPER)),
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Unlocks \"Minesweeper Hardmode\"";
                    }
                },
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "(ask Skyuh about it!)";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_BARDING);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.getMinigame() instanceof FlawlessEnabled)
            if (event.getMinigame().getName().equals("Minesweeper") && event.getScore() >= ((FlawlessEnabled) event.getMinigame()).getFlawless())
                main().challenges().award(event.getPlayer(), this);
    }
}
