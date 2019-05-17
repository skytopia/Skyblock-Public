package solar.rpg.skyblock.challenges.chapter5.part2;

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

public class OneToRemember extends Chronicle implements Live {

    public String getName() {
        return "One to Remember";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Airshow',", "with 2 or more participants", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Super Gliding")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ELYTRA, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerCompleteMinigameEvent event) {
        if (event.isFlawless() && event.getParticipants() >= 2)
            if (event.getMinigame().getName().equals("Airshow"))
                main().challenges().complete(event.getPlayer(), this);
    }
}
