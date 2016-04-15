package com.github.vmota.roundsoftinputlib.sample;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.vmota.roundsoftinputlib.sample.databinding.ActivityMainBinding;
import com.github.vmota.roundsoftinputlib.SoftInputActivity;

public class MainActivity
		extends Activity {

	private int mCallerId;
	private MainActivityViewModel mViewModel;
	private EditText mInputText, mInputTextCustom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mViewModel = new MainActivityViewModel();
		binding.setViewModel(mViewModel);
		mInputText = binding.etNormalText;
		mInputTextCustom = binding.etNumberText;
	}

	public void startSoftInputActivity(View v) {
		Intent intent = new Intent(this, SoftInputActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SoftInputActivity.PRE_INSERTED_TEXT_KEY_NAME, mInputText.getText().toString());
//		bundle.putBoolean(SoftInputActivity.RIGHT_HANDED_LAYOUT_KEY_NAME, true);
		intent.putExtras(bundle);
		startActivityForResult(intent, SoftInputActivity.TEXT_INSERT_REQUEST);
		mCallerId = v.getId();
	}

	public void startSoftInputActivityCustomSet(View v) {
		Intent intent = new Intent(this, SoftInputActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SoftInputActivity.PRE_INSERTED_TEXT_KEY_NAME, mInputTextCustom.getText().toString());
//		bundle.putBoolean(SoftInputActivity.RIGHT_HANDED_LAYOUT_KEY_NAME, true);

		intent.putExtras(bundle);
		intent.putExtra(SoftInputActivity.CUSTOM_CHAR_SET_KEY_NAME, "0123456789.");
		startActivityForResult(intent, SoftInputActivity.TEXT_INSERT_REQUEST);
		mCallerId = v.getId();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == SoftInputActivity.TEXT_INSERT_REQUEST) {
			if (resultCode == RESULT_OK) {
				final String insertedText = data.getStringExtra(SoftInputActivity.INSERTED_TEXT_KEY_NAME);
				switch (mCallerId) {
					case R.id.et_normal_text:
						mViewModel.setInsertedText(insertedText);
						break;
					case R.id.et_number_text:
						mViewModel.setInsertedTextCustom(insertedText);
						break;
				}

			}
		}
	}
}
