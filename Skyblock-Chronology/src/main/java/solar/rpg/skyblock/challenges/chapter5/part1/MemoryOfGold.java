package solar.rpg.skyblock.challenges.chapter5.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.reward.TrailReward;

public class MemoryOfGold extends Chronicle implements Live {

    public String getName() {
        return "Memory Of Gold";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Taking Notes'", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new TrailReward("Note")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.NOTE_BLOCK, 1, (short) 0);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isGold())
            if (event.getMinigame().getName().equals("Taking Notes"))
                main().challenges().award(event.getPlayer(), this);
    }
}
