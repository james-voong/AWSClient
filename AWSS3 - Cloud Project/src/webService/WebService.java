package webService;

import interfaces.ListBucketContents;
import interfaces.MergeBuckets;
import interfaces.MoveObjects;
import interfaces.SplitBuckets;

public interface WebService extends ListBucketContents, MergeBuckets, MoveObjects, SplitBuckets {

}
