package solar.rpg.skyblock.challenges.chapter3.part2;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class WhitsEnd extends Chronicle implements Live {

    public String getName() {
        return "Whit's End";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Re-summon the Ender Dragon")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.DRAGON_EGG))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 2);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof EnderDragon)
            for (Entity nearby : event.getEntity().getNearbyEntities(150, 150, 150))
                if (nearby instanceof Player)
                    main.challenges().award((Player) nearby, this);
    }
}
