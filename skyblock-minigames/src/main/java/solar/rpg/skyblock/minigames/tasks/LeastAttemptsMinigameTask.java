package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;

import java.util.List;
import java.util.UUID;

/**
 * Supports "least attempt-based" minigames, similar to attempt-based minigames.
 * <ul>
 * <li>Timer: counts up from zero.</li>
 * <li>Score: starts at a predefined number, increases as attempts are made.</li>
 * <li>Medals: start with best medal, lose medals as amount of attempts increases.</li>
 * <li>Gameover happens as defined in each minigame, or when score reaches zero.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 1.1
 * @since 1.0
 */
public abstract class LeastAttemptsMinigameTask extends TurnBasedMinigameTask {

    public LeastAttemptsMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty, int maxGoes, int startingPoints) {
        super(owner, minigame, participants, main, difficulty, maxGoes);
        setStartingPoints(startingPoints);
    }

    @Override
    public boolean isHighscoreCheckable() {
        return false;
    }

    @Override
    public boolean isMedalAchieved() {
        return false;
    }
}
