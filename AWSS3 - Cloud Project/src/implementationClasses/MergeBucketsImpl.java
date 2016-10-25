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
import interfaces.MergeBuckets;

/** Implementation class of MergeBuckets */
@WebService(endpointInterface = "interfaces.MergeBuckets")
@SOAPBinding(style = Style.RPC)
public class MergeBucketsImpl implements MergeBuckets {

	AmazonS3 s3;

	/** Merges two buckets together so that only one remains */
	@Override
	public void mergeTheBuckets(int bucketToRemain, int bucketToDelete) {
		s3 = Client.getClient();
		int currentBucket = 0;
		String bucketToDelete_Name = "";
		String bucketToRemain_Name = "";

		// Find the bucket that is to be kept and save it
		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToRemain) {
				bucketToRemain_Name = bucket.getName();
				break;
			}
		}

		currentBucket = 0;
		// Find the bucket that will not be kept ie. bucketToDelete_Name
		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToDelete) {
				bucketToDelete_Name = bucket.getName();
				ObjectListing objectListing = s3
						.listObjects(new ListObjectsRequest().withBucketName(bucketToDelete_Name));

				// Iterate through all items and move to bucketToRemain_Name
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

					// Download the object
					S3Object object = s3.getObject(new GetObjectRequest(bucket.getName(), objectSummary.getKey()));

					// Put the downloaded object in new location
					s3.putObject(new PutObjectRequest(bucketToRemain_Name, objectSummary.getKey(),
							object.getObjectContent(), object.getObjectMetadata()));

					// Delete the old object from original location
					s3.deleteObject(bucketToDelete_Name, objectSummary.getKey());
				}
			}
		}
		// Delete the old bucket
		s3.deleteBucket(bucketToDelete_Name);
		System.out.println("'" + bucketToDelete_Name + "' has been merged into '" + bucketToRemain_Name + "'");
	}

}
