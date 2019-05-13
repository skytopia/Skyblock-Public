package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Legend3 extends Chronicle implements Live {

    public String getName() {
        return "The Ultimatum";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill the Wither using both your fists")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Visioned")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.FLINT);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Wither)) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getDamage() < ((Wither) event.getEntity()).getHealth()) return;
        if (((Player) event.getDamager()).getInventory().getItemInMainHand().getType() != Material.AIR) return;
        if (((Player) event.getDamager()).getInventory().getItemInOffHand().getType() != Material.AIR) return;
        main().challenges().complete((Player) event.getDamager(), this);
    }
}
