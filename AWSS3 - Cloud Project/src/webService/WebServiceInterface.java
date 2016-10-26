package webService;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import interfaces.ListBucketContents;
import interfaces.MergeBuckets;
import interfaces.MoveObjects;
import interfaces.SplitBuckets;

/**
 * This interface is used to bundle ListBucketContents, MergeBuckets,
 * MoveObjects and SplitBuckets
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface WebServiceInterface extends ListBucketContents, MergeBuckets, MoveObjects, SplitBuckets {

	public void instantiateClient();

}
