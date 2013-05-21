package org.motechproject.spike;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class TestController {

    @ResponseBody
    @RequestMapping("/http")
    private String http() throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://www.google.com");
        final int responseCode = client.executeMethod(method);
        return "HTTP status code: " + responseCode;
    }

    @ResponseBody
    @RequestMapping("/smpp")
    private String smpp() throws Exception {
        final String gatewayId = "smpp_gateway";
        final BindAttributes bindAttributes = new BindAttributes("motech", "motech", null, BindAttributes.BindType.TRANSCEIVER);

        JSMPPGateway jsmppGateway = new JSMPPGateway(gatewayId, "localhost", 2775, bindAttributes);

        final Service service = Service.getInstance();
        service.addGateway(jsmppGateway);
        service.startService();

        service.setOutboundMessageNotification(new IOutboundMessageNotification() {
            @Override
            public void process(AGateway aGateway, OutboundMessage outboundMessage) {
                System.out.println("Outbound message notification. " + outboundMessage);
                try {
                    service.stopService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        OutboundMessage outboundMessage = new OutboundMessage();
        outboundMessage.setRecipient("12345");
        outboundMessage.setText("Hola Mundo...");
        outboundMessage.setGatewayId(gatewayId);

        service.queueMessage(outboundMessage);

        return "SMS sent over SMPP.";
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new TestController().smpp());
    }
}
