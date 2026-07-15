package com.zzyl.nursing.service.impl;

import java.util.HashMap;
import java.util.List;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.WechatService;
import com.zzyl.nursing.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.FamilyMemberMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.Map;

/**
 * 老人家属Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-19
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper,FamilyMember> implements IFamilyMemberService
{
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private TokenService tokenService;

    static List<String> DEFAULT_NICK_NAME = ListUtil.of(
            "生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝");

    /**
     * 小程序登录
     *
     * @param dto
     * @return
     */
    @Override
    public LoginVo login(UserLoginRequestDto dto) {

        //1.获取openid(调用微信的接口获取) ***
        String openid = wechatService.getOpenid(dto.getCode());

        //2.根据openid查询用户
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getOpenId,openid);
        FamilyMember familyMember = getOne(queryWrapper);

        //3.判断用户是否存在，用户不存在，就构建用户
        if(ObjectUtil.isEmpty(familyMember)){
            /*familyMember = new FamilyMember();
            familyMember.setOpenId(openid);*/
            familyMember = FamilyMember.builder()
                    .openId(openid)
                    .build();
        }

        //4.获取用户的手机号（调用微信的接口获取）***
        String phone = wechatService.getPhone(dto.getPhoneCode());

        //5. 新增或修改用户
        inserOrUpdateFamilyMember(familyMember,phone);

        //6. 把用户的信息封装到token,返回
        Map<String,Object> claims = new HashMap<>();
        claims.put("username",familyMember.getName());
        claims.put("userId",familyMember.getId());
        String token = tokenService.createToken(claims);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setNickName(familyMember.getName());

        return loginVo;
    }

    /**
     * 新增或修改用户
     * @param familyMember
     * @param phone
     */
    private void inserOrUpdateFamilyMember(FamilyMember familyMember, String phone) {

        //1. 判断新的手机跟数据库中保存的手机号是否一致
        if(ObjectUtil.notEqual(familyMember.getPhone(),phone)){
            familyMember.setPhone(phone);
        }

        //2. id是否存在，如存在，就修改
        if(ObjectUtil.isNotEmpty(familyMember.getId())){
            updateById(familyMember);
            return;
        }

        //3. 不存在，就新增 (拼装用户的昵称：随机字符串+手机号后4位)
        int index = (int)(Math.random() * DEFAULT_NICK_NAME.size());
        String nickName = DEFAULT_NICK_NAME.get(index)+ phone.substring(phone.length()-4);
        familyMember.setName(nickName);
        save(familyMember);

    }

    /**
     * 查询老人家属
     * 
     * @param id 老人家属主键
     * @return 老人家属
     */
    @Override
    public FamilyMember selectFamilyMemberById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人家属列表
     * 
     * @param familyMember 老人家属
     * @return 老人家属
     */
    @Override
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember)
    {
        return familyMemberMapper.selectFamilyMemberList(familyMember);
    }

    /**
     * 新增老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int insertFamilyMember(FamilyMember familyMember)
    {
        return save(familyMember)?1:0;
    }

    /**
     * 修改老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int updateFamilyMember(FamilyMember familyMember)
    {
        return updateById(familyMember)?1:0;
    }

    /**
     * 批量删除老人家属
     * 
     * @param ids 需要删除的老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除老人家属信息
     * 
     * @param id 老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberById(Long id)
    {
        return removeById(id)?1:0;
    }

}
