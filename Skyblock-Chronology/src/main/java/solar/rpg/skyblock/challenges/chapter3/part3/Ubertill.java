package solar.rpg.skyblock.challenges.chapter3.part3;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class Ubertill extends Chronicle implements Live {

    public String getName() {
        return "Ubertill";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Break a Diamond Hoe")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Super Bone Meal")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_HOE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerItemBreakEvent event) {
        if (event.getBrokenItem().getType().equals(Material.DIAMOND_HOE))
            main().challenges().award(event.getPlayer(), this);
    }
}
