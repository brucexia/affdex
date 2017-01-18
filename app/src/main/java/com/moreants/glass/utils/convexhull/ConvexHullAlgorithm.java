package com.moreants.glass.utils.convexhull;
import android.graphics.PointF;

import java.util.ArrayList;

public interface ConvexHullAlgorithm 
{
	ArrayList<PointF> execute(ArrayList<? extends PointF> PointFs);
}
