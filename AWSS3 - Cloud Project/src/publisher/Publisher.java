package publisher;

import javax.xml.ws.Endpoint;

import webService.WebServiceImpl;

public class Publisher {

	public static void main(String[] args) {

		Endpoint.publish("http://localhost:8080/WebService", new WebServiceImpl());

		System.out.println("Service is working");
	}
}
