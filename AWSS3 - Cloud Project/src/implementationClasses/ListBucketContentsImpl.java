package implementationClasses;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import interfaces.ListBucketContents;

public class ListBucketContentsImpl implements ListBucketContents {

	@Override
	public void listContents(AmazonS3 s3) {
		// Integer for numbering buckets
		int bucketNumber = 0;

		// List the buckets
		System.out.println("Listing bucket(s):");
		for (Bucket bucket : s3.listBuckets()) {
			bucketNumber++;
			System.out.println(bucketNumber + ". " + bucket.getName());

			ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucket.getName()));

			// Integer for numbering items inside the buckets
			int itemNumber = 0;

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				itemNumber++;
				System.out.println("  " + itemNumber + " - " + objectSummary.getKey());
			}
			System.out.println();
		}
	}

	// Returns total number of buckets
	@Override
	public int totalNumberOfBuckets(AmazonS3 s3) {
		int totalBuckets = 0;
		for (@SuppressWarnings("unused")
		Bucket bucket : s3.listBuckets()) {
			totalBuckets++;
		}
		return totalBuckets;
	}

	// Returns total number of items inside a given bucket
	@Override
	public int totalNumberOfItemsInsideBucket(AmazonS3 s3, int bucketNumber) {

		int currentBucket = 0;
		int totalNumberOfItems = 0;

		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketNumber) {
				ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucket.getName()));

				for (@SuppressWarnings("unused")
				S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					totalNumberOfItems++;
				}
			}
		}
		return totalNumberOfItems;
	}

}