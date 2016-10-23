package interfaces;

import com.amazonaws.services.s3.AmazonS3;

public interface MergeBuckets {

	// Merges buckets
	public void mergeTheBuckets(AmazonS3 s3, int bucketToRemain, int bucketToDelete);

}
