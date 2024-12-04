package screen;

import engine.Core;
import engine.InputManager;
import engine.SoundManager;
import engine.Sound;

import java.awt.event.KeyEvent;

/**
 * Implements the playable ship editor screen.
 */
public class PlayableShipEditor extends Screen {

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public PlayableShipEditor(final int width, final int height, final int fps) {
        super(width, height, fps);
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                // Return to editor screen.
                this.returnCode = 1;
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
            }
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        // Draw title
        drawManager.drawCenteredBigString(this, "Playable Ship Editor", getHeight() / 4);

        drawManager.completeDrawing(this);
    }
}