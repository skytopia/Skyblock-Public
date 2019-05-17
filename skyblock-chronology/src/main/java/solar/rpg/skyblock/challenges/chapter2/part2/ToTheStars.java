package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.Main;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class ToTheStars extends Chronicle implements Live {

    private final HashMap<UUID, Location> from = new HashMap<>();

    public String getName() {
        return "To The Stars!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Launch yourself with an explosion more", "than 100 blocks away in 2 seconds")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.NETHER_STAR)),
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.IRON_SWORD), ChatColor.GRAY + "Pirate Scimitar"), Enchantment.DAMAGE_ALL, 4, Enchantment.SWEEPING_EDGE, 2, Enchantment.MENDING, 1)),
                new MoneyReward(main.getEconomy(), 20000)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.OBSERVER);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onPrime(final ExplosionPrimeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;
        for (Entity nearby : event.getEntity().getNearbyEntities(8, 8, 8))
            if (nearby instanceof Player)
                if (!from.containsKey(nearby.getUniqueId())) {
                    from.put(nearby.getUniqueId(), nearby.getLocation());
                    Bukkit.getScheduler().runTaskLater(main.plugin(), () -> from.remove(nearby.getUniqueId()), 165L);
                }
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;
        if (from.containsKey(event.getEntity().getUniqueId()))
            Bukkit.getScheduler().runTaskLater(main.plugin(), () -> {
                Location frm = from.get(event.getEntity().getUniqueId());
                if (frm == null) return;
                from.remove(event.getEntity().getUniqueId());
                double dist = frm.distance(event.getEntity().getLocation());
                //TODO: Test, remove debug
                Main.log("Checked, " + dist + " launched");
                if (dist >= 100) {
                    Main.log("to the stars!");
                    main.challenges().complete((Player) event.getEntity(), main.challenges().findChallenge("To The Stars!"));
                }
            }, 40L);
    }
}
