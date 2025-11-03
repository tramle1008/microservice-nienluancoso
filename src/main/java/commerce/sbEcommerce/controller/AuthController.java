package commerce.sbEcommerce.controller;


import commerce.sbEcommerce.model.AppRole;
import commerce.sbEcommerce.model.Role;
import commerce.sbEcommerce.model.User;

import commerce.sbEcommerce.repository.RoleRepository;
import commerce.sbEcommerce.repository.UserRepository;
import commerce.sbEcommerce.security.jwt.LoginRequest;
import commerce.sbEcommerce.security.request.SignupRequest;
import commerce.sbEcommerce.security.request.UpdateUserRequest;
import commerce.sbEcommerce.security.response.MessageResponse;
import commerce.sbEcommerce.security.response.UserInfoResponse;
import commerce.sbEcommerce.security.services.UserDetailsImpl;
import commerce.sbEcommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import commerce.sbEcommerce.security.jwt.JwtUtils;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Mật khẩu sai");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Gửi JWT trong body chứ không phải cookie
        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwtToken
        ));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid SignupRequest signupRequest) {

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Tên đăng ký người dùng đã tồn tại"));
        }

        // Kiểm tra email đã tồn tại (sửa lỗi dùng sai getUserName())
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email đăng ký đã tồn tại"));
        }

        // Tạo user mới với mật khẩu đã mã hóa
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword())
        );

        Set<String> strRole = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // Nếu không gửi role => mặc định là ROLE_USER
        if (strRole == null || strRole.isEmpty()) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new IllegalArgumentException("Error: Role USER không tìm thấy"));
            roles.add(userRole);
        } else {
            for (String roleStr : strRole) {
                switch (roleStr.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new IllegalArgumentException("Error: Role ADMIN không tìm thấy"));
                        roles.add(adminRole);
                        break;
                    case "deliver":
                        Role deliverRole = roleRepository.findByRoleName(AppRole.ROLE_DELIVER)
                                .orElseThrow(() -> new IllegalArgumentException("Error: Role ADMIN không tìm thấy"));
                        roles.add(deliverRole);
                        break;
                    default:
                        Role defaultRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new IllegalArgumentException("Error: Role USER không tìm thấy"));
                        roles.add(defaultRole);
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Đăng ký thành công"));
    }


    @GetMapping("/username")
    public String currentUser(Authentication authentication){
        if(authentication != null) {
            return authentication.getName();
        }else
            return "null";
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetail(Authentication authentication, HttpServletRequest request){
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng ký");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        String jwtToken = jwtUtils.getJWTFromHeader(request);

        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                jwtToken
        );
        return  ResponseEntity.ok().body(response);
    }





}