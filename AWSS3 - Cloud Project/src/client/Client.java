package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class Client {

	/*
	 * Declare as static fields so they can be called in all methods within this
	 * class
	 */
	private static ListBucketContents listContentsObj = new ListBucketContentsImpl();
	private static MoveObjects moveObj = new MoveObjectsImpl();
	private static MergeBuckets mergeObj = new MergeBucketsImpl();
	private static SplitBuckets splitObj = new SplitBucketsImpl();

	private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {

		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/voongjame/.aws/credentials), and is in valid format.", e);
		}

		// Instantiate a new client
		AmazonS3 s3 = new AmazonS3Client(credentials);

		// Set region
		Region myRegion = Region.getRegion(Regions.AP_SOUTHEAST_2);
		s3.setRegion(myRegion);

		// Client for interaction with user
		while (true) {

			System.out.println("\nWhat do you wish to do? (Integer input)");
			System.out.println("1. List Bucket Contents");
			System.out.println("2. Move Objects");
			System.out.println("3. Merge Buckets");
			System.out.println("4. Split Buckets");

			int answer = 0;

			// Read the user input and save it
			try {
				answer = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			// Call method that user wanted
			if (answer == 1) {
				listContentsObj.listContents(s3);
			} else if (answer == 2) {
				moveObjectsClient(s3);
			} else if (answer == 3) {
				mergeBucketsClient(s3);
			} else if (answer == 4) {
				splitBucketClient(s3);
			} else
				System.out.println("\nInvalid option. Try again.\n");
		}

	}

	// For when the user wants to move objects from one bucket to another
	public static void moveObjectsClient(AmazonS3 s3) throws IOException {
		listContentsObj.listContents(s3);

		// Get the total number of buckets
		int totalBucketNumber = listContentsObj.totalNumberOfBuckets(s3);
		int totalNumberOfItems = 0;
		int bucketToMoveFrom = 0;
		int itemToMove = 0;
		int bucketToMoveTo = 0;
		boolean bucketToMoveFrom_Accepted = false;
		boolean itemToMove_Accepted = false;
		boolean bucketToMoveTo_Accepted = false;

		// Inside a while loop to prevent invalid bucket selection by user
		while (bucketToMoveFrom_Accepted == false) {
			System.out.println("\nPlease select which bucket to move an item from:");

			// Save the selected bucket number
			try {
				bucketToMoveFrom = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			// Get the total number of items inside bucket
			totalNumberOfItems = listContentsObj.totalNumberOfItemsInsideBucket(s3, bucketToMoveFrom);

			// If invalid input selected
			if (bucketToMoveFrom <= 0 || bucketToMoveFrom > totalBucketNumber) {
				System.out.println("\nSelected bucket does not exist, try again.");
			}
			// If the bucket is empty
			else if (totalNumberOfItems <= 0) {
				System.out.println("\nBucket is empty. Select a different bucket.");
			} else
				bucketToMoveFrom_Accepted = true;
		}

		while (itemToMove_Accepted == false) {
			System.out.println("\nPlease select which item to move out of bucket " + bucketToMoveFrom);

			// Save the selected item number
			try {
				itemToMove = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			if (itemToMove <= 0 || itemToMove > totalNumberOfItems) {
				System.out.println("\nSelected item does not exist, try again.");
			} else
				itemToMove_Accepted = true;
		}

		while (bucketToMoveTo_Accepted == false) {
			System.out.println("\nWhich bucket do you wish to move it to?");

			// Save the new bucket number

			try {
				bucketToMoveTo = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			if (bucketToMoveTo <= 0 || bucketToMoveTo > totalBucketNumber) {
				System.out.println("\nSelected bucket does not exist, try again.");
			} else
				bucketToMoveTo_Accepted = true;
		}

		moveObj.moveTheObjects(s3, bucketToMoveFrom, itemToMove, bucketToMoveTo);

	}

	public static void mergeBucketsClient(AmazonS3 s3) throws IOException {
		listContentsObj.listContents(s3);
		int totalBuckets = listContentsObj.totalNumberOfBuckets(s3);
		int bucketToRemain = 0;
		int bucketToDelete = 0;
		boolean bucketToRemain_Accepted = false;
		boolean bucketToDelete_Accepted = false;

		while (bucketToRemain_Accepted == false) {
			System.out.println("\nSelect which bucket will remain: ");

			try {
				bucketToRemain = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			if (bucketToRemain <= 0 || bucketToRemain > totalBuckets) {
				System.out.println("\nInvalid bucket selected. Try again.");
			} else
				bucketToRemain_Accepted = true;
		}
		while (bucketToDelete_Accepted == false) {
			System.out.println("\nSelect which bucket will be deleted: ");
			try {
				bucketToDelete = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}
			if (bucketToDelete <= 0 || bucketToDelete > totalBuckets) {
				System.out.println("\nInvalid bucket selected. Try again.");
			} else
				bucketToDelete_Accepted = true;
		}
		mergeObj.mergeTheBuckets(s3, bucketToRemain, bucketToDelete);
	}

	public static void splitBucketClient(AmazonS3 s3) {
		listContentsObj.listContents(s3);
		int totalBuckets = listContentsObj.totalNumberOfBuckets(s3);
		int bucketToSplit = 0;
		int itemSplitPoint = 0;
		boolean bucketToSplit_Accepted = false;
		boolean itemSplitPoint_Accepted = false;

		while (bucketToSplit_Accepted == false) {
			System.out.println("\nSelect which bucket to split:");

			try {
				bucketToSplit = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			if (bucketToSplit <= 0 || bucketToSplit > totalBuckets) {
				System.out.println("\nInvalid bucket selected. Try again.");
			} else
				bucketToSplit_Accepted = true;
		}

		int totalNumberOfItems = listContentsObj.totalNumberOfItemsInsideBucket(s3, bucketToSplit);

		while (itemSplitPoint_Accepted == false) {
			System.out.println("\nAt which item should the bucket be split? (Inclusive)");

			// Save the selected item number
			try {
				itemSplitPoint = Integer.parseInt(input.readLine());
			} catch (Exception e) {
			}

			if (itemSplitPoint <= 0 || itemSplitPoint > totalNumberOfItems) {
				System.out.println("\nSelected item does not exist, try again.");
			} else
				itemSplitPoint_Accepted = true;
		}
		splitObj.splitTheBuckets(s3, bucketToSplit, itemSplitPoint);
	}

}
