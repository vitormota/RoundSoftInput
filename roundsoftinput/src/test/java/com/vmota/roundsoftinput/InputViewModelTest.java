package com.vmota.roundsoftinput;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vitor on 18/03/2016.
 */
public class InputViewModelTest {

	List<InputViewModel> mViewModels;


	@Test
	public void testTouchOn() throws Exception {
		//TODO: ~300ms run time, optimize
		Assert.assertEquals(false, mViewModels.get(0).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(1).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(2).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(3).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(4).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(5).getModelAt(0).isExpanded());

		for (int m = 0; m < mViewModels.size(); m++) {
			mViewModels.get(m).touchOn(250, 160, 160, 160);
			Assert.assertEquals(0f, mViewModels.get(m).getRads());
			Assert.assertEquals(250f, mViewModels.get(m).getX());
			Assert.assertEquals(160f, mViewModels.get(m).getY());
		}

		Assert.assertEquals(false, mViewModels.get(0).getModelAt(1).isExpanded());
		Assert.assertEquals(true, mViewModels.get(0).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(1).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(2).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(3).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(4).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(5).getModelAt(0).isExpanded());

		for (int m = 0; m < mViewModels.size(); m++) {
			mViewModels.get(m).touchOn(160, 10, 160, 160);
			Assert.assertEquals(1.5707964f, mViewModels.get(m).getRads());
			Assert.assertEquals(160f, mViewModels.get(m).getX());
			Assert.assertEquals(10f, mViewModels.get(m).getY());
		}

		Assert.assertEquals(true, mViewModels.get(0).getModelAt(7).isExpanded());
		Assert.assertEquals(false, mViewModels.get(0).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(1).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(2).getModelAt(0).isExpanded());
		Assert.assertEquals(false, mViewModels.get(3).getModelAt(0).isExpanded());
		//next retain state
		Assert.assertEquals(true, mViewModels.get(4).getModelAt(0).isExpanded());
		Assert.assertEquals(true, mViewModels.get(5).getModelAt(0).isExpanded());

	}

	@Test
	public void testGetTouchRads() throws Exception {
		final float center = 160f, x = 10f, y = 10f;
		float result = InputViewModel.getTouchRads(x, y, center, center);
		final float expResult = 2.3561f;
		BigDecimal bd = new BigDecimal(result).setScale(4, BigDecimal.ROUND_FLOOR);

		Assert.assertEquals(result <= 2 * Math.PI && result > 0, true);
		Assert.assertEquals(expResult, bd.floatValue());
	}

	@Test
	public void testCapitalizeKeys() throws Exception {
		Assert.assertEquals('a', mViewModels.get(0).getModelAt(0).getValue());
		mViewModels.get(0).toggleKeysCapitalization();
		Assert.assertEquals('A', mViewModels.get(0).getModelAt(0).getValue());
		mViewModels.get(0).toggleKeysCapitalization();
		Assert.assertEquals('a', mViewModels.get(0).getModelAt(0).getValue());

		Assert.assertEquals('.', mViewModels.get(2).getModelAt(10).getValue());
		mViewModels.get(1).toggleKeysCapitalization();
		Assert.assertEquals('.', mViewModels.get(2).getModelAt(10).getValue());

		Assert.assertEquals('0', mViewModels.get(3).getModelAt(0).getValue());
		mViewModels.get(3).toggleKeysCapitalization();
		Assert.assertEquals('0', mViewModels.get(3).getModelAt(0).getValue());

	}

	@Test
	public void testCalcAnimationPivots() throws Exception {
		final InputViewModel model = mViewModels.get(0);
		model.calcAnimationPivots(0, 0f);
		model.calcAnimationPivots(1, (float) Math.PI / 4f);
		model.calcAnimationPivots(2, (float) Math.PI / 2f);
		model.calcAnimationPivots(3, (float) (3f * Math.PI / 4f));
		model.calcAnimationPivots(4, (float) Math.PI);
		model.calcAnimationPivots(5, (float) (5f * Math.PI / 4f));
		model.calcAnimationPivots(6, (float) (3f * Math.PI / 2f));
		model.calcAnimationPivots(7, (float) (7f * Math.PI / 4f));


		Assert.assertEquals(1f, model.getModelAt(0).getXPivotFraction());
		Assert.assertEquals(.5f, model.getModelAt(0).getYPivotFraction());

		Assert.assertEquals(.75f, model.getModelAt(1).getXPivotFraction());
		Assert.assertEquals(.25f, model.getModelAt(1).getYPivotFraction());

		Assert.assertEquals(.5f, model.getModelAt(2).getXPivotFraction());

		Assert.assertEquals(.25f, model.getModelAt(3).getXPivotFraction());
		Assert.assertEquals(.25f, model.getModelAt(3).getYPivotFraction());

		Assert.assertEquals(.5f, model.getModelAt(4).getYPivotFraction());

		Assert.assertEquals(.75f, model.getModelAt(5).getYPivotFraction());

		Assert.assertEquals(.5f, model.getModelAt(6).getXPivotFraction());
		Assert.assertEquals(1f, model.getModelAt(6).getYPivotFraction());

	}

