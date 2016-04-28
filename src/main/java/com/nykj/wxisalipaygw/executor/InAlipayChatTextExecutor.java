/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.nykj.wxisalipaygw.executor;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayMobilePublicMessageCustomSendRequest;
import com.alipay.api.response.AlipayMobilePublicMessageCustomSendResponse;
import com.nykj.wxisalipaygw.common.MyException;
import com.nykj.wxisalipaygw.util.AlipayMsgBuildUtil;
import com.nykj.wxisalipaygw.util.AlipayUtil;
import net.sf.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聊天执行器(纯文本消息)
 * 
 * @author baoxing.gbx
 * @version $Id: InAlipayChatExecutor.java, v 0.1 Jul 28, 2014 5:17:04 PM baoxing.gbx Exp $
 */
public class InAlipayChatTextExecutor implements ActionExecutor {

    /** 线程池 */
    private static ExecutorService executors = Executors.newSingleThreadExecutor();

    /** 业务参数 */
    private JSONObject             bizContent;

    /** 推送消息内容 */
    private  String content;

    public InAlipayChatTextExecutor(JSONObject bizContent) {
        this.bizContent = bizContent;
    }

    public InAlipayChatTextExecutor(JSONObject bizContent,String content) {
        this.bizContent = bizContent;
        this.content = content;
    }

    public InAlipayChatTextExecutor() {
        super();
    }

    /**
     * 
     * @see ActionExecutor#execute()
     */
    @Override
    public String execute() throws MyException {

        //取得发起请求的支付宝账号id
        final String fromUserId = bizContent.getString("FromUserId");

        //1. 首先同步构建ACK响应
        String syncResponseMsg = AlipayMsgBuildUtil.buildBaseAckMsg(fromUserId);

        //2. 异步发送消息
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    // 2.1 构建一个业务响应消息，商户根据自行业务构建，这里只是一个简单的样例
                    String requestMsg = AlipayMsgBuildUtil.buildSingleTextMsg(fromUserId);

                    AlipayClient alipayClient = AlipayUtil.getAlipayClient();
                    AlipayMobilePublicMessageCustomSendRequest request = new AlipayMobilePublicMessageCustomSendRequest();
                    request.setBizContent(requestMsg);

                    // 2.2 使用SDK接口类发送响应
                    AlipayMobilePublicMessageCustomSendResponse response = alipayClient
                        .execute(request);

                    // 2.3 商户根据响应结果处理结果
                    //这里只是简单的打印，请商户根据实际情况自行进行处理
                    if (null != response && response.isSuccess()) {
                        System.out.println("异步发送成功，结果为：" + response.getBody());
                    } else {
                        System.out.println("异步发送失败 code=" + response.getCode() + "msg："
                                           + response.getMsg());
                    }
                } catch (Exception e) {
                    System.out.println("异步发送失败");
                }
            }
        });

        // 3.返回同步的ACK响应
        return syncResponseMsg;
    }

}