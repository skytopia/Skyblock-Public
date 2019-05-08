package solar.rpg.skyblock.challenges.chapter3.part2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.stored.Settings;

import java.util.UUID;

public class Always extends Chronicle implements Live {

    public String getName() {
        return "Always";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Set foot in the End")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new Reward() {
                    public void reward(Island island, UUID toReward) {
                        island.aeon().add(4);
                    }

                    public String getReward() {
                        return "Empowers your Aeon Block Block with End Stone";
                    }
                },
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.GOLD_SWORD, 1), ChatColor.LIGHT_PURPLE + "Pure Sword", ChatColor.GRAY + "Purity I", "Don't strike the Living!"), Enchantment.VANISHING_CURSE, 1, Enchantment.MENDING, 1)),
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DRAGON_EGG);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onBreak(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Settings.ADMIN_WORLD_ID + "_end"))
            main.challenges().award(event.getPlayer(), this);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().contains(Settings.ADMIN_WORLD_ID)) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.isCancelled()) return;
        Player pl = (Player) event.getDamager();
        if (pl.getInventory().getItemInMainHand() != null) {
            ItemStack main = pl.getInventory().getItemInMainHand();
            if (main.hasItemMeta() && main.getItemMeta().hasDisplayName())
                if (main.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE + "Pure Sword"))
                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 100, 1));
        }
    }
}
