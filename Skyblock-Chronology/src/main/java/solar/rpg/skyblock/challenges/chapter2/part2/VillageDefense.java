package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.UUID;

public class VillageDefense extends Chronicle implements Live {

    public String getName() {
        return "Village Defense";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Encourage villagers to spawn an", "Iron Golem in an act of defense")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(1);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block with Sand";
                    }
                },
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.LEATHER_LEGGINGS, 1), ChatColor.GREEN + "Spiny Leggings", "From the arsenal of Mother Nature.", "Grass just wants to grow around this!"), Enchantment.DURABILITY, 999, Enchantment.MENDING, 1, Enchantment.THORNS, 6))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.RED_ROSE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof IronGolem)) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE) return;
        for (Entity nearby : event.getEntity().getNearbyEntities(75, 75, 75)) {
            Island found = main().islands().getIsland(nearby.getUniqueId());
            if (found != null)
                if (!found.chronicles().has(getName()))
                    main().challenges().complete((Player) nearby, this);
        }
    }
}
