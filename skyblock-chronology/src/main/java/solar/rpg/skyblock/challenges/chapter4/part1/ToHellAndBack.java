package solar.rpg.skyblock.challenges.chapter4.part1;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.ArrayList;
import java.util.UUID;

public class ToHellAndBack extends Chronicle implements Live {

    private final ArrayList<UUID> complete;

    public ToHellAndBack() {
        super();
        complete = new ArrayList<>();
    }

    public String getName() {
        return "To Hell And Back";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Travel to the Nether world border,", "then return to Nether spawn", "(in a single trip)", "(no flying!)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(9);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Netherrack";
                    }
                },
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.NETHER_BRICK, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onChange(PlayerMoveEvent event) {
        if (isNetherWorld(event.getTo().getWorld())) {
            if (event.getPlayer().isFlying() && complete.contains(event.getPlayer().getUniqueId())) {
                complete.remove(event.getPlayer().getUniqueId());
                return;
            }
            if (event.getPlayer().getLocation().distanceSquared(new Location(event.getPlayer().getWorld(), 0, 64, 0)) <= 50)
                if (complete.contains(event.getPlayer().getUniqueId())) {
                    complete.remove(event.getPlayer().getUniqueId());
                    main.challenges().complete(event.getPlayer(), this);
                }
            Location loc = event.getPlayer().getLocation();
            if (!complete.contains(event.getPlayer().getUniqueId()))
                if (loc.getX() >= 2450 || loc.getX() <= -2450 || loc.getZ() >= 2450 || loc.getZ() <= -2450)
                    complete.add(event.getPlayer().getUniqueId());
        } else complete.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        complete.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        complete.remove(event.getPlayer().getUniqueId());
    }
}
