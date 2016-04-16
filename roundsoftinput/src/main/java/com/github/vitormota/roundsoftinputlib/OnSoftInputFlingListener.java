package com.github.vitormota.roundsoftinputlib;

/**
 * Created by vitor on 11/03/2016.
 *
 * Interface for easy override of fling interactions
 */
public interface OnSoftInputFlingListener {

	void onUpFling();
	void onDownFling();
	void onLeftFling();
	void onRightFling();
}
