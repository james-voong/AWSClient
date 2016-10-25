package publisher;

import javax.xml.ws.Endpoint;
import implementationClasses.ListBucketContentsImpl;
import implementationClasses.MoveObjectsImpl;
import implementationClasses.MergeBucketsImpl;
import implementationClasses.SplitBucketsImpl;

public class Publisher {

	public static void main(String[] args) {
		Endpoint.publish("http://localhost:8080/ListBucketContents", new ListBucketContentsImpl());
		Endpoint.publish("http://localhost:8080/MoveObjects", new MoveObjectsImpl());
		Endpoint.publish("http://localhost:8080/MergeBuckets", new MergeBucketsImpl());
		Endpoint.publish("http://localhost:8080/SplitBuckets", new SplitBucketsImpl());

		System.out.println("Service is working");
	}
}
