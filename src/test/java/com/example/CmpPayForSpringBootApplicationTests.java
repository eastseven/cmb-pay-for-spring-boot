package com.example;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CmpPayForSpringBootApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(CmpPayForSpringBootApplicationTests.class);

    @Autowired CmbPayProperty cmbPayProperty;

    @Test
    public void contextLoads() {
        //app_CSKmb1vLSKyTKKez
        Pingpp.apiKey = "sk_test_Lij1aTO4ajfH84KG48WXjTG8";

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("order_no", "0" + DateTime.now().toString("HHmmssSSS"));
        chargeParams.put("amount", 2);
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", "app_CSKmb1vLSKyTKKez");
        chargeParams.put("app", app);
        chargeParams.put("channel", "cmb_wallet");
        chargeParams.put("currency", "cny");
        chargeParams.put("client_ip", "127.0.0.1");
        chargeParams.put("subject", "一网通测试订单");
        chargeParams.put("body", "Your Body");

        Map<String, Object> extra = new HashMap<String, Object>();
        extra.put("result_url", cmbPayProperty.getMerchantRetUrl());
        extra.put("p_no", DateTime.now().toDate().getTime());
        extra.put("seq", DateTime.now().toDate().getTime());
        extra.put("m_uid", DateTime.now().toDate().getTime());
        extra.put("mobile", "13500007111");
        chargeParams.put("extra", extra);

        log.info("{}", chargeParams);

        try {
            Charge charge = Charge.create(chargeParams);
            Assert.assertNotNull(charge);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        } catch (ChannelException e) {
            e.printStackTrace();
        }
    }

}
