package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class SlickMeander extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public SlickMeander() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Slick Meander";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Walk continuously across ice for 4km", "(don't jump!)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(6);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Packed Ice";
                    }
                },
                new MilestoneReward(12)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_BOOTS, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.ICE) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
                if (streak.containsKey(event.getPlayer().getUniqueId())) {
                    streak.put(event.getPlayer().getUniqueId(), streak.get(event.getPlayer().getUniqueId()) + 1);
                    if (streak.get(event.getPlayer().getUniqueId()) == 4000)
                        main.challenges().award(event.getPlayer(), this);
                } else streak.put(event.getPlayer().getUniqueId(), 1);
        } else if (streak.containsKey(event.getPlayer().getUniqueId()))
            streak.remove(event.getPlayer().getUniqueId());
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
