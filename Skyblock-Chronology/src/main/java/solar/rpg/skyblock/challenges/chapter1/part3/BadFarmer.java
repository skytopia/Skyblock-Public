package solar.rpg.skyblock.challenges.chapter1.part3;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.item.ItemCrit;
import solar.rpg.skyblock.island.chronology.criteria.item.Multiple;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.MoneyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.stored.Settings;

public class BadFarmer extends Chronicle {

    public String getName() {
        return "Bad Farmer";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new ItemCrit(
                new Multiple(Material.CACTUS, 3),
                new Multiple(Material.NETHER_STALK, 3),
                new Multiple(Material.MYCEL, 2),
                new Multiple(Material.BEETROOT, 2)
        )
        };
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.WOOD_SWORD, 1), ChatColor.DARK_RED + "Corrupted Sword", ChatColor.GRAY + "Corruption I", "Don't strike the Undead!"), Enchantment.THORNS, 2)),
                new ItemReward(ItemUtility.changeItem(ItemUtility.createPotion(PotionEffectType.HEAL, 6000, 0), ChatColor.LIGHT_PURPLE + "Invulnerability Potion")),
                new MoneyReward(main().getEconomy(), 30000)
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.changeItem(ItemUtility.createPotion(PotionEffectType.HEAL, 3600, 0), ChatColor.LIGHT_PURPLE + "Short Invulnerability Potion")),
                new MoneyReward(main().getEconomy(), 5000)
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.DEAD_BUSH, 1);
    }

    public boolean isRepeatable() {
        return true;
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
                if (main.getItemMeta().getDisplayName().contains(ChatColor.DARK_RED + "Corrupted Sword"))
                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.HARM, 100, 1));
        }
    }
}
