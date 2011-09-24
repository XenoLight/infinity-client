package org.rsbot.client;

public interface RSCharacter extends RSAnimable {

	int[] getAnimationQueue();

	Graphic[] getGraphicsData();

	int getHeight();

	int getHPRatio();

	int getInteracting();

	int[] getLocationX();

	int[] getLocationY();

	int getLoopCycleStatus();

	RSMessageData getMessageData();

	Model getModel();

	int getOrientation();

	int isMoving();
}
