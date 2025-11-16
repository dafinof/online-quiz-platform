package bg.softuni.onlinequizplatform.web.dto;

import bg.softuni.onlinequizplatform.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapperProfile {
    public static EditProfileRequest fromUserToEditProfileRequest(User user) {
        return EditProfileRequest.builder()
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .score(user.getScore())
                .level(user.getLevel())
                .email(user.getEmail())
                .build();
    }
}
