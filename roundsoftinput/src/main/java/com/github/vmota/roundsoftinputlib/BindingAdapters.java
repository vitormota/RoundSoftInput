package com.github.vmota.roundsoftinputlib;

import android.databinding.BindingAdapter;
import android.widget.EditText;

/**
 * Created by vitor on 15/03/2016.
 */
public final class BindingAdapters {

	private BindingAdapters() {}

	@BindingAdapter({"bind:startPosition", "bind:endPosition"})
	public static void editTextSelectionPosition(EditText editText, int start, int end) {
		editText.setSelection(start, end);
	}

}
