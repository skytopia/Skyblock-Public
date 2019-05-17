package solar.rpg.skyblock.challenges.chapter4.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class CloseCombatII extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public CloseCombatII() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Close Combat II";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill 50 Blazes in a row,", "without taking damage")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.STONE_SWORD, 1), ChatColor.GRAY + "Superheavy Stone Sword"), Enchantment.KNOCKBACK, 15, Enchantment.FIRE_ASPECT, 2)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.NIGHT_VISION, 960 * 20, 0), 3)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.INVISIBILITY, 300 * 20, 0), 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BLAZE_POWDER, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Blaze)) return;
        if (isAnyIslandWorld(event.getEntity().getWorld())) return;
        if (event.getEntity().getKiller() != null) {
            if (!event.getEntity().getKiller().isOnline()) return;
            if (!streak.containsKey(event.getEntity().getKiller().getUniqueId()))
                streak.put(event.getEntity().getKiller().getUniqueId(), 1);
            else
                streak.put(event.getEntity().getKiller().getUniqueId(), streak.get(event.getEntity().getKiller().getUniqueId()) + 1);
            int current = streak.get(event.getEntity().getKiller().getUniqueId());
            if (current % 10 == 0)
                event.getEntity().getWorld().spawnParticle(Particle.FLAME, event.getEntity().getLocation(), 8);
            if (current == 50)
                main().challenges().complete(event.getEntity().getKiller(), this);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            streak.remove(event.getEntity().getUniqueId());
    }
}
