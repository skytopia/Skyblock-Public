package solar.rpg.skyblock.challenges.chapter4.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class StandYourGround extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak;

    public StandYourGround() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Stand Your Ground";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Dodge 75 Shulker Bullets,", "without taking damage")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Divergent Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SHULKER_SHELL, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDeath(ProjectileHitEvent event) {
        if (check(event.getEntity().getWorld())) return;
        if (!(event.getEntity() instanceof ShulkerBullet)) return;
        if (!(((ShulkerBullet) event.getEntity()).getTarget() instanceof Player)) return;
        Player target = (Player) ((ShulkerBullet) event.getEntity()).getTarget();
        if (target == null) return;
        if (!streak.containsKey(target.getUniqueId()))
            streak.put(target.getUniqueId(), 1);
        else
            streak.put(target.getUniqueId(), streak.get(target.getUniqueId()) + 1);
        if (streak.get(target.getUniqueId()) == 75)
            main.challenges().award(target, this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (streak.containsKey(event.getEntity().getUniqueId()))
                streak.remove(event.getEntity().getUniqueId());
    }
}
