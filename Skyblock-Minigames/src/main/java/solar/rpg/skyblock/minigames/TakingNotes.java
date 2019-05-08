package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.AttemptsMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.Minigame;

import java.util.*;

public class TakingNotes extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MemoryRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Taking Notes";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.NOTE_BLOCK);
    }

    public String[] getDescription() {
        return new String[]{"Hitting those high notes in order! Nice.",
                ChatColor.ITALIC + "Note blocks are playing! Remember the order!",
                ChatColor.ITALIC + "The order gets longer with every correct attempt!",
                ChatColor.ITALIC + "♪♪♪ A longer order means a better score! ♪♪♪"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Play notes in the correct order!";
    }

    public String getObjectiveWord() {
        return "notes in a row";
    }

    public int getDuration() {
        return 0;
    }

    public int getGold() {
        return 25;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getFlawless() {
        return 50;
    }

    public int getMaxReward() {
        return 10000;
    }

    private class MemoryRun extends AttemptsMinigameTask implements Listener {

        // Blocks and what notes they belong to.
        private HashMap<Block, Short> clickable;
        private Location gen;
        private ArrayList<Short> attempt;
        private ArrayList<Short> order;
        MemoryRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }
        // [0] is the first note, order increases.

        @Override
        public boolean ascendingTimer() {
            return true;
        }

        public void onStart() {
            clickable = new HashMap<>();
            order = new ArrayList<>();
            attempt = new ArrayList<>();
            canMove = false;

            gen = generateLocation(100, 20, 140, true, false);

            for (int x = 0; x <= 10; x++)
                for (int z = 0; z <= 9; z++)
                    if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z).getType() != Material.AIR) {
                        error();
                        return;
                    }

            for (int x = 0; x <= 10; x++)
                for (int z = 0; z <= 9; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            for (int x = 1; x <= 4; x++)
                registerClicks(new Location(gen.getWorld(), gen.getBlockX() + (x * 2), gen.getBlockY() + 1, gen.getBlockZ() + 7), x);

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 3));

            playNextTone();
        }

        private void playNextTone() {
            order.add((short) (main.main().rng().nextInt(4) + 1));

            canMove = false;
            titleParticipants("", ChatColor.GOLD + "Get ready!");

            new BukkitRunnable() {
                int currentlyAt = -1;

                public void run() {
                    currentlyAt++;

                    for (Map.Entry<Block, Short> entry : clickable.entrySet())
                        if (Objects.equals(entry.getValue(), order.get(currentlyAt))) {
                            ((NoteBlock) entry.getKey().getState()).play();
                            break;
                        }

                    if (currentlyAt + 1 == order.size()) {
                        this.cancel();
                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                            selectPlayer();
                            canMove = true;
                        }, 35L);
                    }
                }
            }.runTaskTimer(main.main().plugin(), 80L, difficulty == Difficulty.HARDER ? 5L : 15L);
        }

        private void play(Block block, Player player) {
            if (!clickable.containsKey(block)) return;

            int index = attempt.size();
            short played = clickable.get(block);

            attempt.add(played);

            if (!order.get(index).equals(played)) {
                canMove = false;
                main.messageAll(getParticipants(), ChatColor.RED + "That was not the correct order! You lose.");
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::stop, 60L);
            } else if (attempt.size() == order.size()) {
                canMove = false;
                attempt.clear();
                scorePoint(player, true, 1);
                titleParticipants("", ChatColor.GREEN + "Nice work! Keep going!");
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::playNextTone, 30L);
            }
        }

        /**
         * Registers clickable noteblocks. (and hides them)
         */
        private void registerClicks(Location loc, int allocation) {
            clickable.put(loc.getBlock(), (short) allocation);
            placed.add(loc.getBlock());
            Block bl = loc.getBlock();
            bl.getRelative(BlockFace.DOWN).setType(Material.GRASS);
            bl.setType(Material.NOTE_BLOCK);
            NoteBlock nbl = (NoteBlock) bl.getState();
            nbl.setNote(new Note(allocation * 3));
            nbl.update(true);
        }

        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            attempt.clear();
            attempt = null;
            order.clear();
            order = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.NOTE_BLOCK) return;
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
                if (isValidParticipant(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            if (canMove(event.getPlayer()))
                play(event.getClickedBlock(), event.getPlayer());
            else event.setCancelled(true);
        }
    }
}
