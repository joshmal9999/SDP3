package screen;

import engine.Core;
import engine.InputManager;
import engine.SoundManager;
import engine.Sound;

import java.awt.Color;
import java.awt.event.KeyEvent;

/**
 * Implements the playable ship editor screen.
 */
public class PlayableShipEditor extends Screen {

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Canvas for editing the sprite. */
    private boolean[][] canvas;
    /** Pixel size for drawing. */
    private static final int PIXEL_SIZE = 20;
    /** Current selected pixel position. */
    private int selectedX = 0;
    private int selectedY = 0;

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
        this.canvas = new boolean[13][8];
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
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                selectedY = (selectedY - 1 + canvas[0].length) % canvas[0].length;
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                selectedY = (selectedY + 1) % canvas[0].length;
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                selectedX = (selectedX - 1 + canvas.length) % canvas.length;
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                selectedX = (selectedX + 1) % canvas.length;
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_ENTER)) {
                canvas[selectedX][selectedY] = !canvas[selectedX][selectedY]; // Toggle pixel color
                soundManager.playSound(Sound.MENU_CLICK);
            }

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

        // Draw canvas
        drawCanvas();

        drawManager.completeDrawing(this);
    }

    /**
     * Draws the canvas for sprite editing.
     */
    private void drawCanvas() {
        int startX = (getWidth() - (canvas.length * PIXEL_SIZE)) / 2;
        int startY = getHeight() / 2;

        for (int x = 0; x < canvas.length; x++) {
            for (int y = 0; y < canvas[x].length; y++) {
                if (x == selectedX && y == selectedY) {
                    drawManager.getBackBufferGraphics().setColor(Color.RED); // Highlight selected pixel
                } else {
                    drawManager.getBackBufferGraphics().setColor(canvas[x][y] ? Color.BLACK : Color.WHITE);
                }
                drawManager.getBackBufferGraphics().fillRect(startX + x * PIXEL_SIZE, startY + y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                drawManager.getBackBufferGraphics().setColor(Color.GRAY);
                drawManager.getBackBufferGraphics().drawRect(startX + x * PIXEL_SIZE, startY + y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }
} 