package project2;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

public abstract class GameApplication extends Application {

    protected Node rootNode = new Node("Root Node");
    protected Node guiNode = new Node("Gui Node");

    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;

    protected FlyByCamera flyCam;
    protected boolean showSettings = false;

    private final AppActionListener actionListener = new AppActionListener();

    private class AppActionListener implements ActionListener {
        @Override
        public void onAction(final String name, final boolean value,
                final float tpf) {
            if (!value) {
                return;
            }

            if (name.equals("FULL_SCREEN")) {
                try {
                    Display.setFullscreen(!settings.isFullscreen());
                } catch (final LWJGLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (name.equals("SIMPLEAPP_Exit")) {
                stop();
            } else if (name.equals("SIMPLEAPP_CameraPos")) {
                if (cam != null) {
                    final Vector3f loc = cam.getLocation();
                    final Quaternion rot = cam.getRotation();
                    System.out.println("Camera Position: (" + loc.x + ", "
                            + loc.y + ", " + loc.z + ")");
                    System.out.println("Camera Rotation: " + rot);
                    System.out.println("Camera Direction: "
                            + cam.getDirection());
                }
            } else if (name.equals("SIMPLEAPP_Memory")) {
                BufferUtils.printCurrentDirectMemory(null);
            }
        }
    }

    public GameApplication() {
        super();
    }

    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        if (settings == null) {
            setSettings(new AppSettings(true));
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings)) {
                return;
            }
        }

        super.start();
    }

    public FlyByCamera getFlyByCamera() {
        return flyCam;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    public void setShowSettings(final boolean showSettings) {
        this.showSettings = showSettings;
    }

    public void loadFPSText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        fpsText = new BitmapText(guiFont, false);
        fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        fpsText.setText("Frames per second");
        guiNode.attachChild(fpsText);
    }

    public void loadStatsView() {
        statsView = new StatsView("Statistics View", assetManager,
                renderer.getStatistics());
        // move it up so it appears above fps text
        statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        guiNode.attachChild(statsView);
    }

    public void registerCameraControls() {
        final String[] mappings = new String[] { "FLYCAM_Left", "FLYCAM_Right",
                "FLYCAM_Up", "FLYCAM_Down",

                "FLYCAM_StrafeLeft", "FLYCAM_StrafeRight", "FLYCAM_Forward",
                "FLYCAM_Backward",

                "FLYCAM_ZoomIn", "FLYCAM_ZoomOut", "FLYCAM_RotateDrag",

                "FLYCAM_Rise", "FLYCAM_Lower" };

        inputManager.addMapping("FLYCAM_Left", new MouseAxisTrigger(0, true));

        inputManager.addMapping("FLYCAM_Right", new MouseAxisTrigger(0, false));

        inputManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(1, false));

        inputManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(1, true));

        // mouse only - zoom in/out with wheel, and rotate drag
        inputManager
                .addMapping("FLYCAM_ZoomIn", new MouseAxisTrigger(2, false));
        inputManager
                .addMapping("FLYCAM_ZoomOut", new MouseAxisTrigger(2, true));
        inputManager.addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(
                MouseInput.BUTTON_LEFT));

        // keyboard only WASD for movement and WZ for rise/lower height
        inputManager.addMapping("FLYCAM_StrafeLeft", new KeyTrigger(
                KeyInput.KEY_A));
        inputManager.addMapping("FLYCAM_StrafeRight", new KeyTrigger(
                KeyInput.KEY_D));
        inputManager.addMapping("FLYCAM_Forward",
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("FLYCAM_Backward", new KeyTrigger(
                KeyInput.KEY_S));
        inputManager.addMapping("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_Z));

        inputManager.addListener(flyCam, mappings);
        inputManager.setCursorVisible(false);
    }

    @Override
    public void initialize() {
        super.initialize();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        loadFPSText();
        loadStatsView();
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        if (inputManager != null) {
            flyCam = new FlyByCamera(cam);
            flyCam.setMoveSpeed(5f);
            registerCameraControls();

            if (context.getType() == Type.Display) {
                inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(
                        KeyInput.KEY_ESCAPE));
            }

            // set full screen switch
            inputManager.addMapping("FULL_SCREEN", new KeyTrigger(
                    KeyInput.KEY_K));
            inputManager.addMapping("SIMPLEAPP_CameraPos", new KeyTrigger(
                    KeyInput.KEY_C));
            inputManager.addMapping("SIMPLEAPP_Memory", new KeyTrigger(
                    KeyInput.KEY_M));
            inputManager.addListener(actionListener, "SIMPLEAPP_Exit",
                    "SIMPLEAPP_CameraPos", "SIMPLEAPP_Memory", "FULL_SCREEN");

        }

        init();
    }

    @Override
    public void update() {
        if (speed == 0 || paused) {
            return;
        }

        super.update();
        final float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        final int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f) {
            fpsText.setText("Frames per second: " + fps);
            secondCounter = 0.0f;
        }

        // update states
        stateManager.update(tpf);

        // simple update and root node
        update(tpf);
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf);
        render(renderManager);
        stateManager.postRender();
    }

    public abstract void init();

    public void update(final float tpf) {
    }

    public void render(final RenderManager rm) {
    }

}
