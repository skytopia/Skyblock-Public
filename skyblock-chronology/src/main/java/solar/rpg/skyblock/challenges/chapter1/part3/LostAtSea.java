package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class LostAtSea extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public LostAtSea() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Lost At Sea";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Travel 1,000+ blocks by Boat", "(exit boat for reward)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.changeSize(ItemUtility.createEnchantBook(Enchantment.DEPTH_STRIDER, 2, Enchantment.FROST_WALKER, 1), 2)),
                new GadgetReward("Grappling Hook!")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SPRUCE_BOAT, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onChange(VehicleExitEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (!streak.containsKey(event.getExited().getUniqueId())) return;
            double distance = streak.get(event.getExited().getUniqueId());
            streak.remove(event.getExited().getUniqueId());

            //TODO: Test and remove debug
            System.out.println("[Anvil] " + event.getExited().getName() + " travelled the sea for " + distance + " blocks");
            if (distance >= 1000)
                main().challenges().complete((Player) event.getExited(), this);
        } else
            streak.remove(event.getExited().getUniqueId());

    }

    @EventHandler
    public void onMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat)
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
                for (Entity passenger : event.getVehicle().getPassengers())
                    if (passenger instanceof Player)
                        streak.put(passenger.getUniqueId(), streak.get(passenger.getUniqueId()) + 1);
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Boat)
            streak.put(event.getEntered().getUniqueId(), 0);
        else
            streak.remove(event.getEntered().getUniqueId());
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
            streak.remove(event.getPlayer().getUniqueId());
    }
}
