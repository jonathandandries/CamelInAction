/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jonathandandries.example.camelinaction.riderauto;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.springframework.stereotype.Component;

/**
 *
 * @author jonathandandries
 */
@Component
public class SampleCamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // If you do restConfiguration here, it will override the one in camel-context.xml.
        // We should create do this in an @Configuration class so that it is only done once.
//        RestConfigurationDefinition contextPath = restConfiguration()
//                .component("servlet")
//                .bindingMode(RestBindingMode.json)
//                .contextPath("/RiderAuto/api/v1")
//                .apiContextPath("/api-doc");
//
//        rest().get("/hello")
//                .to("direct:hello");
//
//        from("direct:hello")
//                .log(LoggingLevel.INFO, "Hello World")
//                .transform().simple("Hello World");
        from("file:target/placeorder")
                .to("jms:incomingOrders");
        
        from("jetty://http://0.0.0.0:8888/placeorder")
                .inOnly("jms:incomingOrders")
                .transform().constant("OK");
        
        from("jms:incomingOrders")
                .convertBodyTo(String.class)
                .choice()
                .when().simple("${body} contains '?xml'")
                .log("XML")
                .log("${body}")
                .to("jms:orders")
                .otherwise()
                .log("CSV?")
                .log("${body}")
                .to("jms:orders");
    }
}
