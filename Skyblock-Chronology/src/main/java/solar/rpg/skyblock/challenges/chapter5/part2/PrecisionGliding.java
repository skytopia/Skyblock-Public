package solar.rpg.skyblock.challenges.chapter5.part2;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import solar.rpg.skyblock.event.PlayerCompleteMinigameEvent;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class PrecisionGliding extends Chronicle implements Live {

    public String getName() {
        return "Super Gliding";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Earn a Gold Medal in 'Airshow',", "With 2 or more participants", "(/play)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Sneak to increase gliding height anywhere";
                    }
                }
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
                main().challenges().award(event.getPlayer(), this);
    }

    @EventHandler
    public void onElytra(PlayerToggleSneakEvent event) {
        if (!event.getPlayer().isGliding()) return;
        if (event.getPlayer().isSneaking()) return;
        Island found = main.islands().getIsland(event.getPlayer().getUniqueId());
        if (found == null || !found.chronicles().has("Super Gliding")) return;
        Vector velocity = event.getPlayer().getVelocity();
        if (velocity.getY() >= 2) return;
        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1.05, 0)));
    }
}
