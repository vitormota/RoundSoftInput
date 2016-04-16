package com.github.vitormota.roundsoftinputlib;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by vitor on 07/03/2016.
 *
 * Model for screen key
 */
public class InputModel
		extends BaseObservable {


	private float mStartRad;
	private float mEndRad;

	private char mValue;
	private boolean mExpanded;

	private float mXPivotFraction;
	private float mYPivotFraction;

	public InputModel(final char value) {
		this.mValue = value;
	}

	/**
	 * Change the value case and return it
	 *
	 * @return the changed value, if possible to change the case
	 */
	public char toggleCase() {
		setValue(Character.isUpperCase(mValue) ?
				         Character.toLowerCase(mValue) :
				         Character.toUpperCase(mValue));
		return mValue;
	}

	@Override
	public String toString() {
		return mValue + " " + mStartRad + "," + mEndRad + " " + mExpanded + " " + mXPivotFraction + "," + mYPivotFraction;
	}

	///////////////////////////////////////////////////////////////////////////
	// Getters & Setters
	///////////////////////////////////////////////////////////////////////////

	public float getEndRad() {
		return mEndRad;
	}

	public void setEndRad(final float endRad) {
		mEndRad = endRad;
	}

	public float getStartRad() {
		return mStartRad;
	}

	public void setStartRad(final float startRad) {
		mStartRad = startRad;
	}

	@Bindable
	public char getValue() {
		return mValue;
	}

	public void setValue(final char value) {
		mValue = value;
		notifyPropertyChanged(BR.value);
	}

	public float getXPivotFraction() {
		return mXPivotFraction;
	}

	public void setXPivotFraction(final float xPivotFraction) {
		mXPivotFraction = xPivotFraction;
	}

	public float getYPivotFraction() {
		return mYPivotFraction;
	}

	public void setYPivotFraction(final float yPivotFraction) {
		mYPivotFraction = yPivotFraction;
	}

	@Bindable
	public boolean isExpanded() {
		return mExpanded;
	}

	public void setExpanded(final boolean expanded) {
		mExpanded = expanded;
		notifyPropertyChanged(BR.expanded);
	}
}
