package screen;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;

/**
 * Implements the editor screen.
 */
public class EditorScreen extends Screen {

    /** Time between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    /** Cooldown for the selection change. */
    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width Screen width.
     * @param height Screen height.
     * @param fps Frames per second, frame rate at which the game is run.
     */
    public EditorScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public int run() {
        super.run();

        while (true) {
            draw();
            if (this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()) {
                if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                    return 1; // Return to main menu
                }
                // Add more key handling logic here for editor functionality
            }
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredBigString(this, "Editor Screen", this.height / 2);
        drawManager.completeDrawing(this);
    }
} 