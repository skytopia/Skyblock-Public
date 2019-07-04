package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class FeelMyFury extends Chronicle implements Live {

    private final HashMap<UUID, Double> streak = new HashMap<>();

    public String getName() {
        return "Feel My Fury!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Deal 250 hearts of damage in a", "row using only your fists")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Huge Fist Damage")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.MAGMA_BLOCK);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (((Player) event.getDamager()).getInventory().getItemInMainHand().getType() != Material.AIR) {
            streak.remove(event.getDamager().getUniqueId());
            return;
        }
        if (!streak.containsKey(event.getDamager().getUniqueId()))
            streak.put(event.getDamager().getUniqueId(), event.getFinalDamage());
        else {
            double strk = streak.get(event.getDamager().getUniqueId()) + event.getFinalDamage();
            if (strk >= 500)
                main.challenges().complete((Player) event.getDamager(), this);
            else
                streak.put(event.getDamager().getUniqueId(), strk);
        }
    }
}
