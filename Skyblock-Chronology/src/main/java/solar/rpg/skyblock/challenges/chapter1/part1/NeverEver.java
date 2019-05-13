package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class NeverEver extends Chronicle implements Live {

    public String getName() {
        return "Never Ever";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Travel to the Nether")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createPotion(PotionEffectType.FIRE_RESISTANCE, 20 * 600, 0)),
                new ItemReward(new ItemStack(Material.GHAST_TEAR, 2))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.NETHER_FENCE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (isNetherWorld(event.getPlayer().getWorld()))
            main().challenges().complete(event.getPlayer(), this);
    }
}
