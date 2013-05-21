package org.motechproject.spike;

import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class SmppService {

    @Autowired
    private Properties smppProperties;

    public void sendMessage() throws Exception {
        final String gatewayId = "SMPP_GATEWAY";
        final String name = smppProperties.getProperty("smpp.name");
        final String password = smppProperties.getProperty("smpp.password");
        final String host = smppProperties.getProperty("smpp.host");
        final int port = Integer.valueOf(smppProperties.getProperty("smpp.port"));
        final BindAttributes bindAttributes = new BindAttributes(name, password, null, BindAttributes.BindType.TRANSCEIVER);

        JSMPPGateway jsmppGateway = new JSMPPGateway(gatewayId, host, port, bindAttributes);

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
    }
}
