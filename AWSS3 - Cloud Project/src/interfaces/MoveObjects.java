package interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/** Interface for when an object is to be moved */
@WebService
@SOAPBinding(style = Style.RPC)
public interface MoveObjects {

	/** Moves an object from one bucket to another */
	public void moveTheObjects(int bucketToMoveFrom, int itemToMove, int bucketToMoveTo);

	// /** Checks for duplicate items within the new bucket and renames it */
	// public String checkForDuplicateItems(String objectKeyChecker);

}
