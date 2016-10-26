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
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import interfaces.ListBucketContents;

/** Implementation class for ListBucketContents */
@WebService(endpointInterface = "interfaces.ListBucketContents")
@SOAPBinding(style = Style.RPC)
public class ListBucketContentsImpl implements ListBucketContents {

	private AmazonS3 s3;

	/** Prints out all buckets and their respective contents in console */
	@Override
	public String[] listContents() {
		String[] contents = new String[300];
		int i = 0;
		instantiateClient();
		// Integer for numbering buckets
		int bucketNumber = 0;

		// List the buckets
		contents[i] = ("Listing bucket(s)");
		i++;
		for (Bucket bucket : s3.listBuckets()) {
			bucketNumber++;
			contents[i] = (bucketNumber + ". " + bucket.getName());
			i++;

			ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(bucket.getName()));

			// Integer for numbering items inside the buckets
			int itemNumber = 0;

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				itemNumber++;
				contents[i] = ("  " + itemNumber + " - " + objectSummary.getKey());
				i++;
			}
			contents[i] = ("");
			i++;
		}
		return contents;
	}

	/** Returns total number of buckets */
	@Override
	public int totalNumberOfBuckets() {
		instantiateClient();
		int totalBuckets = 0;
		for (@SuppressWarnings("unused")
		Bucket bucket : s3.listBuckets()) {
			totalBuckets++;
		}
		return totalBuckets;
	}

	/** Returns total number of items inside a given bucket */
	public int totalNumberOfItemsInsideBucket(int bucketNumber) {
		instantiateClient();
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