package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.event.PlayerVisitIslandEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.AbilityReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.util.UUID;

public class Housewarming extends Chronicle implements Live {

    public String getName() {
        return "Housewarming";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Receive 5 visitors at one time", "(Owner must be online)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new AbilityReward("Reputation+")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.FIREWORK);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerVisitIslandEvent event) {
        if (event.getIsland().members().getVisitors().size() >= 5)
            for (UUID online : event.getIsland().members().getMembers())
                if (Bukkit.getOfflinePlayer(online).isOnline())
                    main.challenges().complete(Bukkit.getPlayer(online), this);
    }
}
