package cn.alchemylab.chatrag.dto;

import lombok.Data;

@Data
public class RegisterFormDTO {
    private String phone;

    private String password;

    private String confirmPassword;
}