	@Test
	public void testBackspace() throws Exception {
		mViewModels.get(0).backspace(0);
		Assert.assertEquals(true, mViewModels.get(0).getTextInput().isEmpty());
		mViewModels.get(0).addCharAt('h', 0);
		mViewModels.get(0).addCharAt('e', 1);
		mViewModels.get(0).addCharAt('y', 2);
		mViewModels.get(0).addCharAt('!', 3);
		Assert.assertEquals("hey!", mViewModels.get(0).getTextInput());
		mViewModels.get(0).backspace(4);
		Assert.assertEquals("hey", mViewModels.get(0).getTextInput());
		mViewModels.get(0).backspace(4);
		Assert.assertEquals("hey", mViewModels.get(0).getTextInput());
		mViewModels.get(0).backspace(2);
		Assert.assertEquals("hy", mViewModels.get(0).getTextInput());
	}

	@Test
	public void testAlternateKeys() throws Exception {
		//Warning: Test may fail if key values are built with default method, as the default locale is machine dependant

		Assert.assertEquals('0', mViewModels.get(3).getModelAt(0).getValue());
		Assert.assertEquals('6', mViewModels.get(3).getModelAt(6).getValue());
		Assert.assertEquals('7', mViewModels.get(3).getModelAt(7).getValue());
		mViewModels.get(3).alternateKeys();
		Assert.assertEquals('8', mViewModels.get(3).getModelAt(0).getValue());
		Assert.assertEquals('E', mViewModels.get(3).getModelAt(6).getValue());
		Assert.assertEquals('F', mViewModels.get(3).getModelAt(7).getValue());

		mViewModels.get(3).alternateKeys();
		Assert.assertEquals('0', mViewModels.get(3).getModelAt(0).getValue());
		Assert.assertEquals('6', mViewModels.get(3).getModelAt(6).getValue());
		Assert.assertEquals('7', mViewModels.get(3).getModelAt(7).getValue());

		Assert.assertEquals('0', mViewModels.get(2).getModelAt(0).getValue());
		Assert.assertEquals('6', mViewModels.get(2).getModelAt(6).getValue());
		Assert.assertEquals('7', mViewModels.get(2).getModelAt(7).getValue());
		mViewModels.get(2).alternateKeys();
		Assert.assertEquals('0', mViewModels.get(2).getModelAt(0).getValue());
		Assert.assertEquals('6', mViewModels.get(2).getModelAt(6).getValue());
		Assert.assertEquals('7', mViewModels.get(2).getModelAt(7).getValue());

		mViewModels.get(2).alternateKeys();
		Assert.assertEquals('0', mViewModels.get(2).getModelAt(0).getValue());
		Assert.assertEquals('6', mViewModels.get(2).getModelAt(6).getValue());
		Assert.assertEquals('7', mViewModels.get(2).getModelAt(7).getValue());
	}

	@Test
	public void testAddCharAt() throws Exception {
		mViewModels.get(0).addCharAt('h', 0);
		Assert.assertEquals(1, mViewModels.get(0).getSelectionEnd());
		Assert.assertEquals(1, mViewModels.get(0).getSelectionEnd());
		mViewModels.get(0).addCharAt('y', 1);
		Assert.assertEquals(2, mViewModels.get(0).getSelectionStart());
		Assert.assertEquals(2, mViewModels.get(0).getSelectionEnd());
		mViewModels.get(0).addCharAt('e', 1);
		Assert.assertEquals(2, mViewModels.get(0).getSelectionStart());
		Assert.assertEquals(2, mViewModels.get(0).getSelectionEnd());
		Assert.assertEquals("hey", mViewModels.get(0).getTextInput());
		mViewModels.get(0).addCharAt('!', 4);
		Assert.assertEquals("hey", mViewModels.get(0).getTextInput());
		mViewModels.get(0).addCharAt('!', 3);
		Assert.assertEquals(4, mViewModels.get(0).getSelectionStart());
		Assert.assertEquals(4, mViewModels.get(0).getSelectionEnd());
		Assert.assertEquals("hey!", mViewModels.get(0).getTextInput());
	}

	@After
	public void tearDown() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		mViewModels = new ArrayList<>();
		mViewModels.add(new InputViewModel());
		mViewModels.add(new InputViewModel(30));
		mViewModels.add(new InputViewModel("0123456789."));
		mViewModels.add(new InputViewModel(8, "0123456789ABCDEF"));
		mViewModels.add(new InputViewModel(5, ""));
		mViewModels.add(new InputViewModel(-5));
		Locale.setDefault(new Locale.Builder().setLanguage("pt").setRegion("PT").build());
		mViewModels.add(new InputViewModel());

		for (int m = 0; m < mViewModels.size(); m++) {
			float startRad = 0f;
			float endRad = 0.22f;
			//2 PI by 28 keys ~.22
			for (int i = 0; i < mViewModels.get(m).getKeysOnScreen(); i++) {
				mViewModels.get(m).setInputAmplitude(i, startRad, endRad);
				startRad += .22f;
				endRad += .22f;
			}
		}
	}
}