package com.github.vmota.roundsoftinputlib.sample;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by vitor on 16/03/2016.
 */
public class MainActivityViewModel
		extends BaseObservable {

	private String mInsertedText;
	private String mInsertedTextCustom;

	@Bindable
	public String getInsertedText() {
		return mInsertedText;
	}

	public void setInsertedText(final String insertedText) {
		mInsertedText = insertedText;
		notifyPropertyChanged(BR.insertedText);
	}

	@Bindable
	public String getInsertedTextCustom() {
		return mInsertedTextCustom;
	}

	public void setInsertedTextCustom(final String insertedTextCustom) {
		mInsertedTextCustom = insertedTextCustom;
		notifyPropertyChanged(BR.insertedTextCustom);
	}
}
