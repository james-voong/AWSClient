package implementationClasses;

import java.util.UUID;

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

	/** Splits a bucket from a given item point into two buckets */
	@Override
	public void splitTheBuckets(int bucketToSplit, int itemSplitPoint) {
		s3 = Client.getClient();
		int currentBucket = 0;
		int currentItem = 0;
		String newBucket = "";
		String splitBucketName = "";

		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToSplit) {
				splitBucketName = bucket.getName();

				// Create a new bucket for the split
				newBucket = bucket.getName() + "-split-" + UUID.randomUUID();
				s3.createBucket(newBucket);

				ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(splitBucketName));

				// Iterate through all items until split point is found
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					currentItem++;

					// Upon finding the split point
					if (currentItem >= itemSplitPoint) {

						// Download the object
						S3Object object = s3.getObject(new GetObjectRequest(splitBucketName, objectSummary.getKey()));

						// Put the downloaded object in new bucket
						s3.putObject(new PutObjectRequest(newBucket, objectSummary.getKey(), object.getObjectContent(),
								object.getObjectMetadata()));

						// Delete the old object from original bucket
						s3.deleteObject(bucket.getName(), objectSummary.getKey());
					}
				}
			}
		}
		System.out.println("'" + splitBucketName + "' has been split into '" + newBucket + "'");
	}

}
