package com.volunteersync.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Custom validation annotation for conditional field requirements
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterRequest.ConditionalFieldsValidator.class)
@interface ValidConditionalFields {
    String message() default "Invalid field combination for user type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@ValidConditionalFields
public class RegisterRequest {
    
    // For volunteers - conditional validation based on userType
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    // For organizations - conditional validation based on userType
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    private String organizationName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    @NotBlank(message = "User type is required")
    private String userType; // "VOLUNTEER" or "ORGANIZATION"
    
    // Default constructor
    public RegisterRequest() {}
    
    // Constructor for volunteers
    public RegisterRequest(String firstName, String lastName, String email, 
                          String password, String confirmPassword, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userType = userType;
    }
    
    // Constructor for organizations
    public RegisterRequest(String organizationName, String email, 
                          String password, String confirmPassword, String userType) {
        this.organizationName = organizationName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userType = userType;
    }
    
    // Constructor with all fields
    public RegisterRequest(String firstName, String lastName, String organizationName,
                          String email, String password, String confirmPassword, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizationName = organizationName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.userType = userType;
    }
    
    // Getters
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public String getUserType() {
        return userType;
    }
    
    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    // Helper methods
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
    
    public boolean isVolunteer() {
        return "VOLUNTEER".equalsIgnoreCase(userType);
    }
    
    public boolean isOrganization() {
        return "ORGANIZATION".equalsIgnoreCase(userType);
    }
    
    // Get display name based on user type
    public String getDisplayName() {
        if (isOrganization()) {
            return organizationName;
        } else if (isVolunteer()) {
            return firstName + " " + lastName;
        }
        return email; // fallback
    }
    
    @Override
    public String toString() {
        if (isOrganization()) {
            return "RegisterRequest{" +
                    "organizationName='" + organizationName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType='" + userType + '\'' +
                    '}';
        } else {
            return "RegisterRequest{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", userType='" + userType + '\'' +
                    '}';
        }
    }
    
    // Custom validator for conditional field requirements
    public static class ConditionalFieldsValidator implements ConstraintValidator<ValidConditionalFields, RegisterRequest> {
        
        @Override
        public void initialize(ValidConditionalFields constraintAnnotation) {
            // Initialization if needed
        }
        
        @Override
        public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
            if (request == null || request.getUserType() == null) {
                return false;
            }
            
            // Disable default constraint violation
            context.disableDefaultConstraintViolation();
            
            boolean isValid = true;
            
            if (request.isVolunteer()) {
                // For volunteers, firstName and lastName are required
                if (isBlankOrNull(request.getFirstName())) {
                    context.buildConstraintViolationWithTemplate("First name is required for volunteers")
                           .addPropertyNode("firstName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
                if (isBlankOrNull(request.getLastName())) {
                    context.buildConstraintViolationWithTemplate("Last name is required for volunteers")
                           .addPropertyNode("lastName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
                // Organization name should be null or empty for volunteers
                if (!isBlankOrNull(request.getOrganizationName())) {
                    context.buildConstraintViolationWithTemplate("Organization name should not be provided for volunteers")
                           .addPropertyNode("organizationName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
            } else if (request.isOrganization()) {
                // For organizations, organizationName is required
                if (isBlankOrNull(request.getOrganizationName())) {
                    context.buildConstraintViolationWithTemplate("Organization name is required for organizations")
                           .addPropertyNode("organizationName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
                // First and last names should be null or empty for organizations
                if (!isBlankOrNull(request.getFirstName())) {
                    context.buildConstraintViolationWithTemplate("First name should not be provided for organizations")
                           .addPropertyNode("firstName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
                if (!isBlankOrNull(request.getLastName())) {
                    context.buildConstraintViolationWithTemplate("Last name should not be provided for organizations")
                           .addPropertyNode("lastName")
                           .addConstraintViolation();
                    isValid = false;
                }
                
            } else {
                // Invalid user type
                context.buildConstraintViolationWithTemplate("User type must be either VOLUNTEER or ORGANIZATION")
                       .addPropertyNode("userType")
                       .addConstraintViolation();
                isValid = false;
            }
            
            // Validate password matching
            if (!request.isPasswordMatching()) {
                context.buildConstraintViolationWithTemplate("Password and confirm password must match")
                       .addPropertyNode("confirmPassword")
                       .addConstraintViolation();
                isValid = false;
            }
            
            return isValid;
        }
        
        private boolean isBlankOrNull(String str) {
            return str == null || str.trim().isEmpty();
        }
    }
}