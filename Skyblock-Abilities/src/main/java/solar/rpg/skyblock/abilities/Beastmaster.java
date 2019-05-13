package solar.rpg.skyblock.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Ability;
import solar.rpg.skyblock.util.Title;


/**
 * Beastmaster allows the player to instantly tame an animal.
 * The material needed to tame the animal normally is no longer required.
 *
 * @author lavuh
 * @version 1.0
 * @since 1.0
 */
public class Beastmaster extends Ability {

    public String getName() {
        return ChatColor.RED + "Beastmaster";
    }

    public String[] getPurpose() {
        return new String[]{
                ChatColor.BLUE + "Active ability!",
                "Right clicking an untamed animal may",
                "cause it to become instantly tamed.",
                "\"NyaaAAAaaAAAAAaaaAah\""
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.CARROT_STICK, 1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!check(event.getRightClicked().getWorld())) return;
        if (event.getRightClicked() instanceof Tameable && !((Tameable) event.getRightClicked()).isTamed()) {
            if (eligible(main().islands().getIsland(event.getPlayer().getUniqueId()))) {
                // Cancel the event to stop certain things from happening, i.e., getting on an untamed horse.
                event.setCancelled(true);
                if (main().rng().nextInt(20) == 7) { // 1 in 20 chance to insta-tame
                    ((Tameable) event.getRightClicked()).setTamed(true);
                    ((Tameable) event.getRightClicked()).setOwner(event.getPlayer());
                    event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), 6);
                    event.getRightClicked().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_GUARDIAN_FLOP, 1, 0.5F);
                    event.getRightClicked().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_CAT_PURREOW, 0.5F, 1.1F);
                    Title.showTitle(event.getPlayer(), "", ChatColor.GREEN + "** TAME **", 5, 0, 5);
                } else {
                    event.getRightClicked().getWorld().spawnParticle(Particle.HEART, event.getRightClicked().getLocation(), main().rng().nextInt(2) + 1);
                    event.getRightClicked().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_SILVERFISH_DEATH, 1, 0.65F);
                    Title.showTitle(event.getPlayer(), "", ChatColor.RED + "** TAME FAIL **", 5, 0, 5);
                }
            }
        }
    }
}
