package info.spark.starter.sms.yimei;

import com.google.common.collect.Maps;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.sms.yimei.constant.MethodParamName;
import info.spark.starter.sms.yimei.enums.YimeiMethodEnum;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.core.util.EnumUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.core.util.UrlUtils;
import info.spark.starter.sms.SmsClient;
import info.spark.starter.sms.exception.SmsException;

import info.spark.starter.sms.yimei.constant.YimeiConsts;
import info.spark.starter.sms.yimei.entity.MoDTO;
import info.spark.starter.validation.util.RegexUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

import static info.spark.starter.sms.yimei.constant.YimeiConsts.PREFIX_MAX_LEN;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.02 17:57
 * @since 1.0.0
 */
@Slf4j
public class YimeiSmsClient implements SmsClient<YimeiSmsMessage> {

    /** config */
    private final YimeiConfig config;
    /** restTemplate */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Yimei sms client
     *
     * @param yimeiConfig yimei config
     * @since 1.0.0
     */
    @Contract(pure = true)
    public YimeiSmsClient(@NotNull YimeiConfig yimeiConfig) {
        this.check(yimeiConfig);
        this.config = yimeiConfig;
    }

    /**
     * 使用 URL 检查 url 是否为合法的 url, 不合法将抛出异常, 并将最后的 / 删除 (如果存在的话)
     *
     * @param yimeiConfig yimei config
     * @since 1.0.0
     */
    private void check(@NotNull YimeiConfig yimeiConfig) {
        String urlString = yimeiConfig.getUrl();
        // 删除末尾的 /
        if (urlString.endsWith(StringPool.SLASH)) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        yimeiConfig.setUrl(urlString);
    }

