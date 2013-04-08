package Comparch.augmentedreality.effects;

import org.opencv.core.Mat;

public interface Effect {
	public Mat applyTo(Mat frame);
}
