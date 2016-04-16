package com.github.vitormota.roundsoftinputlib;

/**
 * Created by vitor on 06/03/2016.
 */
public final class DebugOptions {

	private boolean showCrossReferential = false;
	private boolean showVerboseInfo = false;

	public DebugOptions() {}

	public boolean getShowCrossReferential() {
		return showCrossReferential;
	}

	public void setShowCrossReferential(final boolean showCrossReferential) {
		this.showCrossReferential = showCrossReferential;
	}

	public void setShowVerboseInfo(final boolean showVerboseInfo) {
		this.showVerboseInfo = showVerboseInfo;
	}

	public boolean getShowVerboseInfo() {
		return showVerboseInfo;
	}
}
