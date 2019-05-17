package solar.rpg.skyblock.challenges.chapter4.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Endless extends Chronicle implements Live {

    private final HashMap<UUID, Long> streak;

    public Endless() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Endless";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit(
                "Go to, and remain in",
                "the End for 45+ Minutes",
                "(return back to island to get reward)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Herobrine Eyes")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_STONE, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onChange(PlayerChangedWorldEvent event) {
        if (isEndWorld(event.getPlayer().getWorld())) {
            streak.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        } else {
            if (!streak.containsKey(event.getPlayer().getUniqueId())) return;
            long time = streak.get(event.getPlayer().getUniqueId());
            streak.remove(event.getPlayer().getUniqueId());
            if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time) >= 45)
                main().challenges().complete(event.getPlayer(), this);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        streak.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        streak.remove(event.getPlayer().getUniqueId());
    }
}
