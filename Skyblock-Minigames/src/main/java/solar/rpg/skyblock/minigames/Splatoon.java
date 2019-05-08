package solar.rpg.skyblock.minigames;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.task.DefaultMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Splatoon extends Minigame {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new SplatRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Splatoon";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.EXP_BOTTLE);
    }

    public String[] getDescription() {
        return new String[]{"Paintball fight! Color me in!",
                ChatColor.ITALIC + "Left click to haul paint-filled balls!",
                ChatColor.ITALIC + "Color in your island! COLOR!!!",
                ChatColor.ITALIC + "More color = more points!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    public String getSummary() {
        return "Color in your island!";
    }

    public String getObjectiveWord() {
        return "blocks painted";
    }

    public int getDuration() {
        return 35;
    }

    public int getGold() {
        return 650;
    }

    public boolean isScoreDivisible() {
        return true;
    }

    public int getMaxReward() {
        return 4000;
    }

    private class SplatRun extends DefaultMinigameTask {

        private HashMap<Location, Material> placed;
        private HashMap<Location, Byte> placed2;
        private HashMap<UUID, Long> lastThrown;

        SplatRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
        }

        public void onStart() {
            placed = new HashMap<>();
            placed2 = new HashMap<>();
            lastThrown = new HashMap<>();
        }

        public void onFinish() {
            for (Map.Entry<Location, Material> en : placed.entrySet()) {
                en.getKey().getBlock().setType(en.getValue());
                en.getKey().getBlock().setData(placed2.get(en.getKey()));
            }
            placed.clear();
            placed2.clear();
            lastThrown.clear();
            placed = null;
            placed2 = null;
            lastThrown = null;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlace(PlayerInteractEvent event) {
            if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
            if (!participants.contains(event.getPlayer().getUniqueId())) return;
            if (disqualified.contains(event.getPlayer().getUniqueId())) return;
            if (!owner.isInside(event.getPlayer().getLocation())) return;
            if (lastThrown.containsKey(event.getPlayer().getUniqueId())) {
                long diff = System.currentTimeMillis() - lastThrown.get(event.getPlayer().getUniqueId());
                if (diff < 250) return;
                lastThrown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            } else lastThrown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            Snowball ball = event.getPlayer().launchProjectile(Snowball.class);
            ball.setFireTicks(200);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onHit(ProjectileHitEvent event) {
            if (event.getEntity().getFireTicks() == 0) return;
            if (!(event.getEntity().getShooter() instanceof Player)) return;
            if (!participants.contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            if (disqualified.contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            Location bottomLeft = event.getEntity().getLocation().clone().subtract(1, 1, 1);
            Location topRight = event.getEntity().getLocation().clone().add(1, 1, 1);
            byte bit = (byte) main.main().rng().nextInt(15);
            for (int x = bottomLeft.getBlockX(); x <= topRight.getBlockX(); x++)
                for (int y = bottomLeft.getBlockY(); y <= topRight.getBlockY(); y++)
                    for (int z = bottomLeft.getBlockZ(); z <= topRight.getBlockZ(); z++) {
                        Block found = event.getEntity().getWorld().getBlockAt(x, y, z);
                        if (found.getType().isBlock() && !found.isLiquid() && found.getType() != Material.AIR && found.getType() != Material.STAINED_CLAY && !found.isBlockPowered() && !(found.getState() instanceof InventoryHolder)
                                && found.getRelative(BlockFace.DOWN).getType() != Material.SOIL && found.getType() != Material.BANNER && found.getType() != Material.SKULL && !found.getType().toString().contains("REDSTONE") &&
                                !found.getType().toString().contains("SIGN") && found.getType() != Material.MOB_SPAWNER) {
                            Block up = found.getRelative(BlockFace.UP);
                            if (up.isEmpty()) {
                                scorePoint((Player) event.getEntity().getShooter(), false, 1);
                                placed.put(found.getLocation(), found.getType());
                                placed2.put(found.getLocation(), found.getData());
                                found.setType(Material.STAINED_CLAY);
                                found.setData(bit);
                            }
                        }
                    }
        }
    }
}
