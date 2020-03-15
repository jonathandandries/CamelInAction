# Rider Order Frontend

Spring Boot + Fuse/Camel implementation of the "Rider Order Frontend" described
in the article here:

[Open Source Integration With Apache Camel and How Fuse IDE Can Help](https://dzone.com/articles/open-source-integration-apache) by Jonathan Anstey, August 14, 2019.

Note that my implementation is different than the base article in two important ways:
 1. I'm using the [Red Hat Fuse v7.1](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.1/html/) packaging of [Apache Camel](https://camel.apache.org/) for Spring Boot, and
 2. I'm using Java DSL instead of XML to specify my routes.

Quoting the description of the problem:

    The example in this article is based on a fictional motorcycle parts business 
    used throughout the Camel in Action book. The company, named Rider Auto Parts, 
    supplies parts to motorcycle manufacturers. Over the years, they’ve changed the 
    way they receive orders several times. Initially, orders were placed by 
    uploading comma-separated value (CSV) files to an FTP server. The message format
    was later changed to XML. Currently, they provide a website through which orders
    are submitted as XML messages over HTTP.
    
    Rider Auto Parts asks new customers to use the web interface to place orders, 
    but because of service level agreements (SLAs) with existing customers, they 
    must keep all the old message formats and interfaces up and running. All of 
    these messages are converted to an internal Plain Old Java Object (POJO) format 
    before processing.
    
        1. There are two Message Endpoints; one for FTP connectivity and another 
           for HTTP.
        2. Messages from these endpoints are fed into the incomingOrders Message 
           Channel
        3. The messages are consumed from the incomingOrders Message Channel and 
           routed by a Content-Based Router to one of two Message Translators. As 
           the EIP name implies, the routing destination depends on the content of 
           the message. In this case, we need to route based on whether the 
           content is a CSV or XML file.
        4. Both Message Translators convert the message content into a POJO, which 
           is fed into the orders Message Channel.

## Running on your machine

First, obtain the project (git clone or download) and enter the project's directory.

Because this is Spring Boot, you don't need to install any server software. All you need is [Java version 8](https://openjdk.java.net/install/) and [Apache Maven](https://maven.apache.org/). You can run this project from the command line:

```bash
# Build the project
mvn clean package

# Run the project
mvn spring-boot:run 
```

Creating orders to be processed:

```bash
# Send an XML order via the REST endpoint:
curl -d '<?xml version="1.0" encoding="UTF-8"?>
<order name="windshield wiper" amount="2"/>' http://localhost:8888/placeorder

# Move pre-created messages to the /data/placeorder folder
mv data/message* data/placeorder
```

## Technology Stack References:

1. [Red Hat Fuse v7.1](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.1/html/)
2. [Apache Camel v2.21.0](https://camel.apache.org/)
3. [Spring Boot v1.5.13](https://spring.io/projects/spring-boot)
3. [Apache Maven](https://maven.apache.org/)
4. [Java version 8](https://openjdk.java.net/install/)

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request.

## History

1. March 7, 2020 -- initial version.

## License

Gratis and libre.