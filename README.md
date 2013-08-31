JMS Receiver Demo
=================

Demonstrates a few different approaches and deployment models for server 
components that receive and act on JMS messages, but don't necessarily have 
any web front end.

The demo application is essentially "Hello, World", but with a few twists.
A servlet provides a very basic UI that displays a greeting.  On a GET
request to the servlet, it uses a ```GreetingService``` bean to send a 
message containing the name of the person to greet, via a designated 
JMS queue.  The GET request is marked for asynchronous handling; when a 
reply is received on the designated JMS queue, the greeting it contains 
is returned as the response to the GET request.  When the greeting service
sends a greeting request, it sets the JMS reply-to header so that it
references the designated reply queue.

The application contains three different components that handle greeting
requests on the designated JMS queue.  All three components are deployed
in the same application, so they will effectively load-share; each will
handle some subset of the received requests.  Each component represents
a distinct approach to constructing and deploying a message receiver
component.

The first and most straightforward approach uses an EJB-jar module 
containing a message-driven bean.  This approach is represented in the 
```jms-rx-mdb``` module.  In this module, we simply create a POJO that 
implements ```javax.jms.MessageListener``` and annotated it using 
```@MesssageDriven```.  The container arranges to deliver greeting requests
to our bean as they arrive on the queue.  In this approach, we use JAXB to 
handle message marshalling tasks, and we work directly with the JMS API to 
send the greeting response.  Apart from some of the typical, boilerplate
connection and session setup tasks, imposed by the JMS API, this approach
is really pretty simple.

The second approach (in the ```jms-rx-mdb-spring``` module) improves on 
the first approach, by taking advantage of some Spring components which 
are injected into our messsage-driven bean.  Our bean is annotated using
```@Interceptor``` that references Spring's ```SpringBeanAutowiringInterceptor```.
This interceptor loads a Spring application context and uses it to 
inject dependencies annotated using Spring's ```@Autowired``` annotation.
This allows us to inject a ```JmsTemplate``` and a ```MessageConverter```,
which are used to eliminate most of the JMS-imposed boilerplate, and to
help take care of message marshalling tasks.

The third approach (illustrated in the ```jms-rx-ra``` module) is radically 
different from the previous two.  In this approach, instead of using a
message-driven bean in an EJB-jar, we use a resource adapter (RAR) that
utilizes Spring's ```SpringContextResourceAdapter``` to load an
application context.  The application context creates an instance of
Spring's ```SimpleMessageListenerContainer``` which serves the same role
as our message driven bean -- namely, it listens to the greeting request
queue.  This container is configured with a reference to our 
```GreetingRequestReceiver``` which implements the 
```javax.jms.MessageListener``` interface. As messages are received, 
the message listener container delegates message handling to our receiver.
Our receiver is injected with a reference to a ```JmsTemplate``` and 
```MessageConverter``` which is uses to eliminate JMS-imposed boilerplate
and to handle message marshalling tasks.

One potentially significant disadvantage of the latter approach is that
we cannot utilize other deployed components.  For example, in the first
two approaches our message-driven bean is injected with a reference to 
a stateless session bean that provides a repository of greeting templates.
As a resource adapter component, our third approach cannot make use of
the repository component.





