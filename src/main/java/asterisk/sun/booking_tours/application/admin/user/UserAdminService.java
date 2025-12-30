package asterisk.sun.booking_tours.application.admin.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.user.dto.FormCreateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.FormUpdateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.ListUserDTO;
import asterisk.sun.booking_tours.application.rest.admin.user.dto.GetUsersRequestDTO;

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

    public Page<User> queryListUserByKeywordApiAdmin(GetUsersRequestDTO request) {
        Pageable pageable = request.getPageable();
        Specification<User> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("username")), likeKey),
                        cb.like(cb.lower(root.get("email")), likeKey),
                        cb.like(cb.lower(root.get("firstName")), likeKey),
                        cb.like(cb.lower(root.get("lastName")), likeKey));
                predicates.add(keywordPredicate);
            }

            if (request.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), request.getRole()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return repository.findAll(spec, pageable);
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

        // Store the current password before mapping
        String currentPassword = user.getPassword();

        // Map the DTO to the user entity
        user = MapperHelper.map(formUpdateUserDTO, User.class);

        // Update password only if provided, otherwise keep the current password
        if (formUpdateUserDTO.getPassword() != null && !formUpdateUserDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(formUpdateUserDTO.getPassword()));
        } else {
            user.setPassword(currentPassword);
        }

        repository.save(user);
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
