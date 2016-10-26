package implementationClasses;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import interfaces.MergeBuckets;

/** Implementation class of MergeBuckets */
@WebService(endpointInterface = "interfaces.MergeBuckets")
@SOAPBinding(style = Style.RPC)
public class MergeBucketsImpl implements MergeBuckets {

	AmazonS3 s3;
	ObjectListing objectListing_Remain;
	String objectKey;

	/** Merges two buckets together so that only one remains */
	@Override
	public String mergeTheBuckets(int bucketToRemain, int bucketToDelete) {
		instantiateClient();
		int currentBucket = 0;
		String bucketToDelete_Name = "";
		String bucketToRemain_Name = "";

		// Find the bucket that is to be kept and save it
		for (Bucket bucket : s3.listBuckets()) {
			currentBucket++;
			if (currentBucket == bucketToRemain) {
				bucketToRemain_Name = bucket.getName();

				// Create objectListing for bucketToRemain
				objectListing_Remain = s3.listObjects(new ListObjectsRequest().withBucketName(bucketToRemain_Name));
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
					objectKey = objectSummary.getKey();

					objectKey = checkForDuplicateItems(objectKey);

					// Put the downloaded object in new location
					s3.putObject(new PutObjectRequest(bucketToRemain_Name, objectKey, object.getObjectContent(),
							object.getObjectMetadata()));

					// Delete the old object from original location
					s3.deleteObject(bucketToDelete_Name, objectSummary.getKey());
				}
			}
		}
		// Delete the old bucket
		s3.deleteBucket(bucketToDelete_Name);
		String contents = ("'" + bucketToDelete_Name + "' has been merged into '" + bucketToRemain_Name + "'");
		return contents;
	}

	/** Checks for duplicate items and renames them if there is a duplicate */
	public String checkForDuplicateItems(String objectKeyChecker) {
		// This 'for' loop deals with duplicate names
		for (S3ObjectSummary objectSummary2 : objectListing_Remain.getObjectSummaries()) {
			if (objectKeyChecker.equals(objectSummary2.getKey())) {
				objectKeyChecker = objectKeyChecker + "-copy";
				checkForDuplicateItems(objectKeyChecker);
			}
		}
		return objectKeyChecker;
	}

	public void instantiateClient() {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/voongjame/.aws/credentials), and is in valid format.", e);
		}

		// Instantiate a new client
		s3 = new AmazonS3Client(credentials);

		// Set region
		Region myRegion = Region.getRegion(Regions.AP_SOUTHEAST_2);
		s3.setRegion(myRegion);

	}

}
