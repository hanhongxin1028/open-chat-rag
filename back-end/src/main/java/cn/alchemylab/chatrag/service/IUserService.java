package cn.alchemylab.chatrag.service;

import cn.alchemylab.chatrag.dto.LoginFormDTO;
import cn.alchemylab.chatrag.dto.RegisterFormDTO;
import cn.alchemylab.chatrag.dto.Result;
import cn.alchemylab.chatrag.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {


    Result login(LoginFormDTO loginForm);

    Result register(RegisterFormDTO registerForm);
}
