package interfaces;

import com.amazonaws.services.s3.AmazonS3;

/** Interface for when an object is to be moved */
public interface MoveObjects {

	/** Moves an object from one bucket to another */
	public void moveTheObjects(AmazonS3 s3, int bucketToMoveFrom, int itemToMove, int bucketToMoveTo);

}
