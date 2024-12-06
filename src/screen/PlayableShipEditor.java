package screen;

import engine.Core;
import engine.InputManager;
import engine.SoundManager;
import engine.Sound;
import engine.Cooldown;
import engine.FileManager;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

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
    /** Cooldown for input to prevent rapid key presses. */
    private final Cooldown inputCooldown = Core.getCooldown(200); // 200 milliseconds cooldown

    /** Selected row. */
    private int selectedRow;
    /** Total number of rows for selection. */
    private static final int TOTAL_ROWS = 3; // Canvas, Save, Clear
    /** Time between changes in user selection. */
    private final Cooldown selectionCooldown;
    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Flag to indicate if in button selection mode. */
    private boolean inButtonMode = false;

    /** Logger for logging messages */
    private static final Logger logger = Core.getLogger();

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
        this.selectedRow = 0; // Start with Canvas selected
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        loadCustomShip(); // Load custom ship data on initialization
    }

    /**
     * Loads the custom ship data from the graphics file.
     */
    private void loadCustomShip() {
        try (BufferedReader reader = FileManager.getInstance().loadGraphics()) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# CustomShip")) {
                    line = reader.readLine(); // Read the actual sprite data
                    if (line != null) {
                        for (int i = 0; i < canvas.length; i++) {
                            for (int j = 0; j < canvas[i].length; j++) {
                                canvas[i][j] = line.charAt(i * canvas[i].length + j) == '1';
                            }
                        }
                        logger.info("Loading custom ship.");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (inButtonMode) {
                handleButtonInput();
            } else {
                handleCanvasInput();
            }

            // Handle ESC key to return to EditorScreen
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.returnCode = 9; // Set return code for EditorScreen
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
            }
        }
    }

    /**
     * Handles input for canvas editing.
     */
    private void handleCanvasInput() {
        if (this.inputCooldown.checkFinished()) {
            selectedRow = 0; // Keep Canvas selected while editing pixels
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                selectedY = (selectedY - 1 + canvas[0].length) % canvas[0].length;
                soundManager.playSound(Sound.MENU_MOVE);
                inputCooldown.reset();
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                if (selectedY == canvas[0].length - 1) {
                    inButtonMode = true; // Switch to button mode
                    selectedRow = 1; // Start with Save selected
                    selectionCooldown.reset(); // Reset cooldown when switching modes
                } else {
                    selectedY = (selectedY + 1) % canvas[0].length;
                }
                soundManager.playSound(Sound.MENU_MOVE);
                inputCooldown.reset();
            } else if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                selectedX = (selectedX - 1 + canvas.length) % canvas.length;
                soundManager.playSound(Sound.MENU_MOVE);
                inputCooldown.reset();
            } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                selectedX = (selectedX + 1) % canvas.length;
                soundManager.playSound(Sound.MENU_MOVE);
                inputCooldown.reset();
            } else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                canvas[selectedX][selectedY] = !canvas[selectedX][selectedY]; // Toggle pixel color
                soundManager.playSound(Sound.MENU_CLICK);
                inputCooldown.reset();
            }
        }
    }

    /**
     * Handles input for button selection.
     */
    private void handleButtonInput() {
        if (this.selectionCooldown.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                if (selectedRow == 1) {
                    inButtonMode = false; // Switch back to canvas mode
                    selectedY = canvas[0].length - 1;
                } else {
                    this.selectedRow = (this.selectedRow - 1 + TOTAL_ROWS) % TOTAL_ROWS;
                }
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                this.selectedRow = (this.selectedRow + 1) % TOTAL_ROWS;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                if (this.selectedRow == 1) {
                    saveSpriteData();
                } else if (this.selectedRow == 2) {
                    clearCanvas();
                }
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_CLICK);
            }
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        // Draw the title for custom playable ships
        drawManager.drawPlayableShips(this);

        // Draw Canvas button above the canvas
        drawManager.drawCenteredRegularString(this, "Canvas", getHeight() / 4 + 100, selectedRow == 0);

        // Draw canvas
        drawCanvas();

        // Draw Save and Clear buttons below the canvas
        String[] options = {"Save", "Clear"};
        for (int i = 0; i < options.length; i++) {
            drawManager.drawCenteredRegularString(this, options[i], getHeight() / 2 + i * 40 + 220, selectedRow == i + 1);
        }

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
                    drawManager.getBackBufferGraphics().setColor(Color.BLACK); // Highlight selected pixel
                } else {
                    drawManager.getBackBufferGraphics().setColor(canvas[x][y] ? Color.GREEN : Color.WHITE);
                }
                drawManager.getBackBufferGraphics().fillRect(startX + x * PIXEL_SIZE, startY + y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                drawManager.getBackBufferGraphics().setColor(Color.GRAY);
                drawManager.getBackBufferGraphics().drawRect(startX + x * PIXEL_SIZE, startY + y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }

    /**
     * Saves the current canvas sprite data to the graphics file.
     */
    private void saveSpriteData() {
        StringBuilder spriteData = new StringBuilder();
        for (boolean[] row : canvas) {
            for (boolean pixel : row) {
                spriteData.append(pixel ? '1' : '0');
            }
        }
        try {
            FileManager.getInstance().updateShipGraphics("CustomShip", spriteData.toString());
            System.out.println("Sprite data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the canvas, setting all pixels to white.
     */
    private void clearCanvas() {
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                canvas[i][j] = false;
            }
        }
        System.out.println("Canvas cleared.");
    }
}
