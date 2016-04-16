package com.github.vitormota.roundsoftinputlib;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.vitormota.roundsoftinputlib.databinding.ControlSoftKeyBinding;
import com.github.vitormota.roundsoftinputlib.databinding.RoundKeyboardBinding;

/**
 * Created by vitor on 06/03/2016.
 */
public class SoftInputActivity
		extends WearableActivity
		implements GestureDetector.OnGestureListener,
		View.OnTouchListener,
		OnSoftInputFlingListener {

	public static final String TAG = "SI/InputEvent";
	public static final int TEXT_INSERT_REQUEST = 0;
	public static final String INSERTED_TEXT_KEY_NAME = "inserted_text_key";
	public static final String PRE_INSERTED_TEXT_KEY_NAME = "pre_inserted_text_key";
	public static final String CUSTOM_CHAR_SET_KEY_NAME = "custom_char_set_key";
	public static final String RIGHT_HANDED_LAYOUT_KEY_NAME = "right_handed_layout_key";

	//	private static final long FADE_DURATION = 500;
	//Give the user enough time to perform a fling gesture
//	private static final long SHOW_DELAY = 0;
//	private static final long FADE_DELAY = 1000 + SHOW_DELAY;
//	private static final long SHOW_DURATION = 0;
	private static final int SWIPE_MIN_DISTANCE = 64;
	//	private static final int SWIPE_MAX_OFF_PATH = 125;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	/**
	 * Amount of pixels to shrink the circle so that keys are drawn closer to center
	 * NOTE: Only used for round screen devices
	 */
	private static final int CIRCLE_SHRINK = 35;

	private InputViewModel mInputViewModel;

	private OnSoftInputFlingListener mSoftInputFlingListener;
	private GestureDetector mDetector;
	private View.OnTouchListener mGestureListener;
	/**
	 * NULL ? Then square shape, else round
	 */
	private View mDummy;
	/**
	 * Custom dismiss overlay shown on long press
	 */
	private View mDismissOverlay;
	private EditText mTextInputView;
	/**
	 * ViewGroup Root for soft input
	 */
	private RelativeLayout mSoftInputViewGroup;
	private Point mSize = new Point();
	private boolean mIsScrolling;
	private boolean mFlingDetected = false;
	private boolean mRightHanded;
	//TODO: Maybe get this dynamically, maybe not
	private int mChin = 30;
	private int mDiameter, mPerimeter, mRadius, mCenterX, mCenterY, mKeySide;
	private float mKeyAmplitude;

	/**
	 * Dismiss (hide) the done input overlay
	 *
	 * @param v - the calling view
	 */
	public void dismissOverlay(View v) {
		mDismissOverlay.setVisibility(View.INVISIBLE);
	}

	/**
	 * Called when user is done with text input, finish activity and return text entered bundled for result
	 *
	 * @param v - the calling view
	 */
	public void inputDone(View v) {
		Intent data = new Intent();
		data.putExtra(INSERTED_TEXT_KEY_NAME, mInputViewModel.getTextInput());
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public boolean onDown(final MotionEvent e) {

		return true;
	}

	@Override
	public void onShowPress(final MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(final MotionEvent e) {
		return true;
	}

	@Override
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
		Log.v(TAG, "On Scroll");

		if (mDismissOverlay.getVisibility() == View.VISIBLE) {
			return true;
		}

		mIsScrolling = true;
		mInputViewModel.touchOn(e2.getRawX(), e2.getRawY(), mCenterX, mCenterY);

		return false;
	}

	@Override
	public void onLongPress(final MotionEvent e) {
		mDismissOverlay.bringToFront();
		mDismissOverlay.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		Log.v(TAG, "On Fling");

		mFlingDetected = true;
		//not working
//		mFeedbackCircleShow.cancel();
//		mFeedbackCircleFade.end();

//src: http://stackoverflow.com/questions/13095494/how-to-detect-swipe-direction-between-left-right-and-up-down
		try {
//			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
//				return false;
//			}
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				mSoftInputFlingListener.onLeftFling();
			}
			// left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				mSoftInputFlingListener.onRightFling();
			}
			//bottom to top swipe
			else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				mSoftInputFlingListener.onUpFling();
			}
			//top to bottom swipe
			else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				mSoftInputFlingListener.onDownFling();
			}
		} catch (Exception e) {

		}

		return false;
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (onTouchEvent(event)) {
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mIsScrolling) {
				Log.v(TAG, "OnTouchListener --> onTouch ACTION_UP");
				mIsScrolling = false;
				handleScrollFinished();
			}
		}
		return false;

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
	}

	///////////////////////////////////////////////////////////////////////////
	// OnSoftInputFlingListener
	///////////////////////////////////////////////////////////////////////////

	@Override
	public void onUpFling() {
		mInputViewModel.toggleKeysCapitalization();
	}

	@Override
	public void onDownFling() {
		mInputViewModel.alternateKeys();
	}

	@Override
	public void onLeftFling() {
		mInputViewModel.backspace(mTextInputView.getSelectionEnd());
	}

	@Override
	public void onRightFling() {
		mInputViewModel.addCharAt(' ', mTextInputView.getSelectionEnd());
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		String preInsertedText = bundle.getString(PRE_INSERTED_TEXT_KEY_NAME, "");
		String customCharSet = bundle.getString(CUSTOM_CHAR_SET_KEY_NAME);
		mRightHanded = bundle.getBoolean(RIGHT_HANDED_LAYOUT_KEY_NAME, false);
		mInputViewModel = new InputViewModel(customCharSet);
		mInputViewModel.setTextInput(preInsertedText);

		setContentView(R.layout.main_layout);

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		display.getSize(mSize);

		// Configure a gesture detector
		mDetector = new GestureDetector(this, this);
		mGestureListener = this;
		mSoftInputFlingListener = this;

		mDiameter = Math.max(mSize.y - CIRCLE_SHRINK, mSize.x - CIRCLE_SHRINK);

		mKeyAmplitude = (float) (2.0 * Math.PI) / (InputViewModel.MAX_KEYS_ON_SCREEN + 6);
		mRadius = (mDiameter) / 2;
		mCenterX = mRadius + CIRCLE_SHRINK / 2;
		mCenterY = mRadius + CIRCLE_SHRINK / 2;
		mPerimeter = (int) (2 * Math.PI * Math.sqrt((Math.pow(mSize.x / 2, 2) + Math.pow((mSize.y + mChin) / 2, 2)) / 2.0));
		mKeySide = mPerimeter / (26 + 6);

		((WatchViewStub) findViewById(R.id.ws_root)).setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(final WatchViewStub watchViewStub) {
				mDummy = findViewById(R.id.dummy);
				RoundKeyboardBinding binding = RoundKeyboardBinding.bind(findViewById(R.id.rl_soft_input_root));
				DebugOptions opt = new DebugOptions();
				binding.setDebug(opt);
				binding.setViewModel(mInputViewModel);
				mSoftInputViewGroup = binding.rlSoftInputRoot;
				mSoftInputViewGroup.setOnTouchListener(SoftInputActivity.this);
				mTextInputView = binding.etTextInput;
				mTextInputView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(final View v, final MotionEvent event) {
						if (mDismissOverlay.getVisibility() == View.VISIBLE) { return true; }
						if (event.getAction() == MotionEvent.ACTION_UP) {
							boolean res = false;
							if (mIsScrolling) { res = true; }
							SoftInputActivity.this.onTouch(v, event);
							return res;
						}
						return onTouchEvent(event);
					}
				});
				mDismissOverlay = binding.dismissOverlay;
				Toast.makeText(SoftInputActivity.this, R.string.long_press_intro, Toast.LENGTH_SHORT).show();
				addKeysToRoot();
			}
		});
	}

	/**
	 * Draw keys on screen
	 */
	private void addKeysToRoot() {
		final double TWO_PI = 2.0 * Math.PI;

		int halfKeySide = mKeySide / 2;
//		x = cx + r * cos(a)
//		y = cy + r * sin(a)
		float startRad;
		//factor will determine the direction of drawing (right vs left handed)
		int factor = mRightHanded ? -1 : 1;
		if (mDummy == null) {
			startRad = (float) (4 * Math.PI / 3.0);
		}
		else {
			startRad = (float) (Math.atan2(-mCenterY, -Math.sqrt(Math.pow(mRadius, 2) - Math.pow(mRadius - mChin, 2))) + TWO_PI);
		}
		if (mRightHanded) {
			startRad = (float) (TWO_PI - (startRad - Math.PI));
		}
		for (int i = 0; i < mInputViewModel.getKeysOnScreen(); i++) {
			float keyCenterRad = startRad - factor * (mKeyAmplitude * (i + 1));
			if (keyCenterRad < 0) {
				keyCenterRad += TWO_PI;
			}
			else if (keyCenterRad > TWO_PI) {
				keyCenterRad -= TWO_PI;
			}

			int keyCenterX, keyCenterY, leftMargin, topMargin;

			if (mDummy == null) {
				//drawing on squared screen

				double hypotenuse = mRadius * Math.sqrt(2.0);

				final int outerX = (int) (hypotenuse * Math.cos(keyCenterRad));
				if (outerX <= -mRadius) { keyCenterX = 0; }
				else if (outerX >= mRadius) {keyCenterX = mDiameter;}
				else { keyCenterX = outerX + mRadius; }

				final int outerY = (int) (hypotenuse * Math.sin(keyCenterRad));
				if (outerY <= -mRadius) { keyCenterY = mDiameter; }
				else if (outerY >= mRadius) {keyCenterY = 0;}
				else { keyCenterY = Math.abs(outerY - mRadius); }

				leftMargin = keyCenterX;// - halfKeySide;
				topMargin = keyCenterY;// - halfKeySide;
			}
			else {
				//drawing on circular screen
				keyCenterX = (int) (mCenterX + mRadius * Math.cos(keyCenterRad));
				//yy axis grows in opposite direction
				keyCenterY = (int) (mSize.x - Math.abs(mCenterY + mRadius * Math.sin(keyCenterRad)));

				leftMargin = keyCenterX - halfKeySide;
				topMargin = keyCenterY - halfKeySide;
			}

			View key = getLayoutInflater().inflate(R.layout.control_soft_key, null);
			ControlSoftKeyBinding binding = ControlSoftKeyBinding.bind(key);

			final int index = i;
			final InputModel model = mInputViewModel.getModelAt(index);

			binding.setInputModel(model);

			float halfKeyAmplitude = mKeyAmplitude / 2f;
			mInputViewModel.setInputAmplitude(index, keyCenterRad - halfKeyAmplitude, keyCenterRad + halfKeyAmplitude);
			mInputViewModel.calcAnimationPivots(index, keyCenterRad);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mKeySide, mKeySide);
			params.setMargins(leftMargin, topMargin, 0, 0);
			mSoftInputViewGroup.addView(key, params);
		}
	}

	/**
	 * Called when user interaction ends i.e., he has lifted his finger from the screen.
	 * In case of a fling detected perform the fling 'direction' associated action
	 * else insert char from last selected key
	 */
	private void handleScrollFinished() {
		if (!mFlingDetected) {
			mInputViewModel.handleScrollEnd(mTextInputView.getSelectionEnd());
		}
		mFlingDetected = false;
	}

	/**
	 * Getter for fling listener
	 * @return the currently associated listener
	 */
	public OnSoftInputFlingListener getSoftInputFlingListener() {
		return mSoftInputFlingListener;
	}

	/**
	 * Setter for fling listener, use this to override default fling actions
	 * @param softInputFlingListener - the object defining the new custom actions
	 */
	public void setSoftInputFlingListener(final OnSoftInputFlingListener softInputFlingListener) {
		mSoftInputFlingListener = softInputFlingListener;
	}
}
