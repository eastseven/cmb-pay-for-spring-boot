package com.example;

import cmb.MerchantCode;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@SpringBootApplication
public class CmpPayForSpringBootApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(CmpPayForSpringBootApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(CmpPayForSpringBootApplication.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmbPayProperty cmbPayProperty;

    MultiValueMap<String, String> combine(String pno) {
        String strKey = cmbPayProperty.getMerchantKey();
        String strDate = DateTime.now().toString("yyyyMMdd");
        String strBranchID = cmbPayProperty.getBranchId();
        String strCono = cmbPayProperty.getCono();
        String strBillNo = DateTime.now().toString("yyMMddHHmm");
        String strAmount = "0.01";
        String strMerchantUrl = cmbPayProperty.getMerchantUrl();
        String strMerchantPara = "pno=" + pno + "|billNo=" + strBillNo;
        String strMerchantRetUrl = cmbPayProperty.getMerchantRetUrl();
        String strMerchantRetPara = "pno=" + pno + "|billNo=" + strBillNo;

        String strPayerID = "";//买家
        String strPayeeID = "";//卖家
        String strClientIP = "";
        String strGoodsType = "54011600";

        String strReserved = "<Protocol>";
        strReserved += "<PNo>" + DateTime.now().toDate().getTime() + "</PNo>";
        strReserved += "<TS>" + DateTime.now().toString("yyyyMMddHHmmss") + "</TS>";
        strReserved += "<MchNo>" + cmbPayProperty.getMchNo() + "</MchNo>";
        strReserved += "<Seq>" + DateTime.now().toDate().getTime() + "</Seq>";
        strReserved += "<URL>" + cmbPayProperty.getMerchantNotify() + "</URL>"; // 协议开通结果通知命令请求地址,签约结果回调
        strReserved += "</Protocol>";


        String merchantCode = MerchantCode.genMerchantCode(strKey, strDate, strBranchID, strCono, strBillNo, strAmount, strMerchantPara, strMerchantUrl, strPayerID, strPayeeID, strClientIP, strGoodsType, strReserved/**/);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("BranchID", strBranchID);
        map.add("CoNo", strCono);
        map.add("BillNo", strBillNo);
        map.add("Amount", strAmount);
        map.add("Date", strDate);
        map.add("MerchantUrl", strMerchantUrl);
        map.add("MerchantPara", strMerchantPara);
        map.add("MerchantRetUrl", strMerchantRetUrl);
        map.add("MerchantRetPara", strMerchantRetPara);
        map.add("MerchantCode", merchantCode);
        map.add("ExpireTimeSpan", cmbPayProperty.getExpireTimeSpan());
        log.debug("{}", map);

        return map;
    }

    @ResponseBody
    @GetMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
    public Object pay(HttpServletRequest request) {
        log.info("\n\t===== CMB Pay =====\n\tgetRemoteAddr={}\n\tgetRemoteHost={}\n\tgetRemotePort={}\n\tgetRequestURI={}\n\tgetRequestedSessionId={}\n\n\n", request.getRemoteAddr(), request.getRemoteHost(), request.getRemotePort(), request.getRequestURI(), request.getRequestedSessionId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> _request = new HttpEntity(combine("02885191558"), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(cmbPayProperty.getGateway(), _request, String.class);
        String cmb = response.getBody();
        log.debug("\n\n{}\n", cmb);
        //if (null == cmb) return new ResponseEntity<>("操作失败", HttpStatus.NO_CONTENT);
        //log.debug("cmb={}", cmb);
        return cmb;
    }

    @ResponseBody
    @GetMapping("/ppp")
    public Object ppp() {
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

        final long value = DateTime.now().toDate().getTime();
        Map<String, Object> extra = new HashMap<String, Object>();
        extra.put("result_url", cmbPayProperty.getMerchantRetUrl());
        extra.put("p_no", value);
        extra.put("seq", value);
        extra.put("m_uid", value);
        extra.put("mobile", "13500007111");
        chargeParams.put("extra", extra);

        log.info("{}", chargeParams);

        try {
            Charge charge = Charge.create(chargeParams);
            return charge;
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

        return null;
    }
}
