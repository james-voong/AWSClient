package interfaces;

import com.amazonaws.services.s3.AmazonS3;

public interface SplitBuckets {

	// Splits buckets
	public void splitTheBuckets(AmazonS3 s3, int bucketToSplit, int itemSplitPoint);

}
