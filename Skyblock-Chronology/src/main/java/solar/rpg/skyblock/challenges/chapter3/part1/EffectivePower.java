package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class EffectivePower extends Chronicle implements Live {

    public String getName() {
        return "Effective Power";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Stand on a fully powered beacon")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Ender Arrow")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BEACON);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerMoveEvent event) {
        Block bl = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (bl.getType() != Material.BEACON) return;
        if (((Beacon) bl.getState()).getTier() == 4)
            main.challenges().award(event.getPlayer(), this);
    }
}
