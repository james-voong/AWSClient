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
	public String moveTheObjects(int bucketToMoveFrom, int itemToMove, int bucketToMoveTo) {

		instantiateClient();
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
		String contents = (objectKey + " has been moved from '" + bucketToMoveFromName + "' to '" + bucketToMoveToName
				+ "'");
		return contents;
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
