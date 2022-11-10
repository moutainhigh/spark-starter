package info.spark.starter.dingtalk.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.dingtalk.config.DingtalkConfig;
import info.spark.starter.dingtalk.entity.ActionCardMessage;
import info.spark.starter.dingtalk.entity.DingtalkMessage;
import info.spark.starter.dingtalk.entity.FeedCardMessage;
import info.spark.starter.dingtalk.entity.LinkMessage;
import info.spark.starter.dingtalk.entity.MarkdownMessage;
import info.spark.starter.dingtalk.entity.TextMessage;
import info.spark.starter.dingtalk.service.DingtalkNotifyService;
import info.spark.starter.notify.exception.NotifyException;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.core.api.BaseCodes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Slf4j
public class DingtalkNotifyServiceImpl implements DingtalkNotifyService<DingtalkMessage<?>> {

    /** Config */
    private final DingtalkConfig config;

    /**
     * Dingtalk notify service
     *
     * @param config config
     * @since 2.1.0
     */
    @Contract(pure = true)
    public DingtalkNotifyServiceImpl(DingtalkConfig config) {
        this.config = config;
    }

    /**
     * Notify
     *
     * @param message message
     * @return the dingtalk message
     * @since 2.1.0
     */
    @Override
    @SuppressWarnings(value = {"checkstyle:UndefineMagicConstantRule", "PMD.UndefineMagicConstantRule"})
    public DingtalkMessage<?> notify(DingtalkMessage<?> message) {
        log.info("start send Dingtalk message.");
        Assertions.notNull(message, () -> new NotifyException("message 不允许为空"));

        String serverUrl = this.config.getWebhook();
        DingTalkClient client = new DefaultDingTalkClient(serverUrl);

        OapiRobotSendRequest request;
        switch (message.getMessageType()) {
            case TEXT:
                // 发送简单邮件
                request = this.buildTextResquest((TextMessage) message);
                break;
            case LINK:
                request = this.buildLinkResquest((LinkMessage) message);
                break;
            case MARKDOWN:
                request = this.buildMarkdownResquest((MarkdownMessage) message);
                break;
            case ACTION_CARD:
                request = this.buildActionCardResquest((ActionCardMessage) message);
                break;
            case FEED_CARD:
                request = this.buildFeedCardResquest((FeedCardMessage) message);
                break;
            default:
                throw BaseCodes.PARAM_VERIFY_ERROR.newException(new NotifyException("消息类型错误: type = " + message.getMessageType()));
        }

        try {
            OapiRobotSendResponse response = client.execute(request);
            // todo-dong4j : (2022.03.28 20:00) [重构错误代码, 这里直接忽略了代码检查]
            if ("130101".equals(response.getCode())) {
                log.error("消息发送失败：send too fast, exceed 20 times per minute");
                return message;
            } else {
                Assertions.isTrue(StringUtils.isNoneBlank(response.getErrmsg()) && "ok".equals(response.getErrmsg()),
                                  () -> new NotifyException(response.getErrmsg()));
            }
        } catch (ApiException e) {
            log.error("消息发送失败：", e);
            throw BaseCodes.FAILURE.newException(new NotifyException(e.getErrMsg()));
        }
        return message;
    }

    /**
     * Build text resquest
     *
     * @param message message
     * @return the oapi robot send request
     * @since 2.1.0
     */
    private @NotNull OapiRobotSendRequest buildTextResquest(@NotNull TextMessage message) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(message.getContent());
        request.setMsgtype(message.getMessageType().getDesc());
        request.setText(text);
        return request;
    }

    /**
     * Build link resquest
     *
     * @param message message
     * @return the oapi robot send request
     * @since 2.1.0
     */
    private @NotNull OapiRobotSendRequest buildLinkResquest(@NotNull LinkMessage message) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setTitle(message.getTitle());
        link.setText(message.getContent());
        link.setMessageUrl(message.getLink());
        link.setPicUrl(message.getUrl());

        request.setMsgtype(message.getMessageType().getDesc());
        request.setLink(link);

        return request;
    }

    /**
     * Build markdown resquest
     *
     * @param message message
     * @return the oapi robot send request
     * @since 2.1.0
     */
    private @NotNull OapiRobotSendRequest buildMarkdownResquest(@NotNull MarkdownMessage message) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(message.getTitle());
        markdown.setText(message.getContent());
        request.setMsgtype(message.getMessageType().getDesc());
        request.setMarkdown(markdown);
        return request;
    }

    /**
     * Build action card resquest
     *
     * @param message message
     * @return the oapi robot send request
     * @since 2.1.0
     */
    private @NotNull OapiRobotSendRequest buildActionCardResquest(@NotNull ActionCardMessage message) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        OapiRobotSendRequest.Actioncard actioncard = new OapiRobotSendRequest.Actioncard();
        actioncard.setTitle(message.getTitle());
        actioncard.setText(message.getContent());
        actioncard.setHideAvatar("0");
        actioncard.setBtnOrientation("1");

        if (CollectionUtils.isNotEmpty(message.getButtons())) {
            if (message.getButtons().size() == 1) {
                actioncard.setSingleTitle(message.getButtons().get(0).getTitle());
                actioncard.setSingleURL(message.getButtons().get(0).getContent());
            } else {
                List<OapiRobotSendRequest.Btns> btns = new ArrayList<>();
                message.getButtons().forEach(b -> {
                    OapiRobotSendRequest.Btns btn0 = new OapiRobotSendRequest.Btns();
                    btn0.setTitle(b.getTitle());
                    btn0.setActionURL(b.getContent());
                    btns.add(btn0);
                });
                actioncard.setBtns(btns);
            }
        }

        request.setMsgtype(message.getMessageType().getDesc());
        request.setActionCard(actioncard);
        return request;
    }

    /**
     * Build feed card resquest
     *
     * @param message message
     * @return the oapi robot send request
     * @since 2.1.0
     */
    private @NotNull OapiRobotSendRequest buildFeedCardResquest(@NotNull FeedCardMessage message) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        OapiRobotSendRequest.Feedcard feedcard = new OapiRobotSendRequest.Feedcard();
        List<OapiRobotSendRequest.Links> linksList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(message.getLinks())) {
            message.getLinks().forEach(f -> {
                OapiRobotSendRequest.Links links0 = new OapiRobotSendRequest.Links();
                links0.setTitle(f.getTitle());
                links0.setMessageURL(f.getLink());
                links0.setPicURL(f.getUrl());
                linksList.add(links0);
            });

        }
        feedcard.setLinks(linksList);
        request.setMsgtype(message.getMessageType().getDesc());
        request.setFeedCard(feedcard);
        return request;
    }
}
