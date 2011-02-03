package project2;

import java.io.IOException;

import sun.util.LocaleServiceProviderPool.LocalizedObjectGetter;

import com.bulletphysics.linearmath.QuaternionUtil;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

public class ThirdPersonCamera implements ActionListener, AnalogListener,
        Control {
    private final Camera cam;
    private Spatial target;
    private final Vector3f initialUpVec;

    private float rotationZ;
    private float rotationXY;
    private float distance;

    // private final float rotationSpeed;

    public ThirdPersonCamera(final Camera cam, Spatial target,
            InputManager inputManager) {
        this.cam = cam;
        this.target = target;
        initialUpVec = new Vector3f(0, 0, 1);
        distance = 10.0f;

        target.addControl(this);
        registerWithInput(inputManager);
    }

    /**
     * Registers inputs with the input manager
     * 
     * @param inputManager
     */
    public void registerWithInput(InputManager inputManager) {
        String[] inputs = { "toggleRotate", "Down", "Up", "mouseLeft",
                "mouseRight", "ZoomIn", "ZoomOut" };

        inputManager.addMapping("Down", new MouseAxisTrigger(1, true));
        inputManager.addMapping("Up", new MouseAxisTrigger(1, false));
        inputManager.addMapping("ZoomIn", new MouseAxisTrigger(2, true));
        inputManager.addMapping("ZoomOut", new MouseAxisTrigger(2, false));
        inputManager.addMapping("mouseLeft", new MouseAxisTrigger(0, true));
        inputManager.addMapping("mouseRight", new MouseAxisTrigger(0, false));
        inputManager.addMapping("toggleRotate", new MouseButtonTrigger(
                MouseInput.BUTTON_LEFT));
        inputManager.addMapping("toggleRotate", new MouseButtonTrigger(
                MouseInput.BUTTON_RIGHT));

        inputManager.addListener(this, inputs);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
    }

    private void rotateCamera(float value) {
        rotationZ += value * 10.0f;
    }

    private void vRotateCamera(float value) {
        rotationXY += value * 10.0f;

        if (rotationXY > FastMath.HALF_PI) {
            rotationXY = FastMath.HALF_PI - 0.001f;
        }

        if (rotationXY < -FastMath.HALF_PI) {
            rotationXY = -FastMath.HALF_PI + 0.001f;
        }
    }

    private void zoomCamera(float value) {
        distance += value;
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("mouseLeft")) {
            rotateCamera(-value);
        } else if (name.equals("mouseRight")) {
            rotateCamera(value);
        } else if (name.equals("Up")) {
            vRotateCamera(value);
        } else if (name.equals("Down")) {
            vRotateCamera(-value);
        } else if (name.equals("ZoomIn")) {
            zoomCamera(value);
        } else if (name.equals("ZoomOut")) {
            zoomCamera(-value);
        }

    }

    @Override
    public void write(JmeExporter ex) throws IOException {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        target = spatial;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void update(float tpf) {
        Quaternion b = new Quaternion().fromAngleAxis(rotationZ, new Vector3f(
                0, 0, 1));
        Vector3f afterZ = b.mult(new Vector3f(0, 1, 0).multLocal(distance));

        final Vector3f left = afterZ.cross(initialUpVec);

        Quaternion a = new Quaternion().fromAngleAxis(rotationXY, left);
        final Vector3f newDir = a.mult(afterZ);

        Vector3f pos = newDir.add(target.getWorldTranslation());
        cam.setLocation(pos);
        cam.lookAt(target.getWorldTranslation(), initialUpVec);
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

}
