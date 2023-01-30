package com.in28minutes.microservices.namingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/*
Now we've entire chain working currency conversion service, currency exchange service, talking to in-memory database.
However, there is still a small problem in the CurrencyExchangeProxy interface of currency conversion microservice. We are hard coding the URL of the currency exchange service.

So if I would want to get the currency conversion service to talk to a different instance of currency exchange service, we need to go there and
change the URL - 8001, 8002 and so on. Instead, we want to be able to dynamically launch currency exchange instances and distribute load between them.

Right now, let's say one instance is on port 8000, one is on 8001 and another is on 8002. As instances come up and go down, we'd want to be able to automatically
discover them and load balance between them. Feign provide an option where you can hardcode multiple URLs in there using semicolons in between but even that would
not be a good solution because let's say 8000 went down and a new instance was brought up on 8002 then you have to change the configuration of this application
or the code of this application all the time. And that's the reason why we go for something called a service registry or a Naming server.

What would happen is in a microservice architecture, all the instances of all the microservices would register with a service registry.
So the currency conversion microservice would register with the service registry, the currency exchange microservice would register with the service registry
and all the other microservices also would register with this service registry.

And let's say the currency conversion microservice wants to talk to the currency exchange microservice, it would ask the service registry to return addresses of the currency exchange microservice.
The service registry would return those back to the currency conversion microservice and then the currency conversion microservice can send the request out to the currency exchange microservice.
So all the instances would register with the naming server or the service registry.

And whenever currency conversion microservice wants to find out what are the active instances, it asks the naming server, gets the instances and load balances between them.
Let's get started with creating a Naming Server. One of the options that Spring Cloud provides is Eureka.

We've added following dependencies while creating this project via Spring Initializer:-

1) Spring Boot DevTools
2) Spring Boot Actuator
3) Eureka Server - For running a Eureka naming server.

We want to run a Eureka naming server, and we want to create a server. So we need to choose Eureka Server dependency. It is the one which provides Spring Cloud Discovery.
So we've chosen spring-cloud-netflix Eureka Server.

For us to be able to use Eureka server, we need to do add in an annotation @EnableEurekaServer in our NamingServerApplication class.
Before launching the class up, we would need to configure name of the application in application.properties.
Giving it a good name is very important. So I'll call this naming-server and the port.server = 8761 i.e. port that we want to use for the naming server is 8761.

There are a couple of other configurations that are usually recommended by Eureka. What we are creating in here is a Eureka server and we don't want to register with itself.

And for we would want to add a couple of properties in our application.properties so we don't want this specific server to register with itself -

		eureka.client.register-with-eureka=false
       	eureka.client.fetch-registry=false

Make sure that you have both of these configured. And then, we can go ahead and launch our application.
And finally see Eureka Server on the port is 8761 i.e. go to url - localhost:8761
You can see that right now there is nothing registered with Eureka. We'll get the Currency Exchange Service and the Currency Conversion Service to talk to Eureka.



In the previous step, we set up the naming server. In this step let's connect our microservices to the naming server.

We will need to connect the currency conversion microservice and the currency exchange microservice connect to them, and afterwards we make the currency conversion
talk to currency exchange through the naming server. In this step, let's focus on the first step i.e. get the currency exchange microservice and the currency conversions microservice to register with the naming server.

All that you need to do is add dependency for eureka naming server in pom.xml of both currency exchange service and the currency conversion service
with org.springframework.cloud groupId and spring-cloud-starter-netflix-eureka-client artfactId. Remember to add this dependency in both microservices.


And now what I would do is actually stop both the currency exchange application and currency conversion application. Just leave naming-server running.
So the only application that I have running right now is the naming server.

Now launch one of them first. So let's launch the currency exchange service application on 8000.
And if you refresh Eureka server now, i.e. refresh localhost:8761, you can see that currency exchange is now registered with Eureka.

All that we needed to do was add Eureka client dependency and that's all is needed for you to connect with Eureka.

However, to be really safe, we would typically actually configure the naming server URL directly in the application.properties of microservices.
So we can configure the naming server URL in the application.properties as well like -

	eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka

This would also register with Eureka again. And when you refresh naming server url, you should see that everything is fine.
So it's just the same, even though this is the default url. Later, we might want to connect to other Eureka servers and having this property here would be really clear.

Let's open up the application.properties of the currency conversion service and add this property in there as well and launch up the currency conversion service as well.

And let's refresh the naming server url - localhost:8761.
Now you can see currency conversion and currency exchange. Both of them are now registered. You can see the URLs as well.
Currency exchange is on port 8000. Currency conversion is on port 8100. If you go to the naming server logs. You'd see similar logs in there as well.

Each time an application is registered and unregistered so status up is when it's coming up and status down is when it's going down.
So each time when you restart the application first it would be unregistered, and then it would be registered again.

In the step we registered our currency conversion microservice and the currency exchange microservice with our service registry or the naming server.
Next step, let's try and load balance between multiple instances of the currency exchange from the currency conversion service.
 */
@SpringBootApplication
@EnableEurekaServer
public class NamingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamingServerApplication.class, args);
	}

}
