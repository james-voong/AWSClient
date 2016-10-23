package interfaces;

import com.amazonaws.services.s3.AmazonS3;

/** An interface for merging two buckets into one */
public interface MergeBuckets {

	/** Merges two buckets into one */
	public void mergeTheBuckets(AmazonS3 s3, int bucketToRemain, int bucketToDelete);

}
