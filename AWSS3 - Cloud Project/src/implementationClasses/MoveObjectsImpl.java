package implementationClasses;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import interfaces.MoveObjects;

public class MoveObjectsImpl implements MoveObjects {

	S3Object object;

	// Method to move objects
	@Override
	public void moveTheObjects(AmazonS3 s3, int bucketToMoveFrom, int itemToMove, int bucketToMoveTo) {
		int currentBucket = 0;
		int currentItem = 0;
		String key = "";
		String bucketToMoveFromName = "";
		String bucketToMoveToName = "";

		// Download the item to be moved and save it as 'object'
		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToMoveFrom) {
				bucketToMoveFromName = bucket.getName();
				ObjectListing objectListing = s3
						.listObjects(new ListObjectsRequest().withBucketName(bucketToMoveFromName));

				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					currentItem++;
					if (currentItem == itemToMove) {
						object = s3.getObject(new GetObjectRequest(bucket.getName(), objectSummary.getKey()));
						key = objectSummary.getKey();
						s3.deleteObject(bucket.getName(), key);
						break;
					}
				}
			}
		}

		// Upload the item to be moved
		currentBucket = 0;
		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToMoveTo) {
				bucketToMoveToName = bucket.getName();
				s3.putObject(new PutObjectRequest(bucketToMoveToName, key, object.getObjectContent(),
						object.getObjectMetadata()));
			}
		}
		System.out.println(key + " has been moved from '" + bucketToMoveFromName + "' to '" + bucketToMoveToName + "'");
	}

}
