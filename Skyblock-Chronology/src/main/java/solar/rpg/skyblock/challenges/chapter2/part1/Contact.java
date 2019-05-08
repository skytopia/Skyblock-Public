package solar.rpg.skyblock.challenges.chapter2.part1;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class Contact extends Chronicle implements Live {

    private final HashMap<UUID, Integer> flightDistance = new HashMap<>();

    public String getName() {
        return "Contact";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("In a single Elytra flight, travel", "750 blocks and crash into a wall")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Crash Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ELYTRA, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onChange(EntityToggleGlideEvent event) {
        if (event.isGliding())
            flightDistance.put(event.getEntity().getUniqueId(), 1);
        else
            flightDistance.remove(event.getEntity().getUniqueId());

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (flightDistance.containsKey(event.getPlayer().getUniqueId()))
            if (event.getPlayer().isGliding())
                flightDistance.put(event.getPlayer().getUniqueId(), flightDistance.get(event.getPlayer().getUniqueId()) + (int) Math.ceil(event.getFrom().distanceSquared(event.getTo())));
            else
                flightDistance.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onCrash(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FLY_INTO_WALL) return;
        if (flightDistance.containsKey(event.getEntity().getUniqueId())) {
            int distance = flightDistance.get(event.getEntity().getUniqueId());
            System.out.println("[Anvil] " + event.getEntity().getName() + "'s flight distance was " + distance);
            if (distance >= 750)
                main.challenges().award((Player) event.getEntity(), this);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        flightDistance.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        flightDistance.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        flightDistance.remove(event.getPlayer().getUniqueId());
    }
}
