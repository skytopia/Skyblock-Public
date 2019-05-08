package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
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

public class CloseCombat extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public CloseCombat() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Close Combat";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Whilst being Combat Skill Lv. 5+:", "Kill 25 mobs in a row,", "without taking damage")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.STONE_SWORD, 1), ChatColor.GRAY + "Heavy Stone Sword"), Enchantment.KNOCKBACK, 6)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.NIGHT_VISION, 960 * 20, 0), 3)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.INVISIBILITY, 300 * 20, 0), 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.FERMENTED_SPIDER_EYE, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (check(event.getEntity().getWorld())) return;
        if (event.getEntity().getKiller() != null) {
            if (!event.getEntity().getKiller().isOnline()) return;
            if (!streak.containsKey(event.getEntity().getKiller().getUniqueId()))
                streak.put(event.getEntity().getKiller().getUniqueId(), 1);
            else
                streak.put(event.getEntity().getKiller().getUniqueId(), streak.get(event.getEntity().getKiller().getUniqueId()) + 1);
            int current = streak.get(event.getEntity().getKiller().getUniqueId());
            if (current % 5 == 0)
                event.getEntity().getWorld().spawnParticle(Particle.FLAME, event.getEntity().getLocation(), 5);
            if (current == 25)
                main().challenges().award(event.getEntity().getKiller(), this);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (streak.containsKey(event.getEntity().getUniqueId()))
                streak.remove(event.getEntity().getUniqueId());
    }
}
