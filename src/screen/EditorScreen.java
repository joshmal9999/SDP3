package screen;

import engine.Core;
import engine.InputManager;
import engine.SoundManager;
import engine.Sound;
import engine.Cooldown;

import java.awt.event.KeyEvent;

/**
 * Implements the editor screen.
 */
public class EditorScreen extends Screen {

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Selected row. */
    private int selectedRow;
    /** Total number of rows for selection. */
    private static final int TOTAL_ROWS = 2; // Playable Ship Features, Enemy Ship Features
    /** Time between changes in user selection. */
    private final Cooldown selectionCooldown;
    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

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
    public EditorScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.selectedRow = 0;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
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
        if (this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                this.selectedRow = (this.selectedRow - 1 + TOTAL_ROWS) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                this.selectedRow = (this.selectedRow + 1) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if (this.selectedRow == 0) {
                    // Playable Ship Features
                    this.returnCode = 10; // Set return code for PlayableShipEditor
                    this.isRunning = false;
                } else if (this.selectedRow == 1) {
                    // Enemy Ship Features
                    drawManager.drawCenteredBigString(this, "Enemy Ship Features", getHeight() / 2);
                }
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_CLICK);
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                // Return to main menu or previous screen.
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

        drawManager.drawEditor(this);

        // Draw buttons
        String[] options = {"Playable Ship Features", "Enemy Ship Features"};
        for (int i = 0; i < TOTAL_ROWS; i++) {
            drawManager.drawCenteredRegularString(this, options[i], getHeight() / 2 + i * 40, selectedRow == i);
        }

        drawManager.completeDrawing(this);
    }
    /**
     * Determines if a given string is a palindrome.
     *
     * @param text The string to check.
     * @return True if the string is a palindrome, false otherwise.
     */
    public boolean isPalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Determines if a given year is a leap year.
     *
     * @param year The year to check.
     * @return True if the year is a leap year, false otherwise.
     */
    public boolean isLeapYear(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return year % 400 == 0;
            } else {
                return true;
            }
        }
        return false;
    }
}