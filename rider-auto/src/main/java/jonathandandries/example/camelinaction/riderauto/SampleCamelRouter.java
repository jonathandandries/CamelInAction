/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jonathandandries.example.camelinaction.riderauto;

import javax.xml.bind.JAXBContext;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.spi.DataFormat;
import org.springframework.stereotype.Component;

/**
 *
 * @author jonathandandries
 */
@Component
public class SampleCamelRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        DataFormat jaxbFormat = new JaxbDataFormat(JAXBContext.newInstance(jonathandandries.example.camelinaction.riderauto.model.Order.class));

        from("file:data/placeorder?delete=true")
                .routeId("FileToJMS")
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
                    .unmarshal().bindy(BindyType.Csv, jonathandandries.example.camelinaction.riderauto.model.Order.class)
                    .log("CSV: ${body.name} | ${body.amount}")
                    .to("jms:orders");

        // ToDo: Failing to pull Order.class off the JMS queue because it's not a "Trusted Class".
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
//        factory.setTrustAllPackages(true);
        
        
//        from("jms:orders")
//                .routeId("BackendStub")
//                .convertBodyTo(String.class)
//                .log("${body}");
//                .convertBodyTo(jonathandandries.example.camelinaction.riderauto.model.Order.class)
//                .log("${body.name} | ${body.amount}");

    }
}
