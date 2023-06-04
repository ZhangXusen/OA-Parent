package com.atguigu.wechat.controller;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.jwt.JwtHelper;
import com.atguigu.model.system.SysUser;
import com.atguigu.result.Result;
import com.atguigu.vo.wechat.BindPhoneVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

@Api(tags = "微信")
@Controller
@RequestMapping("/admin/wechat")
public class WechatController {

    @Autowired
    private SysUserService userService;
    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    @ApiOperation("获取微信授权")
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) {
        //1.在那个路径获取微信信息 2.授权类型 3.授权成功后跳转的路径
        String RedirectUrl = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                URLEncoder.encode(returnUrl.replace("guiguoa", "#")));
        return "redirect:" + RedirectUrl;
    }

    @ApiOperation("获取用户的微信信息")
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        //微信用户信息
        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
        String token = "";
        if (user != null) {
            //说明已经绑定微信openId，反之为建立账号绑定，去页面建立账号绑定
            token = JwtHelper.createToken(user.getId(), user.getUsername());
        }
        if (returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }
    @ApiOperation(value = "微信账号绑定手机号")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        //根据手机号查询用户信息
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        if(user!=null){
            //用户存在,更新openId
            user.setOpenId(bindPhoneVo.getOpenId());
            userService.updateById(user);

            String token = JwtHelper.createToken(user.getId(), user.getUsername());
            return Result.ok(token);
        }else {
            return Result.fail("手机号码不存在，绑定失败");
        }
    }
}
