package com.watermelon;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.alidns20150109.models.UpdateDomainRecordResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String ipify = "https://api.ipify.org?format=textg?format=text";

    private static final String endpoint = "alidns.cn-hangzhou.aliyuncs.com";

    private static final String file = "ipv4";

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

    public static String getPreviousIpv4() {
        Path path = Paths.get(file);
        boolean exist = Files.exists(path);
        if (!exist) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        } else {
            try {
                return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void refreshPreviousIpv4(String ipv4) {
        try {
            log.info("Saving new IP Address ", ipv4);
            Files.write(Paths.get(file), ipv4.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        log.info("Task start.");
        log.info("CURL {} ...", ipify);
        String ipv4 = retrieveIPV4();
        /**
         * 如果ipv4发生改变，则更新
         */
        String previousIpv4 = getPreviousIpv4();
        log.info("Current IP address:{},Previous IP address:{}", ipv4, previousIpv4);
        if (StringUtils.equals(ipv4, previousIpv4)) {
            log.info("IP address remains unchanged, no need for an update.");
            return;
        }

        log.info("The new IP address is different from the previous one.");

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
        try {
            log.info("Update domain record,waiting...");
            UpdateDomainRecordResponse resp = client.updateDomainRecordWithOptions(updateDomainRecordRequest, runtime);
            log.info("Update success,{}", Common.toJSONString(resp));
            log.info(" Start saving the new IP address.");
            refreshPreviousIpv4(ipv4);
        } catch (Exception e) {
            log.info("Update fail,{}", e.getMessage());
        } finally {
            log.info("Task end.");
        }
    }


}