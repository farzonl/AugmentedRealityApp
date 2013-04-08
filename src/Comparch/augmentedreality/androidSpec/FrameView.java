package Comparch.augmentedreality.androidSpec;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FrameView extends SurfaceView {
	private static final String TAG = "FrameView";

	private SurfaceHolder mHolder;

	public FrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();

		//Log.i(TAG, "Instantiated new " + this.getClass());
	}

	public void setFrame(Mat frame) {
		//Log.i(TAG, "Setting frame");
		Bitmap bmp = Bitmap.createBitmap(frame.cols(), frame.rows(),
				Bitmap.Config.ARGB_8888);

		try {
			Utils.matToBitmap(frame, bmp);

			Canvas canvas = mHolder.lockCanvas();
			if (canvas != null) {
				canvas.drawBitmap(bmp,
						(canvas.getWidth() - bmp.getWidth()) / 2,
						(canvas.getHeight() - bmp.getHeight()) / 2, null);
				mHolder.unlockCanvasAndPost(canvas);
			}
		} catch (Exception e) {
			Log.e(TAG,
					"Utils.matToBitmap() throws an exception: "
							+ e.getMessage());
		} finally {
			bmp.recycle();
		}
	}
}