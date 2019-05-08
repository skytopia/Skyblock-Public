package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.stored.Settings;

public class WoodlandAssassin extends Chronicle implements Live {

    public String getName() {
        return "Woodland Assassin";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill a Vindicator")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Mobs will begin to rarely drop emeralds";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DARK_OAK_STAIRS);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Vindicator)
            if (event.getDamage() >= ((Vindicator) event.getEntity()).getHealth())
                if (event.getDamager() instanceof Player)
                    if (event.getEntity().getWorld().getName().equals(Settings.ADMIN_WORLD_ID))
                        if (main().islands().getIsland(event.getDamager().getUniqueId()) != null)
                            main().challenges().award((org.bukkit.entity.Player) event.getDamager(), this);
    }
}
