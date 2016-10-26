package webService;

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

import implementationClasses.ListBucketContentsImpl;
import implementationClasses.MergeBucketsImpl;
import implementationClasses.MoveObjectsImpl;
import implementationClasses.SplitBucketsImpl;
import interfaces.ListBucketContents;
import interfaces.MergeBuckets;
import interfaces.MoveObjects;
import interfaces.SplitBuckets;

/** This class is to wrap the methods from the other 4 classes */
@WebService(endpointInterface = "webService.WebServiceInterface")
@SOAPBinding(style = Style.RPC)
public class WebServiceImpl implements WebServiceInterface {

	static AmazonS3 s3;

	/** Wrapper that calls the listContents method */
	@Override
	public void listContents() {
		ListBucketContents listObj = new ListBucketContentsImpl();
		listObj.listContents();
	}

	/**
	 * Wrapper that calls the totalNumberOfBuckets and returns it as an integer
	 */
	@Override
	public int totalNumberOfBuckets() {
		ListBucketContents listObj = new ListBucketContentsImpl();
		int num = listObj.totalNumberOfBuckets();

		return num;
	}

	/**
	 * Wrapper that calls a method to return the total number of items inside a
	 * bucket
	 */
	@Override
	public int totalNumberOfItemsInsideBucket(int bucketNumber) {
		ListBucketContents listObj = new ListBucketContentsImpl();
		int num = listObj.totalNumberOfItemsInsideBucket(bucketNumber);
		return num;
	}

	/** Wrapper that calls a method to merge buckets */
	@Override
	public void mergeTheBuckets(int bucketToRemain, int bucketToDelete) {
		MergeBuckets mergeObj = new MergeBucketsImpl();
		mergeObj.mergeTheBuckets(bucketToRemain, bucketToDelete);

	}

	/**
	 * Wrapper that calls a method to move an object from one bucket to another
	 */
	@Override
	public void moveTheObjects(int bucketToMoveFrom, int itemToMove, int bucketToMoveTo) {
		MoveObjects moveObj = new MoveObjectsImpl();
		moveObj.moveTheObjects(bucketToMoveFrom, itemToMove, bucketToMoveTo);

	}

	/** Wrapper that calls a method to split a bucket */
	@Override
	public void splitTheBuckets(int bucketToSplit, int itemSplitPoint) {
		SplitBuckets splitObj = new SplitBucketsImpl();
		splitObj.splitTheBuckets(bucketToSplit, itemSplitPoint);
	}

	/** This method instantiates an AmazonS3 client */
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

//	/** Getter to return the instantiated client */
//	public AmazonS3 getClient() {
//		return s3;
	//	}

}
