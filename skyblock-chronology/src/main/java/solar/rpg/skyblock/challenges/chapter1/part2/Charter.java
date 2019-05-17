package solar.rpg.skyblock.challenges.chapter1.part2;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class Charter extends Chronicle implements Live {

    public String getName() {
        return "Charter";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Travel out 1,000 blocks in the Nether")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.enchant(ItemUtility.changeItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), ChatColor.RED + "Magmatic Chestplate", "A piece of a mystic armor set.", "It is very cool to the touch."), Enchantment.DURABILITY, 999)),
                new ItemReward(ItemUtility.changeSize(ItemUtility.createPotion(PotionEffectType.FIRE_RESISTANCE, 300 * 20, 0), 5))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.SADDLE, 1);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (isNetherWorld(event.getTo().getWorld()))
            if (isOver(event.getTo()))
                main().challenges().complete(event.getPlayer(), this);
    }

    private boolean isOver(Location to) {
        return to.getX() >= 1000 || to.getX() <= -1000 || to.getZ() >= 1000 || to.getZ() <= -1000;
    }
}
