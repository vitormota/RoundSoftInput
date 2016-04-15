package com.github.vmota.roundsoftinputlib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.StateListAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

/**
 * Created by vitor on 10/03/2016.
 *
 * Custom view to represent a key on screen
 */
public class SofInputKey
		extends TextView {

	private static final int[] EXPANDED_STATE = {R.attr.expanded};
	private static final float SCALE_FACTOR = 1.75f;
	private static final int ANIMATION_DURATION_MILLIS = 150;

	private boolean mIsExpanded = false;
	private float mXPivotFraction, mYPivotFraction;

	private Interpolator mInterpolator = new DecelerateInterpolator();

	public SofInputKey(Context context) {
		this(context, null);
	}

	public SofInputKey(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SofInputKey(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final StateListAnimator stateListAnimator = new StateListAnimator();

		final PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1f, SCALE_FACTOR);
		final PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1f, SCALE_FACTOR);

		final ObjectAnimator scaleAnimator =
				ObjectAnimator.ofPropertyValuesHolder(this, pvhScaleX, pvhScaleY).setDuration(ANIMATION_DURATION_MILLIS);

		final PropertyValuesHolder pvhShrinkX = PropertyValuesHolder.ofFloat(SCALE_X, SCALE_FACTOR, 1f);
		final PropertyValuesHolder pvhShrinkY = PropertyValuesHolder.ofFloat(SCALE_Y, SCALE_FACTOR, 1f);

		final ObjectAnimator shrinkAnimator =
				ObjectAnimator.ofPropertyValuesHolder(this, pvhShrinkX, pvhShrinkY).setDuration(ANIMATION_DURATION_MILLIS);

		setupAnimator(scaleAnimator);
		setupAnimator(shrinkAnimator);
		stateListAnimator.addState(EXPANDED_STATE, scaleAnimator);
		stateListAnimator.addState(StateSet.WILD_CARD, shrinkAnimator);

		setStateListAnimator(stateListAnimator);
	}

	private Animator setupAnimator(Animator animator) {
		animator.setInterpolator(mInterpolator);
		return animator;
	}

	@Override
	protected int[] onCreateDrawableState(final int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (mIsExpanded) {
			mergeDrawableStates(drawableState, EXPANDED_STATE);
		}
		return drawableState;
	}

	///////////////////////////////////////////////////////////////////////////
	// Getters & Setters
	///////////////////////////////////////////////////////////////////////////

	public void setExpanded(final boolean expanded) {
		mIsExpanded = expanded;
		refreshDrawableState();
	}

	public boolean isExpanded() {
		return mIsExpanded;
	}

	public void setXPivotFraction(final float xFraction) {
		mXPivotFraction = xFraction;
		setPivotX(getWidth() * mXPivotFraction);
	}

	public void setYPivotFraction(final float yFraction) {
		mYPivotFraction = yFraction;
		setPivotY(getHeight() * mYPivotFraction);
	}
}
