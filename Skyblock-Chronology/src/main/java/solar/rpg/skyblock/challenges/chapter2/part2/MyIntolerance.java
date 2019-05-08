package solar.rpg.skyblock.challenges.chapter2.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class MyIntolerance extends Chronicle implements Live {

    private final HashMap<UUID, Integer> streak = new HashMap<>();

    public String getName() {
        return "My Intolerance";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Eat 20 poisonous potatoes")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.LEATHER_HELMET, 1), ChatColor.GREEN + "Spiny Helmet", "From the arsenal of Mother Nature.", "Grass just wants to grow around this!"), Enchantment.DURABILITY, 999, Enchantment.MENDING, 1, Enchantment.THORNS, 6))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.POISONOUS_POTATO);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POISONOUS_POTATO) return;
        if (!streak.containsKey(event.getPlayer().getUniqueId()))
            streak.put(event.getPlayer().getUniqueId(), 1);
        else {
            int strk = streak.get(event.getPlayer().getUniqueId()) + 1;
            if (strk >= 20)
                main.challenges().award(event.getPlayer(), this);
            else
                streak.put(event.getPlayer().getUniqueId(), strk);
        }
    }
}
