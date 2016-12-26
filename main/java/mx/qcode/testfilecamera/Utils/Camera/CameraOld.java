package mx.qcode.testfilecamera.Utils.Camera;

import android.hardware.Camera;

/**
 * Created by FerCho on 22/12/16.
 */

@SuppressWarnings("deprecation")
public class CameraOld implements CameraSupport {

    private Camera camera;

    @Override
    public CameraSupport open(final int cameraId) {
        this.camera = Camera.open(cameraId);
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }
}
