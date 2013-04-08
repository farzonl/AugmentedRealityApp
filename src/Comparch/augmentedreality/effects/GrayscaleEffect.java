package Comparch.augmentedreality.effects;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class GrayscaleEffect implements Effect {

	@Override
	public Mat applyTo(Mat frame) {
		Mat newFrame = new Mat();
		Imgproc.cvtColor(frame, newFrame, Imgproc.COLOR_RGBA2GRAY);
		
		return newFrame;
	}

}
