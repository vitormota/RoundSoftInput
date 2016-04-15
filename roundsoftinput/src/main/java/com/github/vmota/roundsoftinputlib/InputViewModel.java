package com.github.vmota.roundsoftinputlib;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.ULocale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vitor on 07/03/2016.
 */
public class InputViewModel
		extends BaseObservable {

	/**
	 * Value found to be the most reasonable, further testing may change this to a dynamic value
	 */
	public static final int MAX_KEYS_ON_SCREEN = 26;

	private int mKeysOnScreen;
	private float mX, mY;
	private float mRads;

	private int mSelectionStart
			//Text selection not used at the moment
			, mSelectionEnd;
	private StringBuilder mTextInput;
	private String mInputChars;
	private InputModel mSelectedModel;
	private List<InputModel> mInputs;

	/**
	 * Default constructor
	 */
	public InputViewModel() {
		this(MAX_KEYS_ON_SCREEN);
	}

	/**
	 * @param keysOnScreen - number of keys to show at once, limited by 1 and MAX_KEYS_ON_SCREEN
	 */
	public InputViewModel(int keysOnScreen) {
		this(keysOnScreen, buildDefaultInputChars());
	}

	/**
	 * @param inputChars - string containing all possible input characters
	 */
	public InputViewModel(String inputChars) {
		this(MAX_KEYS_ON_SCREEN, inputChars);
	}

	/**
	 * @param keysOnScreen - number of keys to show at once, limited by 1 and MAX_KEYS_ON_SCREEN
	 */
	public InputViewModel(final int keysOnScreen, String inputChars) {
		if (inputChars == null || inputChars.isEmpty()) {
			inputChars = buildDefaultInputChars();
		}
		this.mKeysOnScreen = Math.min(Math.min(Math.max(keysOnScreen, 1), MAX_KEYS_ON_SCREEN), inputChars.length());
		setInputChars(inputChars);
		mTextInput = new StringBuilder();
	}

	public void addCharAt(final char value, final int position) {
		if (mTextInput.length() >= position) {
			setTextInput(mTextInput.insert(position, value));
			mSelectionStart = position + 1;
			setSelectionStart(mSelectionStart);
			setSelectionEnd(mSelectionStart);
		}
	}

	private int count = 0;

	/**
	 * Alternate currently displayed keys with ones that are not being shown in a cyclic order
	 * If number of possible characters to show is lower than the number of the mount of keys that can be shown at once, nothing happens
	 */
	public void alternateKeys() {
		if (mKeysOnScreen >= mInputChars.length()) {
			return;
		}
		count++;
		char[] chars = mInputChars.toCharArray();
		for (int i = 0; i < mInputs.size(); i++) {
			if (count * getKeysOnScreen() + i >= chars.length) {
//				count = -1;
//				blankRemainingKeys(i);
//				return;
				mInputs.get(i).setValue('\0');
			}
			else {
				mInputs.get(i).setValue(chars[count * getKeysOnScreen() + i]);
			}
		}
		if ((count + 1) * getKeysOnScreen() >= chars.length) {
			count = -1;
		}
	}

//	private void blankRemainingKeys(final int startIndex) {
//		for (int i = startIndex; i < mInputs.size(); i++) {
//			mInputs.get(i).setValue('\0');
//		}
//	}

	/**
	 * Delete character at specific position
	 *
	 * @param position - the position of the char to be deleted
	 */
	public void backspace(final int position) {
		if (mTextInput.length() > 0 && mTextInput.length() >= position) {
			setTextInput(mTextInput.deleteCharAt(position - 1));
			mSelectionStart = position - 1;
			setSelectionStart(mSelectionStart);
			setSelectionEnd(mSelectionStart);
		}
	}

	/**
	 * Calculate animations pivots and set animations on model
	 *
	 * @param index      - index of the model
	 * @param centerRads - the corresponding radians at key center point
	 */
	public void calcAnimationPivots(final int index, final float centerRads) {
		final double PI_HALVE = Math.PI / 2.0;
		final double THREE_PI_HALVES = 3.0 * Math.PI / 2.0;
		final double TWO_PI = 2.0 * Math.PI;

		double px = .5, py = .5;

		if (centerRads >= 0 && centerRads <= Math.PI) {
			px = 1 - (centerRads / Math.PI);
		}
		else if (centerRads > Math.PI && centerRads < TWO_PI) {
			px = (centerRads - Math.PI) / Math.PI;
		}

		if (centerRads >= PI_HALVE && centerRads <= THREE_PI_HALVES) {
			py = (centerRads - PI_HALVE) / Math.PI;
		}
		else {
			if ((centerRads >= 0 && centerRads < PI_HALVE)) {
				py = 1 - (centerRads + PI_HALVE) / Math.PI;
			}
			else if (centerRads > THREE_PI_HALVES && centerRads < TWO_PI) {
				py = PI_HALVE / (centerRads - Math.PI);

				//TODO: screen obscuration optimization (this is just a test)
				//To handle screen obscuration modify pivots so that the key animates past the finger
//				px += 3.5f;
//				py += 3.5f;
			}
		}


		getModelAt(index).setXPivotFraction((float) px);
		getModelAt(index).setYPivotFraction((float) py);

	}

	/**
	 * Toggle currently displayed keys capitalization
	 */
	public void toggleKeysCapitalization() {
		for (InputModel model : mInputs) {
			model.toggleCase();
		}
	}

	/**
	 * Getter for model by its position
	 *
	 * @param index - position of model
	 * @return the model
	 */
	public InputModel getModelAt(int index) {
		return mInputs.get(index);
	}

	/**
	 * Get touch radians based of screen coordinates
	 *
	 * @param x       - touch x
	 * @param y       - touch y
	 * @param centerX - circle center x
	 * @param centerY - circle center y
	 * @return the corresponding radians
	 */
	public static float getTouchRads(final float x, final float y, final float centerX, final float centerY) {
		float res = (float) Math.atan2(centerY - y, x - centerX);
		if (res < 0) {
			res += 2.0 * Math.PI;
		}
		return res;
	}

	/**
	 * Called when scroll end is detected on UI, and add selected key's character to input
	 *
	 * @param selectionEnd
	 */
	public void handleScrollEnd(final int selectionEnd) {
		if (mSelectedModel != null) {
			addCharAt(mSelectedModel.getValue(), selectionEnd);
		}
	}

	/**
	 * Set start and end radians to model at index position
	 *
	 * @param index    - position of model
	 * @param startRad - corresponding key start radians
	 * @param endRad   - corresponding key end radians
	 */
	public void setInputAmplitude(final int index, final float startRad, final float endRad) {
		mInputs.get(index).setStartRad(startRad);
		mInputs.get(index).setEndRad(endRad);
	}

	/**
	 * Called by main UI on each scroll event, finds the key's model and 'focus' that key (if any found)
	 * If so, previously selected key is 'unfocused'
	 *
	 * @param rawX    - screen touch x
	 * @param rawY    - screen touch y
	 * @param centerX - circle center x
	 * @param centerY - circle center y
	 */
	public void touchOn(final float rawX, final float rawY, final int centerX, final int centerY) {

		setRads(getTouchRads(rawX, rawY, centerX, centerY));
		setX(rawX);
		setY(rawY);

		final InputModel model = findModelByRads(mRads);

		if (model != null && mSelectedModel != model) {
			if (mSelectedModel != null) { unfocusSelectedKey(); }

			mSelectedModel = model;
			focusSelectedKey();

		}

	}

	/**
	 * Find a model by radians
	 *
	 * @param rads - the radians
	 * @return the found model or null if it doesn't exist
	 */
	private InputModel findModelByRads(final double rads) {

		for (InputModel model : mInputs) {
			if (model.getStartRad() <= rads && model.getEndRad() > rads) {
				return model;
			}
		}
		return null;
	}

	/**
	 * Focus the selected key associated with this object selected model property
	 * Expands key to expanded size
	 */
	private void focusSelectedKey() {
		if (mSelectedModel.isExpanded()) { return; }
		mSelectedModel.setExpanded(true);
	}

	/**
	 * Unfocus the selected key associated with this object selected model property
	 * Shrinks key to default size
	 */
	private void unfocusSelectedKey() {

		if (!mSelectedModel.isExpanded()) { return; }
		mSelectedModel.setExpanded(false);
	}

	/**
	 * Method to build a default input characters string, based on the user locale if none is provided upon this object initialization.
	 * Please see http://www.unicode.org/reports/tr35/tr35-general.html#Character_Elements for more information
	 *
	 * @return the string containing all possible input characters
	 */
	private static String buildDefaultInputChars() {
		String inputChars = "";
		final ULocale locale = ULocale.forLocale(Locale.getDefault());
		UnicodeSet standard = LocaleData.getExemplarSet(locale, 0, LocaleData.ES_STANDARD), punctuation =
				LocaleData.getExemplarSet(locale, 0, LocaleData.ES_PUNCTUATION);

		for (int i = 0; i < standard.size(); i++) {
			inputChars += (char) standard.charAt(i);
		}

		for (int i = 0; i < punctuation.size(); i++) {
			inputChars += (char) punctuation.charAt(i);
		}

		return inputChars;
	}

	///////////////////////////////////////////////////////////////////////////
	// Getters & Setters
	///////////////////////////////////////////////////////////////////////////

	public String getInputChars() {
		return mInputChars;
	}

	private void setInputChars(final String inputChars) {
		final int capacity = Math.min(inputChars.length(), mKeysOnScreen);
		mInputs = new ArrayList<>(capacity);
		char[] chars = inputChars.toCharArray();
		for (int i = 0; i < capacity; i++) {
			mInputs.add(new InputModel(chars[i]));
		}
		mInputChars = inputChars;
	}

	public int getKeysOnScreen() {
		return mKeysOnScreen;
	}

	@Bindable
	public float getRads() {
		return mRads;
	}


	public void setRads(final float rads) {
		mRads = rads;
		notifyPropertyChanged(BR.rads);
	}

	@Bindable
	public int getSelectionEnd() {
		return mSelectionEnd;
	}

	public void setSelectionEnd(final int selectionEnd) {
		mSelectionEnd = selectionEnd;
		notifyPropertyChanged(BR.selectionEnd);
	}

	@Bindable
	public int getSelectionStart() {
		return mSelectionStart;
	}

	public void setSelectionStart(final int selectionStart) {
		mSelectionStart = selectionStart;
		notifyPropertyChanged(BR.selectionStart);
	}

	@Bindable
	public String getTextInput() {
		return mTextInput.toString();
	}

	private void setTextInput(final StringBuilder textInput) {
		mTextInput = textInput;
		notifyPropertyChanged(BR.textInput);
	}

	@Bindable
	public float getX() {
		return mX;
	}

	public void setX(final float x) {
		mX = x;
		notifyPropertyChanged(BR.x);
	}

	@Bindable
	public float getY() {
		return mY;
	}

	public void setY(final float y) {
		mY = y;
		notifyPropertyChanged(BR.y);
	}

	public void setTextInput(final String textInput) {
		mTextInput = new StringBuilder(textInput);
	}


}
