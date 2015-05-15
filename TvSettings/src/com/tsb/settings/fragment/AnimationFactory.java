package com.tsb.settings.fragment;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

public class AnimationFactory {
	public AnimationFactory() {

	}

	/**
	 * 创建channelist中的左右键翻页、长按下键提示所用到的动画
	 * @return AnimationSet 
	 *         渐渐清晰，一段时间后渐渐消失的动画
	 */
	public AnimationSet getPromptAnimation(){
		AnimationSet animationSet = new AnimationSet(true);
		Animation promptFadeInAnimation = new AlphaAnimation(0, 1);
		promptFadeInAnimation.setDuration(500l);
		promptFadeInAnimation.setStartOffset(500l);
		
		Animation promptFadeOutAnimation = new AlphaAnimation(1, 0);
		promptFadeOutAnimation.setDuration(500l);
		promptFadeOutAnimation.setStartOffset(6000l);
		animationSet.addAnimation(promptFadeInAnimation);
		animationSet.addAnimation(promptFadeOutAnimation);
		return animationSet;
	}
}
