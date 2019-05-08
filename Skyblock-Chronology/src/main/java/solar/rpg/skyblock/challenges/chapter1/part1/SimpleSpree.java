package solar.rpg.skyblock.challenges.chapter1.part1;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

public class SimpleSpree extends Chronicle implements Live {

    public String getName() {
        return "Simple Spree";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Warp to the /shop")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(new ItemStack(Material.COAL, 8)),
                new MoneyReward(main().getEconomy(), 5000)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_INGOT);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().contains("shop"))
            main().challenges().award(event.getPlayer(), this);
    }
}
