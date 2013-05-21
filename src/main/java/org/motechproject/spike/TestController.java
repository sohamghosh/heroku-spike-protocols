package org.motechproject.spike;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class TestController {

    @Autowired
    private SmppService smppService;

    @ResponseBody
    @RequestMapping("/smpp")
    private String smpp() throws Exception {
        smppService.sendMessage();
        return "SMS sent over SMPP.";
    }

    @ResponseBody
    @RequestMapping("/http")
    private String http() throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://www.google.com");
        final int responseCode = client.executeMethod(method);
        return "HTTP status code: " + responseCode;
    }
}
