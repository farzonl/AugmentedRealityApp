package Comparch.augmentedreality.effects;

import org.opencv.core.Mat;


public class IdentityEffect implements Effect {

	@Override
	public Mat applyTo(Mat frame) {
		return frame;
	}

}
