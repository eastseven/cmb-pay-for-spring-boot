package com.example;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dongqi on 16/9/22.
 */

@Data
@Component
@ConfigurationProperties(prefix = "cmb.pay")
public class CmbPayProperty {

    private String gateway;
    private String branchId;
    private String cono;
    private String merchantKey = "";
    private String pno;
    private String mchNo;
    private String expireTimeSpan;
    private String merchantUrl;
    private String merchantRetUrl;
    private String merchantNotify;
    private String orderUrl;
}
