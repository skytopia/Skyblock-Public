package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.HashMap;
import java.util.UUID;

public class CleanThatUp extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak = new HashMap<>();

    public String getName() {
        return "Clean That Up!";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Place 192 Obsidian in a row")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(2);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Stone";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.OBSIDIAN);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            if (!streak.containsKey(event.getPlayer().getUniqueId()))
                streak.put(event.getPlayer().getUniqueId(), 1);
            else {
                int str = streak.get(event.getPlayer().getUniqueId());
                if (str >= 191)
                    main.challenges().complete(event.getPlayer(), this);
                else
                    streak.put(event.getPlayer().getUniqueId(), str + 1);
            }
        } else
            streak.remove(event.getPlayer().getUniqueId());
    }
}
