package implementationClasses;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import client.Client;
import interfaces.MoveObjects;

/** Implementation class of MoveObjects */
@WebService(endpointInterface = "interfaces.MoveObjects")
@SOAPBinding(style = Style.RPC)
public class MoveObjectsImpl implements MoveObjects {

	S3Object object;
	AmazonS3 s3;
	ObjectListing objectListing_BucketToMoveTo;
	String objectKey;

	/** Moves an object from one bucket into another */
	@Override
	public void moveTheObjects(int bucketToMoveFrom, int itemToMove, int bucketToMoveTo) {

		s3 = Client.getClient();
		int currentBucket = 0;
		int currentItem = 0;
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
						objectKey = objectSummary.getKey();
						s3.deleteObject(bucket.getName(), objectKey);
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
				objectListing_BucketToMoveTo = s3
						.listObjects(new ListObjectsRequest().withBucketName(bucketToMoveToName));
				objectKey = checkForDuplicateItems(objectKey);

				s3.putObject(new PutObjectRequest(bucketToMoveToName, objectKey, object.getObjectContent(),
						object.getObjectMetadata()));
			}
		}
		System.out.println(
				objectKey + " has been moved from '" + bucketToMoveFromName + "' to '" + bucketToMoveToName + "'");

	}

	public String checkForDuplicateItems(String objectKeyChecker) {
		// This 'for' loop deals with duplicate names
		for (S3ObjectSummary objectSummary : objectListing_BucketToMoveTo.getObjectSummaries()) {
			if (objectKeyChecker.equals(objectSummary.getKey())) {
				objectKeyChecker = objectKeyChecker + "-copy";
				checkForDuplicateItems(objectKeyChecker);
			}
		}
		return objectKeyChecker;
	}

}
