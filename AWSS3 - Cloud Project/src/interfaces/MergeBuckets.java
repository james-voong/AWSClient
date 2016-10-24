package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.amazonaws.services.s3.AmazonS3;

/** An interface for merging two buckets into one */
@WebService
@SOAPBinding(style = Style.RPC)
public interface MergeBuckets {

	/** Merges two buckets into one */
	public void mergeTheBuckets(AmazonS3 s3, int bucketToRemain, int bucketToDelete);

}
