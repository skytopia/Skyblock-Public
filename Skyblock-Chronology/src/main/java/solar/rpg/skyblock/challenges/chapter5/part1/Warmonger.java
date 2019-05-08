package solar.rpg.skyblock.challenges.chapter5.part1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.CBCrit;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.MilestoneReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Warmonger extends Chronicle {

    public String getName() {
        return "Warmonger";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new CBCrit("Kill 100 Players on War", "(/server pvp)") {
            @Override
            public boolean has(Island island, Player toCheck) {
                Bukkit.getScheduler().runTaskAsynchronously(main.plugin(), () -> {
                    try {
                        PreparedStatement stmt = main.sql().prepare("SELECT `kills` FROM `WarStats` WHERE `player_uuid`=?");
                        stmt.setString(1, toCheck.getUniqueId().toString());
                        ResultSet check = stmt.executeQuery();
                        while (check.next())
                            if (check.getInt("kills") >= 100)
                                Bukkit.getScheduler().runTask(main.plugin(), () -> Bukkit.getPluginManager().callEvent(new WarmongerCompleteEvent(toCheck)));
                        check.close();
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                return false;
            }
        }};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new MilestoneReward(13)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_SWORD);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onComplete(WarmongerCompleteEvent event) {
        main.challenges().award(event.getPlayer(), this);
    }
}
