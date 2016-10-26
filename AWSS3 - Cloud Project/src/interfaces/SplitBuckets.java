package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/** Interface for methods related to splitting one bucket into two */
@WebService
@SOAPBinding(style = Style.RPC)
public interface SplitBuckets {

	/** Splits a bucket into two */
	public String splitTheBuckets(int bucketToSplit, int itemSplitPoint);

}
