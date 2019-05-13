package solar.rpg.skyblock.challenges.chapter3.part2;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class DMs extends Chronicle implements Live {

    public String getName() {
        return "DMs";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Tile Swap'", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(8);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Magenta G. Terracotta";
                    }
                },
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STEP);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isGold())
            if (event.getMinigame().getName().equals("Tile Swap"))
                main().challenges().complete(event.getPlayer(), this);
    }
}
