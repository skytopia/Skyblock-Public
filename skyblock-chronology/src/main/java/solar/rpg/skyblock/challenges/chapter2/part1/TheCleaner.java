package solar.rpg.skyblock.challenges.chapter2.part1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class TheCleaner extends Chronicle implements Live {

    private final HashMap<UUID, Integer> sweeps = new HashMap<>();

    public String getName() {
        return "The Cleanup";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Affect 8 hostile mobs with", "a single sweeping attack")};
    }

    public Reward[] getReward() {
        return new Reward[]{new ItemReward(ItemUtility.changeSize(ItemUtility.createEnchantBook(Enchantment.SWEEPING_EDGE, 5), 2)),
                new MoneyReward(main.getEconomy(), 15000)};
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_SWORD);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Chronicle theCleaner = this;
        if (!sweeps.containsKey(event.getDamager().getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(main.plugin(), () -> {
                int sweep = sweeps.get(event.getDamager().getUniqueId());
                sweeps.remove(event.getDamager().getUniqueId());
                if (sweep >= 8)
                    main.challenges().complete((Player) event.getDamager(), theCleaner);
            }, 5L);
            sweeps.put(event.getDamager().getUniqueId(), 1);
        } else
            sweeps.put(event.getDamager().getUniqueId(), sweeps.get(event.getDamager().getUniqueId()) + 1);
    }
}
