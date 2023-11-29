package com.watermelon;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.alidns20150109.models.UpdateDomainRecordResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String ipify = "https://api.ipify.org?format=textg?format=text";

    private static final String endpoint = "alidns.cn-hangzhou.aliyuncs.com";

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.alidns20150109.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = endpoint;
        return new com.aliyun.alidns20150109.Client(config);
    }

    /**
     * 从公网获取pv4地址
     *
     * @return
     * @throws IOException
     */
    public static String retrieveIPV4() throws IOException {
        try {
            URL url = new URL(ipify);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            // Parse JSON response to get public IP
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String ipv4 = response.toString();
            log.info("Public IP Address: " + ipv4);
            return ipv4;
        } catch (IOException e) {
            log.info("Failed to retrieve public IPv4 address. Please try again.");
            throw new IOException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        log.info("Task start.");
        log.info("CURL {} ...",ipify);
        String ipv4 = retrieveIPV4();

        AppConfig appConfig = new AppConfig();
        String accessKeyId = appConfig.getProperty("aliyun.access_key.id");
        String secret = appConfig.getProperty("aliyun.access_key.secret");
        String recordId = appConfig.getProperty("aliyun.access_key.record_id");
        Client client = createClient(accessKeyId, secret);
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                .setLang("en")
                .setRecordId(recordId)
                .setRR("@")
                .setType("A")
                .setValue(ipv4);

        RuntimeOptions runtime = new RuntimeOptions();
        try{
            log.info("Update domain record,waiting...");
            UpdateDomainRecordResponse resp = client.updateDomainRecordWithOptions(updateDomainRecordRequest, runtime);
            log.info("Update success,{}",Common.toJSONString(resp));
        }catch (Exception e){
            log.info("Update fail,{}",e.getMessage());
        }finally {
            log.info("Task end.");
        }
    }
}