    /**
     * Gets mo *
     *
     * @return the mo
     * @since 1.0.0
     */
    public List<MoDTO> getMo() {
        List<MoDTO> moList = new ArrayList<>();
        Map<String, String> params = Maps.newHashMapWithExpectedSize(2);
        params.put(MethodParamName.CD_KEY, this.config.getAppId());
        params.put(MethodParamName.PASSWORD, this.config.getAppKey());
        // 构造请求URL
        String reqUrl = this.buildGetUrl(this.config.getUrl() + YimeiMethodEnum.GET_MO.getMethod(), params);
        // 发起get请求发送短信
        String response = this.invokeGet(reqUrl, String.class);
        Document document = XmlUtil.readXML(response);
        Element rootElement = document.getDocumentElement();
        // 所有子节点
        NodeList childNodes = rootElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equalsIgnoreCase(MethodParamName.MESSAGE)) {
                // 三级节点
                NodeList subChildNodes = node.getChildNodes();
                MoDTO mo = MoDTO.builder().build();
                for (int j = 0; j < subChildNodes.getLength(); j++) {
                    Node item = subChildNodes.item(j);
                    String nodeName = item.getNodeName();
                    switch (nodeName) {
                        case MethodParamName.SRC_TERM_ID:
                            mo.setMobileNumber(item.getFirstChild().getNodeValue());
                            break;
                        case MethodParamName.MSG_CONTENT:
                            mo.setSmsContent(item.getFirstChild().getNodeValue());
                            break;
                        case MethodParamName.ADD_SERIAL:
                            mo.setAddSerial(item.getFirstChild().getNodeValue());
                            break;
                        case MethodParamName.ADD_SERIAL_REV:
                            mo.setAddSerialRev(item.getFirstChild().getNodeValue());
                            break;
                        case MethodParamName.SEND_TIME:
                            mo.setSentTime(item.getFirstChild().getNodeValue());
                            break;
                        default:
                            log.warn("unknown xml element [{}]", nodeName);
                    }
                }
                moList.add(mo);
            }
        }

        return moList;
    }

    /**
     * Send message *
     *
     * @param content content
     * @since 1.0.0
     */
    @Override
    public void sendMessage(@NotNull YimeiSmsMessage content) {
        if (this.unavailable(this.config, content)) {
            return;
        }
        // 检查手机号数量
        BaseCodes.PARAM_VERIFY_ERROR.isFalse(CollectionUtils.isEmpty(content.getPhone()), "接收消息的手机号不能为空");
        // 检查每个号码
        boolean allMatch = content.getPhone().stream().allMatch(p -> RegexUtils.match(RegexUtils.PHONE, p));
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(allMatch, "手机号不是正确的手机号");
        // 前缀为空或者不为空时长度最多为10
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(StringUtils.isBlank(content.getCompany())
                                            || (StringUtils.isNotBlank(content.getCompany())
                                                && content.getCompany().length() <= PREFIX_MAX_LEN),
                                            "短信模板里的公司名长度应小于等于10");
        Map<String, String> params = Maps.newHashMapWithExpectedSize(8);
        params.put(MethodParamName.CD_KEY, this.config.getAppId());
        params.put(MethodParamName.PASSWORD, this.config.getAppKey());
        params.put(MethodParamName.PHONE, String.join(",", content.getPhone()));
        params.put(MethodParamName.MESSAGE, StringUtils.isBlank(content.getCompany())
                                            // 为空，则使用默认的
                                            ? YimeiConsts.MSG_CONTENT_PREFIX + content.getContent()
                                            : YimeiConsts.FRONT_CHAR
                                                .concat(content.getCompany())
                                                .concat(YimeiConsts.BEHIND_CHAR)
                                                .concat(content.getContent()));
        params.put(MethodParamName.SEQ_ID, String.valueOf(content.getMessageId()));
        params.put(MethodParamName.SMS_PRIORITY, StringPool.ONE);

        // 构造请求URL
        String reqUrl = this.buildGetUrl(this.config.getUrl() + YimeiMethodEnum.SEND_SMS.getMethod(), params);
        // 发起get请求发送短信
        String response = this.invokeGet(reqUrl, String.class);
        log.info("yimei sms response: {}", response);
        int resCode = this.parseSendResponse(response);
        YimeiSmsCodes smsCodes = EnumUtils.of(YimeiSmsCodes.class, q -> q.getCode() == resCode)
            .orElseThrow(() -> YimeiSmsCodes.UNKNOWN.newException(resCode));
        if (smsCodes != YimeiSmsCodes.OK) {
            throw new SmsException(smsCodes);
        }
    }

    /**
     * Parse send response int
     *
     * @param result result
     * @return the int
     * @since 1.0.0
     */
    private int parseSendResponse(String result) {
        String regex = "<error>([^<]+)</error>";
        Matcher m = Pattern.compile(regex).matcher(result);
        if (m.find()) {

            return Integer.parseInt(m.group(1));
        }
        return YimeiSmsCodes.UNKNOWN.getCode();
    }


    /**
     * 创建发送短信的请求URL
     *
     * @param url    url
     * @param params params
     * @return the string
     * @since 1.0.0
     */
    private String buildGetUrl(String url, Map<String, String> params) {
        String urlParamsByMap = UrlUtils.buildUrlParamsByMap(params);
        // 拼接URL和参数
        url += StringPool.QUESTION_MARK + urlParamsByMap;
        return url;
    }

    /**
     * 查询错误全部返回 null, 只有 200 状态才返回实体数据
     *
     * @param <T>           parameter
     * @param url           url
     * @param responseClass response class
     * @return the string
     * @since 1.0.0
     */
    private <T> T invokeGet(String url, Class<T> responseClass) {
        ResponseEntity<T> resEntity = this.restTemplate.getForEntity(url, responseClass);
        log.debug("yimei api result: [{}], url: [{}]", resEntity, url);
        if (HttpStatus.OK.equals(resEntity.getStatusCode())) {
            return resEntity.getBody();
        } else {
            YimeiSmsCodes smsCode = EnumUtils.of(YimeiSmsCodes.class, g -> g.getCode() == resEntity.getStatusCodeValue())
                .orElseThrow(YimeiSmsCodes.UNKNOWN::newException);
            throw new SmsException(smsCode);
        }
    }
}
