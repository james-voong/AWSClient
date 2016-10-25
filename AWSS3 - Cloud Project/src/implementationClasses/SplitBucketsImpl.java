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
import interfaces.SplitBuckets;

/** Implementation class of SplitBuckets */
@WebService(endpointInterface = "interfaces.SplitBuckets")
@SOAPBinding(style = Style.RPC)
public class SplitBucketsImpl implements SplitBuckets {

	AmazonS3 s3;
	String newBucketName;

	/** Splits a bucket from a given item point into two buckets */
	@Override
	public void splitTheBuckets(int bucketToSplit, int itemSplitPoint) {
		s3 = Client.getClient();
		int currentBucket = 0;
		int currentItem = 0;
		String splitBucketName = "";

		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToSplit) {
				splitBucketName = bucket.getName();

				// Check if new bucket name exists yet or not
				newBucketName = bucket.getName();
				newBucketName = checkForDuplicateBucket(newBucketName);

				// Create a new bucket for the split
				s3.createBucket(newBucketName);

				ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(splitBucketName));

				// Iterate through all items until split point is found
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					currentItem++;

					// Upon finding the split point
					if (currentItem >= itemSplitPoint) {

						// Download the object
						S3Object object = s3.getObject(new GetObjectRequest(splitBucketName, objectSummary.getKey()));

						// Put the downloaded object in new bucket
						s3.putObject(new PutObjectRequest(newBucketName, objectSummary.getKey(),
								object.getObjectContent(), object.getObjectMetadata()));

						// Delete the old object from original bucket
						s3.deleteObject(bucket.getName(), objectSummary.getKey());
					}
				}
			}
		}
		System.out.println("'" + splitBucketName + "' has been split into '" + newBucketName + "'");
	}

	public String checkForDuplicateBucket(String newBucketName) {
		// This 'for' loop deals with duplicate names
		for (Bucket bucket : s3.listBuckets()) {
			if (newBucketName.equals(bucket.getName())) {
				newBucketName = newBucketName + "-copy";
				checkForDuplicateBucket(newBucketName);
			}
		}
		return newBucketName;
	}

}
