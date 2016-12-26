package mx.qcode.testfilecamera.Utils.Camera;

/**
 * Created by FerCho on 22/12/16.
 */

public interface CameraSupport {
    CameraSupport open(int cameraId);
    int getOrientation(int cameraId);
}
