package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/** An interface for merging two buckets into one */
@WebService
@SOAPBinding(style = Style.RPC)
public interface MergeBuckets {

	/** Merges two buckets into one */
	public void mergeTheBuckets(int bucketToRemain, int bucketToDelete);

}
