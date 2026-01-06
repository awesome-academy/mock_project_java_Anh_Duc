// package asterisk.sun.booking_tours.application.api.user;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
// import asterisk.sun.booking_tours.application.api.user.dto.UserProfileResponseDTO;
// import asterisk.sun.booking_tours.application.api.user.dto.UserRegistrationRequestDTO;
// import asterisk.sun.booking_tours.application.api.user.dto.UserRegistrationResponseDTO;
// import asterisk.sun.booking_tours.application.api.user.dto.UserResendVerificationRequestDTO;
// import jakarta.mail.MessagingException;
// import jakarta.validation.Valid;
// import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;

// @RestController
// @RequestMapping(ApiV1.USER_ENDPOINT)
// public class ApiUserController {
//     private final ApiUserService apiUserService;

//     public ApiUserController(ApiUserService apiUserService) {
//         this.apiUserService = apiUserService;
//     }

//     @PostMapping("/register")
//     public ResponseEntity<SuccessResponse<UserRegistrationResponseDTO>> register(
//             @Valid @RequestBody UserRegistrationRequestDTO requestDTO) {

//         UserRegistrationResponseDTO responseDTO = apiUserService.registerUser(requestDTO);

//         SuccessResponse<UserRegistrationResponseDTO> response = new SuccessResponse<>(
//                 HttpStatus.CREATED.value(),
//                 "User registered successfully. Please check your email to verify your account.",
//                 responseDTO);

//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
//     }

//     @PostMapping("/resend-verification")
//     public ResponseEntity<SuccessResponse<String>> resendVerificationEmail(
//             @Valid @RequestBody UserResendVerificationRequestDTO requestDTO) throws MessagingException {
//         apiUserService.createResendVerificationRequest(requestDTO);

//         SuccessResponse<String> response = new SuccessResponse<>(
//                 HttpStatus.OK.value(),
//                 "Verification email has been resent. Please check your inbox.");

//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/verify-email")
//     public ResponseEntity<SuccessResponse<String>> verifyEmail(@RequestParam String token) {
//         apiUserService.verifyEmailToken(token);

//         SuccessResponse<String> response = new SuccessResponse<>(
//                 HttpStatus.OK.value(),
//                 "Email verified successfully! You can now login to your account.");

//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/profile")
//     public ResponseEntity<SuccessResponse<UserProfileResponseDTO>> getProfile(
//             @AuthenticationPrincipal UserDetails userDetails) {
//         UserProfileResponseDTO userProfile = apiUserService.getUserProfile(userDetails.getUsername());

//         SuccessResponse<UserProfileResponseDTO> response = new SuccessResponse<>(
//                 HttpStatus.OK.value(),
//                 "User profile retrieved successfully.",
//                 userProfile);

//         return ResponseEntity.ok(response);
//     }

// }
