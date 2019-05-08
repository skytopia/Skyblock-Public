package solar.rpg.skyblock.challenges.chapter2.part3;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;
import solar.rpg.skyblock.stored.Settings;

public class Legend2 extends Chronicle implements Live {

    public String getName() {
        return "Tomb Raider";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Find the hidden Nether Shrine", "(located somewhere directly north of spawn)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Unlocks \"Stun\" Combat Skill";
                    }
                }
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.BRICK);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(Settings.ADMIN_WORLD_ID + "_nether")) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Location click = event.getClickedBlock().getLocation();
        if (click.getX() == 1480 && click.getY() == 45 && click.getZ() == -12) {
            event.setCancelled(true);
            Island is = main.islands().getIsland(event.getPlayer().getUniqueId());
            if (is == null) return;
            if (!is.chronicles().has("Tomb Raider")) {
                if (!main.challenges().award(event.getPlayer(), this)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Now is not your time.");
                    event.getPlayer().setHealth(1.1);
                    event.getPlayer().damage(0.1);
                    return;
                }
                is.actions().messageAll(ChatColor.RED + "A hidden power has been instilled upon you.");
                event.getPlayer().getWorld().strikeLightningEffect(click);
                event.getPlayer().getWorld().playSound(click, Sound.BLOCK_CHEST_OPEN, 1F, 2F);
                Inventory open = Bukkit.createInventory(null, 27, "...");
                open.setItem(13, ItemUtility.createEnchantBook(Enchantment.BINDING_CURSE, 1));
                event.getPlayer().openInventory(open);
            }
        }
    }
}
