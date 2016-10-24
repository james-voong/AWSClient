package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.amazonaws.services.s3.AmazonS3;

/** Interface for when an object is to be moved */
@WebService
@SOAPBinding(style = Style.RPC)
public interface MoveObjects {

	/** Moves an object from one bucket to another */
	public void moveTheObjects(AmazonS3 s3, int bucketToMoveFrom, int itemToMove, int bucketToMoveTo);

}
