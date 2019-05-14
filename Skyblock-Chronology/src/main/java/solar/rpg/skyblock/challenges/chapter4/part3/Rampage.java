package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.Main;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class Rampage extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public Rampage() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Rampage";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill 80 mobs in < 30 seconds", "(timer starts on first kill)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.potion().add(0);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Potion with Strength";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_SWORD, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onMove(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (streak.containsKey(event.getEntity().getKiller().getUniqueId()))
            streak.put(event.getEntity().getKiller().getUniqueId(), streak.get(event.getEntity().getKiller().getUniqueId()) + 1);
        else {
            streak.put(event.getEntity().getKiller().getUniqueId(), 1);
            Bukkit.getScheduler().runTaskLater(main.plugin(), () -> {
                if (!streak.containsKey(event.getEntity().getKiller().getUniqueId())) return;
                int amt = streak.get(event.getEntity().getKiller().getUniqueId());
                if (amt >= 80)
                    main.challenges().complete(event.getEntity().getKiller(), this);
                streak.remove(event.getEntity().getKiller().getUniqueId());
            }, 30 * 20L);
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

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN)
            if (event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.ICE)
                streak.remove(event.getPlayer().getUniqueId());
    }
}
