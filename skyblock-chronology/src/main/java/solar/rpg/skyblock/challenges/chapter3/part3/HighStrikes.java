package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class HighStrikes extends Chronicle implements Live {

    public String getName() {
        return "High Strikes";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill a Powered Creeper")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Thor Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.GLISTERING_MELON_SLICE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Creeper)
            if (((Creeper) event.getEntity()).isPowered())
                if (event.getDamager() instanceof Player)
                    if (event.getDamage() >= ((Creeper) event.getEntity()).getHealth())
                        main.challenges().complete((Player) event.getDamager(), this);
    }
}
