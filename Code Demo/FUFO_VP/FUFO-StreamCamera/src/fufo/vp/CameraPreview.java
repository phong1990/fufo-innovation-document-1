package fufo.vp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback, PreviewCallback {
   //data buffer of picture to stream
    byte[] picBuffer;
    
    public Client client;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private String TAG = "CameraPreview.";
	// This variable is responsible for getting and setting the camera settings
	private Parameters parameters;
	// this variable stores the camera preview size
	private Size previewSize;
	// this array stores the pixels as hexadecimal pairs
	// The file name of NV21 byte array:
	private String filename = "sample_jpeg_Q50_320x240.";
	// the number of the outfile:
	private int fileNumber = 0;
	// this timestamp is to calculate the time gap of processing each frame
	// buffer
	private long timestamp = 0;

	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		 client = new Client();
         client.creatClient();
	}

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			// mCamera.startPreview();
			// sets the camera callback to be the one defined in this class
			mCamera.setPreviewCallback(this);

			parameters = mCamera.getParameters();
			// get preview size for calculation of the back buffer
			// compute the back buffer size according to the preview format. In
			// this case we use NV21 format
			// so the following formula should be equal to ( width x height x 3
			// / 2 )
			previewSize = mCamera.getParameters().getPreviewSize();
			int dataBufferSize = (int) (previewSize.height * previewSize.width * (ImageFormat
					.getBitsPerPixel(mCamera.getParameters().getPreviewFormat()) / 8.0));
			mCamera.setParameters(parameters);
			Log.v("CameraTest", "dataBufferSize = " + dataBufferSize);

		} catch (IOException e) {
			if (mCamera != null) {
				// handle the exception here
				e.printStackTrace();
			}
		}
	}

	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
	}

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		// parameters.setPreviewSize(w, h);
		// set the camera's settings
		// mCamera.setParameters(parameters);

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG
					+ "surfaceChanged(SurfaceHolder holder, int format, int w, int h)",
					"Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.v("CameraTest", "Time Gap = "
				+ (System.currentTimeMillis() - timestamp));
		timestamp = System.currentTimeMillis();
		// NV21toRGB24Converter(pixels, data, previewSize.width,
		// previewSize.height);
		final YuvImage imgPreview = new YuvImage(data, ImageFormat.NV21,
				mCamera.getParameters().getPreviewSize().width, mCamera
						.getParameters().getPreviewSize().height, null);
		byte[] buffer;
		ByteArrayOutputStream jpegOutStream = new ByteArrayOutputStream();
		// Compress image into JPEG
		imgPreview.compressToJpeg(new Rect(0, 0, imgPreview.getWidth(),
				imgPreview.getHeight()), 50, jpegOutStream);

		// This buffer can be sent to another computer.
		buffer = jpegOutStream.toByteArray();

		// increase the file number
		fileNumber++;
		// Write the result in to file
		// writeToFile(buffer);
		 //Sendfile to Server
        Log.v("CameraTest", "package = "
                + fileNumber);
	   
	       
		    client.sendPackage(buffer);

	}
	
	/**
	 * This function accept a byte array and write it into a file.
	 * The file name = filename + fileNumber.
	 * The result directory = directory for picture in the file system.
	 */
	public void writeToFile(byte[] array) {
		try {
			// Log.d("file path", path);
			File root = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File fileToWrite = new File(root, filename + fileNumber+".jpeg");
			root.mkdirs();
			if (root.canWrite()) {
				Log.i("root", root.getAbsolutePath());
				OutputStream os = new FileOutputStream(fileToWrite);
				Log.i("path", fileToWrite.getAbsolutePath());
				os.write(array);
				os.close();
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Convert NV21 to RGB24. This function use the method in Avery Lee's JFIF
	 * Clarification
	 */
	void NV21toRGB24Converter(int[] rgb, byte[] NV21, int width, int height) {

		// frameSize in pixel = width * height
		final int frameSize = width * height;
		// run j from 0 to height. This j value will count the current row.
		// yp: is the Y value per pixel
		for (int j = 0, yp = 0; j < height; j++) {
			// j (or current row) will be divided by 2, and lower rounded.
			// then after multiplied by width, result will add to the frame size
			// to determine the next position
			// of value V. (followed by value U)
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			// increase yp to determine the next position of value Y.
			for (int i = 0; i < width; i++, yp++) {
				// read the byte Y, convert it into integer (by add 24 zeros
				// bits to the left)
				// then AND the result with 0000 0000 0000 0000 0000 0000 1111
				// 1111.
				// This process will make sure the value of Y is changed into
				// integer with no
				// damage.
				int y = (0xff & (NV21[yp]));
				if (y < 0)
					y = 0;
				// 1 = 0000 0000 0000 0000 0000 0000 0000 0001
				// i increase from 0 to width, if i is odd, (i & 1) will not
				// equal to 0;
				// if i is even, (i & 1) will equal to 0;
				if ((i & 1) == 0) {
					// read byte V into integer then increase uvp. The method
					// here is the same as
					// reading byte Y above.
					v = (0xff & NV21[uvp++]) - 128;
					// read byte U into integer then increase uvp. The method
					// here is the same as
					// reading byte Y above.
					u = (0xff & NV21[uvp++]) - 128;
				}

				// the following code is just algorithm in YUV to RGB
				// conversion.
				int r = (int) (y + 1.402f * v);
				int g = (int) (y - 0.344f * u - 0.714f * v);
				int b = (int) (y + 1.772f * u);
				// limit the value of R, G and B in the following inteval:
				// 0000 0000 0000 0000 0000 0000 0000 0000 <= r|g|b <= 0000 0000
				// 0000 0000 0000 0000 1111 1111
				// Reason is:
				// 1. for R value, when we shift 16 bits to the left (code
				// below) we can get 8 MSB of this
				// value, then discard the rest. The maximum value of these 8
				// MSB can be 1111 1111 or 255.
				// 2. for G value, when we shift 8 bits to the left, we can also
				// get the 8 MSB of this value
				// just like the case of R value.
				// 3. for B value, to get 8 MSB of it, we don't have to shift
				// any thing..
				//
				// This interval accept the loss of any value that above
				// maximum.
				if (r < 0)
					r = 0;
				else if (r > 255)
					r = 255;
				if (g < 0)
					g = 0;
				else if (g > 255)
					g = 255;
				if (b < 0)
					b = 0;
				else if (b > 255)
					b = 255;

				// the following formula will construct a pixel of RGB
				// in the following structure of hex number: aa rr gg bb
				// aa: is the alpha bits, in this case it is always equal to
				// 'ff'
				// rr: Red bits (8 MSB of the R value)
				// gg: Green bits (8 MSB of the G value)
				// bb: Blue bits (8 MSB of the B value)
				rgb[yp] = 0xff000000 | ((r << 16) & 0xff0000)
						| ((g << 8) & 0xff00) | (b & 0xff);
			}
		}
	}
}