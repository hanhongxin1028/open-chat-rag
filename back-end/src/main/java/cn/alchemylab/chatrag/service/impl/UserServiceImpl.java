package cn.alchemylab.chatrag.service.impl;

import cn.alchemylab.chatrag.dto.LoginFormDTO;
import cn.alchemylab.chatrag.dto.RegisterFormDTO;
import cn.alchemylab.chatrag.dto.Result;
import cn.alchemylab.chatrag.dto.UserDTO;
import cn.alchemylab.chatrag.entity.User;
import cn.alchemylab.chatrag.mapper.UserMapper;
import cn.alchemylab.chatrag.service.IUserService;
import cn.alchemylab.chatrag.utils.PasswordUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;





@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Override
    public Result login(LoginFormDTO loginForm) {
        // 1. 获取 手机号 和 密码
        String phone = loginForm.getPhone();
        String password = loginForm.getPassword();

        // 2. 各种校验
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(password)) {
            return Result.fail("手机号或密码不能为空");
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.fail("手机号格式不正确");
        }

        if (password.length() < 6 || password.length() > 20) {
            return Result.fail("密码长度必须在6-20位之间");
        }

        // 3. 查看数据库中是否存在该用户
        User user = query().eq("phone", phone).one();

            //不存在，让用户创建
        if(user == null){
            return Result.fail("该用户不存在, 请创建");
        }

        // 4. 判断密码是否正确
        if (! PasswordUtils.matches(password, user.getPassword())) {
            return Result.fail("密码错误, 请重试");
        }

        // 将用户隐私信息隐藏（采用字段少的UserDTO）
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        // 8. 返回token给前端
        return Result.ok(userDTO);
    }

    @Override
    public Result register(RegisterFormDTO registerForm) {
        String phone = registerForm.getPhone();
        String password = registerForm.getPassword();
        String confirmPassword = registerForm.getConfirmPassword();

        // 1. 各种校验
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)) {
            return Result.fail("手机号或密码不能为空");
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.fail("手机号格式不正确");
        }

        if (password.length() < 6 || password.length() > 20) {
            return Result.fail("密码长度必须在6-20位之间");
        }

        if(! password.equals(confirmPassword)){
            return Result.fail("两次输入密码不一致, 请重新输入");
        }

        // 2. 判断是否已经注册过
        User user = query().eq("phone", phone).one();
        if (user != null){
            return Result.fail("该用户已注册, 请直接登录");
        }


        // 3. 加密密码
        String encode_password = PasswordUtils.encode(password);


        // 4. 存入数据库
        User new_user = new User();
        new_user.setPhone(phone);
        new_user.setPassword(encode_password);
        new_user.setNickName("nufe_" + RandomUtil.randomString(8));
        save(new_user);

        return Result.ok("注册成功, 请登录");
    }

}
