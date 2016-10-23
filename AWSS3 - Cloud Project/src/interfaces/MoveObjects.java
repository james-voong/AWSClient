package interfaces;

import com.amazonaws.services.s3.AmazonS3;

public interface MoveObjects {

	// Moves objects
	public void moveTheObjects(AmazonS3 s3, int bucketToMoveFrom, int itemToMove, int bucketToMoveTo);

}
