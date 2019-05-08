package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.Material;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.stored.Settings;

import java.util.UUID;

public class WashedUp extends Chronicle implements Live {

    public String getName() {
        return "Washed Up";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Kill a Guardian")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(3);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Prismarine";
                    }
                },
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SPONGE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Guardian)
            if (event.getDamage() >= ((Guardian) event.getEntity()).getHealth())
                if (event.getDamager() instanceof Player)
                    if (event.getEntity().getWorld().getName().equals(Settings.ADMIN_WORLD_ID))
                        if (main().islands().getIsland(event.getDamager().getUniqueId()) != null)
                            main().challenges().award((org.bukkit.entity.Player) event.getDamager(), this);
    }
}
