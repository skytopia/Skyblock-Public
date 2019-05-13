package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;

import java.util.List;
import java.util.UUID;

/**
 * Supports "attempt-based" minigames, similar to turn based minigames.
 * <ul>
 * <li>Timer: counts up from zero.</li>
 * <li>Score: starts at zero, increases with amount of attempts.</li>
 * <li>Medals: gain medals as amount of attempts increases.</li>
 * <li>Gameover happens as defined in each minigame.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 3.0
 * @since 3.0
 */
public abstract class AttemptsMinigameTask extends TurnBasedMinigameTask {

    public AttemptsMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty, int maxGoes) {
        super(owner, minigame, participants, main, difficulty, maxGoes);
    }

    @Override
    public boolean isHighscoreCheckable() {
        return false;
    }
}
