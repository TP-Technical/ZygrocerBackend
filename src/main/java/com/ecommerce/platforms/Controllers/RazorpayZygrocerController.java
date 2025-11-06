package com.ecommerce.platforms.Controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import com.ecommerce.platforms.Models.PaymentLink;
import com.ecommerce.platforms.Models.User;
import com.ecommerce.platforms.Services.PaymentLinkService;
import com.ecommerce.platforms.Services.UserService;
import com.ecommerce.platforms.Utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/gateway")
public class RazorpayZygrocerController {
    private static final Logger logger = LoggerFactory.getLogger(RazorpayZygrocerController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PaymentLinkService paymentLinkService;

    private final String RAZORPAY_KEY_ID = "rzp_live_RGAbyH4acF7VmU";
    private final String RAZORPAY_KEY_SECRET = "Xul90Ise0afBPSXcv5RKBU6Z";

    @PostMapping("/zygrocer/user/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {

        logger.info("Signup request received for email: {}", user.getEmail());
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        if (userService.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number already exists"));
        }

        String razorpayError = null;

        try {
            Map<String, Object> razorpayResponse = createRazorpayCustomer(user);
            user.setRazorpay_customer_id((String) razorpayResponse.get("id"));
            user.setRazorpay_entity((String) razorpayResponse.get("entity"));
            logger.info("Razorpay customer created successfully: {}", user.getRazorpay_customer_id());
        } catch (Exception e) {
            razorpayError = e.getMessage();
            System.out.println("Razorpay customer creation failed: " + razorpayError);
        }
        User savedUser = userService.registerUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("userName", savedUser.getUserName());
        response.put("phoneNumber", savedUser.getPhoneNumber());
        response.put("email", savedUser.getEmail());

        if (razorpayError != null) {
            response.put("razorpayError", razorpayError);
        }

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createRazorpayCustomer(User user) throws Exception {
        String url = "https://api.razorpay.com/v1/customers";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", user.getUserName());
        requestBody.put("contact", user.getPhoneNumber());
        requestBody.put("email", user.getEmail());
        requestBody.put("fail_existing", "1"); // fail if exists

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Basic Auth
        String auth = RAZORPAY_KEY_ID + ":" + RAZORPAY_KEY_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody();
        } else {
            Map<String, Object> errorBody = response.getBody();
            throw new Exception(errorBody != null ? errorBody.toString() : "Razorpay customer creation failed");
        }

    }

    @GetMapping("/zygrocer/user/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            // 1️⃣ Remove "Bearer " prefix if present
            String token = authHeader.replace("Bearer ", "");

            // 2️⃣ Extract email (subject) from JWT
            String email = jwtUtil.extractUsername(token);

            // 3️⃣ Find user by email
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }
            User user = userOpt.get();

            // 4️⃣ Prepare response map
            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("userName", user.getUserName());
            response.put("phoneNumber", user.getPhoneNumber());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PostMapping("/zygrocer/user/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent() && userService.checkPassword(password, userOpt.get().getPassword())) {
            String token = jwtUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping("/razorpay/payment-link-callback")
    public void paymentLinkCallback(@RequestParam Map<String, String> params, HttpServletResponse response) {
        try {
            String status = params.get("razorpay_payment_link_status");
            if ("paid".equalsIgnoreCase(status)) {
                // Successful payment redirect
                response.sendRedirect("https://www.trustlypay.com/api/success");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback redirect (runs if not paid or in case of any failure/exception)
        try {
            response.sendRedirect("https://www.trustlypay.com/api/error");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @PostMapping("/zygrocer/payment/create-link")
    public ResponseEntity<?> createPaymentLink(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody Map<String, Object> request) {

        try {
            // 1️⃣ Extract JWT token & get User
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid user"));
            }
            User user = userOpt.get();

            // 2️⃣ Extract only required fields from frontend
            Integer amount = (Integer) request.get("amount");
            String description = (String) request.getOrDefault("description", "Payment request");

            // 3️⃣ Build Razorpay body in backend
            Map<String, Object> body = new HashMap<>();
            body.put("upi_link", true);
            body.put("amount", amount * 100);
            body.put("currency", "INR");
            body.put("accept_partial", false);

            // reference_id → generate from DB/order
            String referenceId = new java.util.Random().ints(15, 0, 36).mapToObj(i -> Integer.toString(i, 36)).collect(java.util.stream.Collectors.joining()).toUpperCase();

            body.put("reference_id", referenceId);

            Map<String, Object> notes = new HashMap<>();
            notes.put("internal_ref", referenceId);
            body.put("notes", notes);

            body.put("description", description);

            // customer info from User entity
            Map<String, Object> customer = new HashMap<>();
            customer.put("name", user.getUserName());
            customer.put("contact", user.getPhoneNumber());
            customer.put("email", user.getEmail());
            body.put("customer", customer);

            // notify always false
            Map<String, Object> notify = new HashMap<>();
            notify.put("sms", false);
            notify.put("email", false);
            body.put("notify", notify);

            body.put("reminder_enable", true);

            long now = System.currentTimeMillis() / 1000;

            // Set expire time 20 minutes from now
            long expireBy = now + (20 * 60);

            body.put("expire_by", expireBy);

            body.put("callback_url", "https://www.trustlypay.com/api/gateway/razorpay/payment-link-callback");

            body.put("callback_method", "get");

            // Debug print
            System.out.println("➡️ Now: " + now);
            System.out.println("➡️ Expire By: " + expireBy);
            System.out.println("➡️ Sending JSON: " + new ObjectMapper().writeValueAsString(body));

            // 4️⃣ Hit Razorpay API
            String url = "https://api.razorpay.com/v1/payment_links";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = RAZORPAY_KEY_ID + ":" + RAZORPAY_KEY_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                Map<String, Object> responseBody = response.getBody();

                // save in DB
                PaymentLink link = new PaymentLink();
                link.setRazorpayPaymentLinkId((String) responseBody.get("id"));
                link.setShortUrl((String) responseBody.get("short_url"));
                link.setReferenceId(referenceId);
                link.setStatus((String) responseBody.get("status"));
                link.setAmount((Integer) responseBody.get("amount"));
                link.setCurrency((String) responseBody.get("currency"));
                link.setUser(user);
                link.setCustomerName(user.getUserName());
                link.setCustomerEmail(user.getEmail());
                link.setCustomerPhone(user.getPhoneNumber());

                paymentLinkService.save(link);

                // return trimmed response
                return ResponseEntity.ok(
                        Map.of("paymentLinkId", link.getRazorpayPaymentLinkId(), "amount", link.getAmount(), "currency",
                                link.getCurrency(), "status", link.getStatus(), "short_url", link.getShortUrl()));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }



}
