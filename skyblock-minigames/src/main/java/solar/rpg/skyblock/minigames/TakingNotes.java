package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.*;
import solar.rpg.skyblock.minigames.tasks.AttemptsMinigameTask;

import java.util.*;

public class TakingNotes extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new TakingNotesTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Taking Notes";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.NOTE_BLOCK);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Note blocks are playing! Remember the order!",
                ChatColor.ITALIC + "The order gets longer with every correct attempt!",
                ChatColor.ITALIC + "♪♪♪ A longer order means a better score! ♪♪♪",
                "\"Make sure you.. take note of the order\"",
                "\"*ba dum tss* (⌐■_■)\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Play notes in the correct order!";
    }

    @Override
    public String getObjectiveWord() {
        return "notes in a row";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getGold() {
        return 25;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 50;
    }

    @Override
    public int getMaxReward() {
        return 10000;
    }

    private class TakingNotesTask extends AttemptsMinigameTask {

        /* Holds all four note blocks and their corresponding indexes. */
        private HashMap<Block, Short> clickable;

        /* The correct order for the note blocks to be played in. */
        private LinkedList<Short> order;

        /* The current combination the note blocks have been played in. */
        private LinkedList<Short> attempt;

        TakingNotesTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1);
        }

        /* The only thing that should end the game is pressing a wrong note. */
        @Override
        public boolean ascendingTimer() {
            return true;
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();
            order = new LinkedList<>();
            attempt = new LinkedList<>();
            canMove = false;

            /* The generated location where the minigame will play. */
            Location gen = generateLocation(100, 20, 140, true, false);

            if (!isEmpty(gen, 10, 6, 9)) {
                error();
                return;
            }

            makePlatform(gen, 10, 9, Material.STONE_BRICKS);

            for (int x = 1; x <= 4; x++)
                registerClicks(new Location(gen.getWorld(), gen.getBlockX() + (x * 2), gen.getBlockY() + 1, gen.getBlockZ() + 7), x);

            // Teleports players on to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(5, 2, 3));

            playNextTone();
        }

        /**
         * If the tone was successfully repeated, play it again.
         * Adds an extra random note to the end, increasing complexity over time.
         */
        private void playNextTone() {
            order.add((short) (main.main().rng().nextInt(4) + 1));

            // No moves can be made while the tone is being played back.
            canMove = false;
            titleParticipants("", ChatColor.GOLD + "Get ready!");

            // Play each note block at a delay.
            // Normal mode: every 3/4 of a second.
            // Harder mode: every 1/4 of a second.
            new BukkitRunnable() {
                int currentlyAt = -1;

                public void run() {
                    currentlyAt++;

                    clickable.entrySet().stream().filter(entry ->
                            Objects.equals(entry.getValue(), order.get(currentlyAt))).findFirst().ifPresent(entry ->
                            ((NoteBlock) entry.getKey().getState()).play());

                    if (currentlyAt + 1 == order.size()) {
                        this.cancel();
                        Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                            // After the sequence has been played, the next player can make their move.
                            selectPlayer();
                            canMove = true;
                        }, 35L);
                    }
                }
            }.runTaskTimer(main.main().plugin(), 80L, difficulty == Difficulty.HARDER ? 5L : 15L);
        }

        /**
         * When the selected player plays a note block.
         * Checks if it is the right order, game over otherwise.
         *
         * @param block  The block that was clicked.
         * @param player The player that clicked the block.
         */
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
                scorePoints(player, true, 1);
                titleParticipants("", ChatColor.GREEN + "Nice work! Keep going!");
                Bukkit.getScheduler().runTaskLater(main.main().plugin(), this::playNextTone, 30L);
            }
        }

        /**
         * Registers locations with note blocks so clicks can be listened for.
         *
         * @param loc        Location of a clickable note block.
         * @param allocation Index allocation for the note block.
         */
        private void registerClicks(Location loc, int allocation) {
            clickable.put(loc.getBlock(), (short) allocation);
            placed.add(loc.getBlock());
            Block bl = loc.getBlock();
            bl.getRelative(BlockFace.DOWN).setType(Material.GRASS_BLOCK);
            bl.setType(Material.NOTE_BLOCK);
            NoteBlock nbl = (NoteBlock) bl.getState();
            nbl.setNote(new Note(allocation * 3));
            nbl.update(true);
        }

        @Override
        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
            attempt.clear();
            attempt = null;
            order.clear();
            order = null;
        }

        @Override
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
