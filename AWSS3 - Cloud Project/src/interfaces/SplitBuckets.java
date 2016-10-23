package interfaces;

import com.amazonaws.services.s3.AmazonS3;

/** Interface for methods related to splitting one bucket into two */
public interface SplitBuckets {

	/** Splits a bucket into two */
	public void splitTheBuckets(AmazonS3 s3, int bucketToSplit, int itemSplitPoint);

}
