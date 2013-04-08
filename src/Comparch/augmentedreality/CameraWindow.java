package Comparch.augmentedreality;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import Comparch.augmentedreality.androidSpec.FrameView;
import Comparch.augmentedreality.effects.*;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;


public class CameraWindow extends Activity {

	private static final String TAG = "CameraWindowActivity";
	private static int RESULT_LOAD_IMAGE = 1;
	private FrameView mView;
	private FrameView mPic;
	Thread imageProcessingThread;
	ImageProcessorTask imageProcessingTask;

	MenuItem mItemNone;
	MenuItem mItemGrayScale;
	MenuItem mChessBoard;
	Mat pic;
	Button button;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_camera_window);

		mView = (FrameView) findViewById(R.id.frameView);
		mPic = (FrameView) findViewById(R.id.frameView2);
		addListenerOnButton();
		addListenerOnButton2();

	}
	
	public void addListenerOnButton() {
		 
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
		//button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
				pic = imageProcessingTask.frame.clone();
				Imgproc.resize(pic,pic,new Size(mPic.getWidth(),mPic.getHeight()));
				mPic.setFrame(pic);
 
			}
 
		});
	}
	
	public void addListenerOnButton2() {
		 
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new View.OnClickListener() {
		//button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
				
				//Intent imageIntent = new Intent(getBaseContext(), Comparch.augmentedreality.ImageGalleryActivity.class);     
				//startActivity(imageIntent);
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 
                startActivityForResult(i, RESULT_LOAD_IMAGE);
 
			}
 
		});
 
	}
	
	   @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	         
	        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            //pic = new Mat();
	            //Utils.bitmapToMat(BitmapFactory.decodeFile(picturePath), pic);
	            Log.i(TAG, "picture path is" + picturePath);
	            pic = Highgui.imread(picturePath).clone();
	            Log.i(TAG, "picture cols: " + pic.cols()+"picture rows: " + pic.rows());
	            //mPic = (FrameView) findViewById(R.id.frameView2);
	            mPic.setFrame(pic);
	           
	        }
	     
	     
	    }
	
	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		imageProcessingTask.setRunning(false);
		try {
			imageProcessingThread.join();
		} catch (InterruptedException e) {
		}
		imageProcessingThread = null;
		imageProcessingTask = null;
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		imageProcessingTask = new ImageProcessorTask("main_pipeline", mView);
		imageProcessingThread = new Thread(imageProcessingTask);
		imageProcessingThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.i(TAG, "onCreateOptionsMenu");
		mItemNone = menu.add("None");
		mItemGrayScale = menu.add("Grayscale");
		mChessBoard = menu.add("ChessBoard");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "Menu Item selected " + item);
		if (imageProcessingTask != null) {
			if (item == mItemNone) {
				imageProcessingTask.setEffect(new IdentityEffect());
			} else if (item == mItemGrayScale) {
				imageProcessingTask.setEffect(new GrayscaleEffect());
			} else if(item == mChessBoard) {
				imageProcessingTask.setEffect(new CheckerBoardDetect(pic));
			}
		}
		return true;
	}
}
