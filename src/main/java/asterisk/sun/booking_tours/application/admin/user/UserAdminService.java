package asterisk.sun.booking_tours.application.admin.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.user.dto.FormCreateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.FormUpdateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.ListUserDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.projection.UserBasicProjection;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserAdminService extends BaseServiceController<UserRepository> {
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        super(userRepository);
    }

    public List<ListUserDTO> queryListUserByKeyword(String keyword) {
        List<UserBasicProjection> users = repository.searchByKeyword(keyword);

        return MapperHelper.mapList(users, ListUserDTO.class);
    }

    public void createUser(FormCreateUserDTO formCreateUserDTO) {
        User user = MapperHelper.map(formCreateUserDTO, User.class);

        user.setPassword(passwordEncoder.encode(formCreateUserDTO.getPassword()));

        repository.save(user);
    }

    public FormUpdateUserDTO getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return MapperHelper.map(user, FormUpdateUserDTO.class);
    }

    public void updateUser(FormUpdateUserDTO formUpdateUserDTO) {
        User user = repository.findById(formUpdateUserDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + formUpdateUserDTO.getId()));

        if (user != null) {
            user = MapperHelper.map(formUpdateUserDTO, User.class);

            // Update password only if provided
            if (formUpdateUserDTO.getPassword() != null && !formUpdateUserDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(formUpdateUserDTO.getPassword()));
            }

            repository.save(user);
        }
    }

    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        repository.delete(user);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return repository.existsByPhone(phone);
    }

    public boolean existsByUsernameExcludingId(String username, Long id) {
        return repository.existsByUsernameAndIdNot(username, id);
    }

    public boolean existsByEmailExcludingId(String email, Long id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    public boolean existsByPhoneExcludingId(String phone, Long id) {
        return repository.existsByPhoneAndIdNot(phone, id);
    }
}
