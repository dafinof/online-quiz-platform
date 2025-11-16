package bg.softuni.onlinequizplatform.web.dto;

import bg.softuni.onlinequizplatform.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapperUser {
    public static EditUserRequest fromUserToEditUserRequest(User user) {
        return EditUserRequest.builder()
                .username(user.getUsername())
                .newPassword(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .active(user.isActive())
                .score(user.getScore())
                .level(user.getLevel())
                .email(user.getEmail())
                .build();
    }
}
