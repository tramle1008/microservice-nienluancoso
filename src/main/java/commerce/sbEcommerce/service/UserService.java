package commerce.sbEcommerce.service;

import commerce.sbEcommerce.model.User;
import commerce.sbEcommerce.payload.UserDTO;
import commerce.sbEcommerce.security.request.UpdateUserRequest;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUser();

    List<UserDTO> getAllAdmin();

    String updateCurrentUser(UpdateUserRequest request);
}
