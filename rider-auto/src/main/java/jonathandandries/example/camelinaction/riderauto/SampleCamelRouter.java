/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jonathandandries.example.camelinaction.riderauto;

import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import jonathandandries.example.camelinaction.riderauto.model.Order;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.spi.DataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author jonathandandries
 */
@Component
public class SampleCamelRouter extends RouteBuilder {
    
    @Bean
    JmsComponent jms() {
        // Spring will automatically inject this Bean into CamelContext for us.
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        // Need to setTrustedPackagers for JMS because were failing to pull Order.class off the JMS queue because it's not a "Trusted Class".
        factory.setTrustedPackages(Arrays.asList("jonathandandries.example.camelinaction.riderauto.model"));
        return JmsComponent.jmsComponentAutoAcknowledge(factory);
    }
    
    @Override
    public void configure() throws Exception {
        DataFormat jaxbFormat = new JaxbDataFormat(JAXBContext.newInstance(jonathandandries.example.camelinaction.riderauto.model.Order.class));

        from("file:data/placeorder?delete=true&exclude=.gitkeep")
                .routeId("FileToJMS")
                .log("Body: ${body}")
                .to("jms:incomingOrders")
                .to("file:data");

        from("jetty://http://0.0.0.0:8888/placeorder")
                .routeId("HTTPtoJMS")
                .inOnly("jms:incomingOrders")
                .log("Body: ${body}")
                .transform().constant("OK");

        from("jms:incomingOrders")
                .routeId("NormalizeMessageData")
                .convertBodyTo(String.class)
                .choice()
                .when().simple("${body} contains '?xml'")
                    .unmarshal(jaxbFormat)
                    .log("XML: ${body.name} | ${body.amount}")
                    .to("jms:orders")
                .otherwise()
                    .unmarshal().bindy(BindyType.Csv, Order.class)
                    .log("CSV: ${body.name} | ${body.amount}")
                    .to("jms:orders");
                
        from("jms:orders")
                .routeId("BackendStub")
                .convertBodyTo(Order.class)
                .log("Received order for ${body.amount} of ${body.name}");

    }
}
