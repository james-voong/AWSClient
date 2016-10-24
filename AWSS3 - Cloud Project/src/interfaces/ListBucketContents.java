package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.amazonaws.services.s3.AmazonS3;

/** An interface used for obtaining information about the buckets */
@WebService
@SOAPBinding(style = Style.RPC)
public interface ListBucketContents {

	/** Lists all buckets along with all objects inside them */
	public void listContents(AmazonS3 s3);

	/** Returns the total number of buckets */
	public int totalNumberOfBuckets(AmazonS3 s3);

	/** Returns the total number of items inside a given bucket */
	public int totalNumberOfItemsInsideBucket(AmazonS3 s3, int bucketNumber);
}
