package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Obedient extends Chronicle implements Live {

    public String getName() {
        return "Obedient";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Mr. Cloud Says'", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Beastmaster")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MUSIC_DISC_STRAD);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isGold())
            if (event.getMinigame().getName().equals("Mr. Cloud Says"))
                main().challenges().complete(event.getPlayer(), this);
    }
}
