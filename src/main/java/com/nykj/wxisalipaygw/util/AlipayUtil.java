package com.nykj.wxisalipaygw.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserUserinfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserUserinfoShareResponse;
import com.nykj.wxisalipaygw.constants.AlipayServiceEnvConstants;

import java.util.HashMap;
import java.util.Map;

public class AlipayUtil {

    /** API调用客户端 */
    public static Map<String, AlipayClient> alipayClientMap = new HashMap<String, AlipayClient>();

    public static String getAlipayOpenId(String unitId,String authCode,String scope) {
        if(scope == null || "auth_base".equals(scope)){
            try{
                AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
                oauthTokenRequest.setCode(authCode);
                oauthTokenRequest.setGrantType(AlipayServiceEnvConstants.GRANT_TYPE);
                AlipayClient alipayClient = AlipayUtil.getAlipayClient(unitId);
                AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest);

                //成功获得authToken
                if (null != oauthTokenResponse && oauthTokenResponse.isSuccess()) {
                    return oauthTokenResponse.getUserId();
                } else {
                    return null;
                }
            }catch (Exception e){
                System.out.println("获取用户openId异常");
            }
        }
        AlipayUserUserinfoShareResponse alipayUserUserinfoShareResponse = getAlipayUserInfo(unitId,authCode);
        return alipayUserUserinfoShareResponse == null ? null : alipayUserUserinfoShareResponse.getAlipayUserId();
    }

    /**
     * 获得API调用客户端
     *
     * @return
     */
    public static AlipayClient getAlipayClient(String unitId){

        /*if(unitId == null || "".equals(unitId)){
            return null;
        }

        AlipayClient alipayClient = alipayClientMap.get(unitId);
        if(alipayClient != null){
            return alipayClient;
        }

        WeiXinHospLink weiXinHospLink = AccessDataUtil.findWeiXinHospLinkByUnitId(unitId);
        if(weiXinHospLink == null){
            return null;
        }*/

        AlipayClient alipayClient = alipayClientMap.get(unitId);

        //TODO 生产环境需放开此处
        /*alipayClient = new DefaultAlipayClient(AlipayServiceEnvConstants.ALIPAY_GATEWAY, weiXinHospLink.getApp_id(),
                weiXinHospLink.getApp_secret(), "json", AlipayServiceEnvConstants.CHARSET,weiXinHospLink.getSrv_token());*/

        alipayClient = new DefaultAlipayClient(AlipayServiceEnvConstants.ALIPAY_GATEWAY, AlipayServiceEnvConstants.APP_ID,
                AlipayServiceEnvConstants.PRIVATE_KEY, "json", AlipayServiceEnvConstants.CHARSET,AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY);
        alipayClientMap.put(unitId,alipayClient);
        return alipayClient;

    }

    /**
     * 获取默认的client
     * @return
     */
    public static AlipayClient getAlipayClient(){
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayServiceEnvConstants.ALIPAY_GATEWAY, AlipayServiceEnvConstants.APP_ID,
                AlipayServiceEnvConstants.PRIVATE_KEY, "json", AlipayServiceEnvConstants.CHARSET,AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY);
        return alipayClient;
    }

    /**
     * 获得服务窗用户信息
     *
     * @return
     */
    public static AlipayUserUserinfoShareResponse getAlipayUserInfo(String unitId,String authCode) {
        AlipayUserUserinfoShareRequest userinfoShareRequest = new AlipayUserUserinfoShareRequest();
        AlipayUserUserinfoShareResponse userinfoShareResponse = null;
        try {
            AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
            oauthTokenRequest.setCode(authCode);
            oauthTokenRequest.setGrantType(AlipayServiceEnvConstants.GRANT_TYPE);
            AlipayClient alipayClient = AlipayUtil.getAlipayClient(unitId);
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest);
            
            //成功获得authToken
            if (null != oauthTokenResponse && oauthTokenResponse.isSuccess()) {
                userinfoShareResponse = alipayClient.execute(userinfoShareRequest, oauthTokenResponse.getAccessToken());
            }
        } catch (AlipayApiException e) {
            System.out.println("获取用户信息异常");
        }
        
        return userinfoShareResponse;
    }
}