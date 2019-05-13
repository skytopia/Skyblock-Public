package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
import java.util.concurrent.TimeUnit;

public class HotTenacity extends Chronicle implements Live {

    private final HashMap<UUID, Long> streak;

    public HotTenacity() {
        super();
        streak = new HashMap<>();
    }

    public String getName() {
        return "Hot Tenacity";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Remain in the Nether for 10+ Minutes", "(return back to island to get reward)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.CHAINMAIL_HELMET, 1), ChatColor.RED + "Magmatic Helmet", "A piece of a mystic armor set.", "It is very cool to the touch."), Enchantment.DURABILITY, 999, Enchantment.MENDING, 1)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createEnchantBook(Enchantment.ARROW_FIRE, 1, Enchantment.FIRE_ASPECT, 2, Enchantment.PROTECTION_FIRE, 3), 3)),
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.NETHERRACK, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onChange(PlayerChangedWorldEvent event) {
        if (isNetherWorld(event.getPlayer().getWorld()))
            streak.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        else {
            if (!streak.containsKey(event.getPlayer().getUniqueId())) return;
            long time = streak.get(event.getPlayer().getUniqueId());
            streak.remove(event.getPlayer().getUniqueId());
            if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time) >= 10) {
                main().challenges().complete(event.getPlayer(), this);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        streak.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        streak.remove(event.getPlayer().getUniqueId());
    }
}
