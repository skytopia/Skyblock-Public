package solar.rpg.skyblock.challenges.chapter4.part3;

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
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.island.chronology.reward.TrailReward;
import solar.rpg.skyblock.util.ItemUtility;

public class Legend4 extends Chronicle implements Live {

    public String getName() {
        return "The Detour";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Find the hidden End Shrine", "(located 1km directly north of spawn)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new TrailReward("Ender")
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_EYE);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!isEndWorld(event.getPlayer().getWorld())) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Location click = event.getClickedBlock().getLocation();
        if (click.getX() == 1061 && click.getY() == 71 && click.getZ() == -30) {
            event.setCancelled(true);
            Island is = main.islands().getIsland(event.getPlayer().getUniqueId());
            if (is == null) return;
            if (!is.chronicles().has("The Detour")) {
                if (!main.challenges().complete(event.getPlayer(), this)) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Do not return until you are needed.");
                    event.getPlayer().setHealth(1.1);
                    event.getPlayer().damage(0.1);
                    return;
                }
                main.challenges().complete(event.getPlayer(), this);
                event.getPlayer().getWorld().strikeLightningEffect(click);
                event.getPlayer().getWorld().playSound(click, Sound.BLOCK_CHEST_OPEN, 1F, 2F);
                Inventory open = Bukkit.createInventory(null, 27, "...");
                open.setItem(13, ItemUtility.createEnchantBook(Enchantment.BINDING_CURSE, 1));
                event.getPlayer().openInventory(open);
            }
        }
    }
}
