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
import org.bukkit.metadata.FixedMetadataValue;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.DefaultMinigameTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Splatoon extends Minigame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new SplatoonTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Splatoon";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.EXP_BOTTLE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Left click to haul paint-filled balls!",
                ChatColor.ITALIC + "Color in your island! COLOR!!!",
                ChatColor.ITALIC + "More color = more points!"
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Color in your island!";
    }

    @Override
    public String getObjectiveWord() {
        return "blocks painted";
    }

    @Override
    public int getDuration() {
        return 35;
    }

    @Override
    public int getGold() {
        return 650;
    }

    @Override
    public boolean isScoreDivisible() {
        return true;
    }

    @Override
    public int getMaxReward() {
        return 4000;
    }

    private class SplatoonTask extends DefaultMinigameTask {

        /* Keep a record of what blocks were painted over, and their data. */
        private HashMap<Location, Material> placed;
        private HashMap<Location, Byte> placed2;

        /* Tracks throwing cooldowns. */
        private HashMap<UUID, Long> lastThrown;

        SplatoonTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
        }

        @Override
        public void onStart() {
            placed = new HashMap<>();
            placed2 = new HashMap<>();
            lastThrown = new HashMap<>();
        }

        @Override
        public void onFinish() {
            // Restore blocks.
            placed.forEach((key, value) -> {
                key.getBlock().setType(value);
                key.getBlock().setData(placed2.get(key));
            });
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
                if (diff < 175) return;
                lastThrown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            } else lastThrown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            Snowball ball = event.getPlayer().launchProjectile(Snowball.class);
            ball.setMetadata("splat", new FixedMetadataValue(main.main().plugin(), true));
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onHit(ProjectileHitEvent event) {
            if (!(event.getEntity() instanceof Snowball)) return;
            if (!(event.getEntity().getShooter() instanceof Player)) return;
            if (!event.getEntity().hasMetadata("splat")) return;
            if (!participants.contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            if (disqualified.contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            Location bottomLeft = event.getEntity().getLocation().clone().subtract(1, 1, 1);
            Location topRight = event.getEntity().getLocation().clone().add(1, 1, 1);
            byte bit = (byte) main.main().rng().nextInt(15);
            // Search nearby blocks.
            for (int x = bottomLeft.getBlockX(); x <= topRight.getBlockX(); x++)
                for (int y = bottomLeft.getBlockY(); y <= topRight.getBlockY(); y++)
                    for (int z = bottomLeft.getBlockZ(); z <= topRight.getBlockZ(); z++) {
                        Block found = event.getEntity().getWorld().getBlockAt(x, y, z);
                        if (found.getType().isBlock() && !found.isLiquid() && found.getType() != Material.AIR && found.getType() != Material.STAINED_CLAY && !found.isBlockPowered() && !(found.getState() instanceof InventoryHolder)
                                && found.getRelative(BlockFace.DOWN).getType() != Material.SOIL && found.getType() != Material.BANNER && found.getType() != Material.SKULL && !found.getType().toString().contains("REDSTONE") &&
                                !found.getType().toString().contains("SIGN") && found.getType() != Material.MOB_SPAWNER) {
                            scorePoints((Player) event.getEntity().getShooter(), false, 1);
                            placed.put(found.getLocation(), found.getType());
                            placed2.put(found.getLocation(), found.getData());
                            found.setType(Material.STAINED_CLAY);
                            found.setData(bit);
                        }
                    }
        }
    }
}
