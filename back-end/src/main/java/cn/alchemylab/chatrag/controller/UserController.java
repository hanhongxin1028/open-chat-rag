package cn.alchemylab.chatrag.controller;


import cn.alchemylab.chatrag.dto.LoginFormDTO;
import cn.alchemylab.chatrag.dto.RegisterFormDTO;
import cn.alchemylab.chatrag.dto.Result;
import cn.alchemylab.chatrag.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm){

        return userService.login(loginForm);
    }

    /**
     * 创建账号功能
     * @param registerForm 登录参数，包含手机号、密码、二次确认密码
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterFormDTO registerForm){
        return userService.register(registerForm);
    }

}
