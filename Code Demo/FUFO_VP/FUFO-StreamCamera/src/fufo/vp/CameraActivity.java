package fufo.vp;

import java.util.Iterator;
import java.util.List;

import fufo.vp.R.id;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

//import android.widget.Toast;

public class CameraActivity extends Activity {
    
	private Camera mCamera;
	private CameraPreview mPreview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// get the handle for this button
		Button captureButton = (Button) findViewById(id.button_capture);
		// Button checkButton = (Button) findViewById(id.button_check);
		// check if the system has a camera
		if (checkCameraHardware(this)) {
			// Create an instance of Camera
			mCamera = getCameraInstance();
			Log.d("aop", "da vao");
			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this, mCamera);
			Log.d("aop", "da vao2");
			FrameLayout preview = (FrameLayout) findViewById(id.camera_preview);
			preview.addView(mPreview);
			Log.d("aop", "da vao3");
			mCamera.setErrorCallback(new ErrorCallback() {
				@Override
                public void onError(int error, Camera camera) {
					// do absolutely nothing!
				}
			});

			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();
			List<int[]> supportedPreviewFps = params
					.getSupportedPreviewFpsRange();
			Iterator<int[]> supportedPreviewFpsIterator = supportedPreviewFps
					.iterator();
			while (supportedPreviewFpsIterator.hasNext()) {
				int[] tmpRate = supportedPreviewFpsIterator.next();
				StringBuffer sb = new StringBuffer();
				sb.append("supportedPreviewRate: ");
				for (int i = tmpRate.length, j = 0; j < i; j++) {
					sb.append(tmpRate[j] + ", ");
				}
				Log.v("CameraTest", sb.toString());
			}
			// List the supported Preview Sizes
			List<Size> supportedPreviewSizes = params
					.getSupportedPreviewSizes();
			Iterator<Size> supportedPreviewSizesIterator = supportedPreviewSizes
					.iterator();
			while (supportedPreviewSizesIterator.hasNext()) {
				Size tmpSize = supportedPreviewSizesIterator.next();
				Log.v("CameraTest", "supportedPreviewSize.width = "
						+ tmpSize.width + "supportedPreviewSize.height = "
						+ tmpSize.height);
			}

			// Just show the current framerate for fun
			Log.v("CameraTest", "Camera PreviewFrameRate = "
					+ mCamera.getParameters().getPreviewFrameRate());

			// Start preview
			mCamera.startPreview();

			// Add a listener to the Capture button
			captureButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Stop preview first
					mCamera.stopPreview();
					// release camera
					releaseCamera();
				}
			});
		} else {
			// disable the button
			captureButton.setClickable(false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// check if the system has a camera
		if (checkCameraHardware(this)) {
			// in case user forget to push the stop button
			if (mCamera != null) {
				// Stoppreview first
				mCamera.stopPreview();
				// release camera
				releaseCamera();
			}
		}
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/** Release the camera **/
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

}