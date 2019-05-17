package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class CarbonWarrior extends Chronicle implements Live {

    public String getName() {
        return "Carbon Warrior";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Charcoal Moon'", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.MOB_SPAWNER))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.LAVA_BUCKET);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isGold())
            if (event.getMinigame().getName().equals("Charcoal Moon"))
                main().challenges().complete(event.getPlayer(), this);
    }
}
