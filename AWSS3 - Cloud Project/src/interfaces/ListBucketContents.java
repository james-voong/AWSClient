package interfaces;

import com.amazonaws.services.s3.AmazonS3;

public interface ListBucketContents {

	// Prints out all the contents of a bucket
	public void listContents(AmazonS3 S3);

	// Return the total number of buckets
	public int totalNumberOfBuckets(AmazonS3 s3);

	// Return the total number of items inside a bucket
	public int totalNumberOfItemsInsideBucket(AmazonS3 s3, int bucketNumber);
}
