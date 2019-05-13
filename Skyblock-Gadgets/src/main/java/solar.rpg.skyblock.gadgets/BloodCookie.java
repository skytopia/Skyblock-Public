package solar.rpg.skyblock.gadgets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.island.Gadget;

/**
 * Consuming this cookie gives the player a random potion effect.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class BloodCookie extends Gadget {

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "Blood Cookie";
    }

    @Override
    public int getPrice() {
        return 7500;
    }

    @Override
    public String[] getPurpose() {
        return new String[]{"Consuming this item will instill", "a random potion effect on you"};
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.COOKIE, 8);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (usable(event.getPlayer(), event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.values()[main().rng().nextInt(PotionEffectType.values().length)], 20 * 30, 0));
            event.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, event.getPlayer().getLocation(), 20);
        }
    }
}
