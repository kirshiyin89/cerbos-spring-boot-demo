package org.cerbos.demo.controller;

import dev.cerbos.sdk.CerbosBlockingClient;
import dev.cerbos.sdk.builders.AttributeValue;
import dev.cerbos.sdk.builders.Principal;
import dev.cerbos.sdk.builders.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cerbos.demo.model.Employee;
import org.cerbos.demo.model.Profile;
import org.cerbos.demo.repository.EmployeeRepository;
import org.cerbos.demo.repository.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final EmployeeRepository employeeRepository;
    private final ProfileRepository profileRepository;
    private final CerbosBlockingClient cerbosBlockingClient;


    @GetMapping("/get/{profileId}/{employeeId}")
    public ResponseEntity<String> getProfile(@PathVariable String profileId, @PathVariable String employeeId) {
        Profile profile = getProfileOrNotFound(profileId);
        if (profile == null) {
            return handleProfileNotFound();
        }

        Employee employee = getEmployeeOrNotFound(employeeId);
        if (employee == null) {
            return handleEmployeeNotFound();
        }

        Principal principal = Principal.newInstance(employeeId, employee.getRole())
                .withAttribute("id", AttributeValue.stringValue(employeeId));
        Resource resource = Resource.newInstance("profile", profileId)
                .withAttribute("owner", AttributeValue.stringValue(String.valueOf(profile.getEmployee().getEmployeeId())));

        if (!isAllowed("read", principal, resource)) {
            log.debug("Not allowed to read profile");
            return handleForbidden();
        }

        return ResponseEntity.ok().body(profile.getEmployee().toString());
    }

    @DeleteMapping("/delete/{profileId}/{employeeId}")
    public ResponseEntity<String> deleteProfile(@PathVariable String profileId, @PathVariable String employeeId) {
        Profile profile = getProfileOrNotFound(profileId);
        if (profile == null) {
            return handleProfileNotFound();
        }

        Employee employee = getEmployeeOrNotFound(employeeId);
        if (employee == null) {
            return handleEmployeeNotFound();
        }

        Principal principal = Principal.newInstance(employeeId, employee.getRole());
        Resource resource = Resource.newInstance("profile", profileId);

        if (!isAllowed("delete", principal, resource)) {
            log.debug("Not allowed to delete profile");
            return handleForbidden();
        }

        try {
            profileRepository.deleteById(Long.valueOf(profileId));
            boolean isDeleted = profileRepository.findById(Long.valueOf(profileId)).isEmpty();
            return isDeleted ? ResponseEntity.ok().body("Profile deleted successfully") : ResponseEntity.internalServerError().body("Failed to delete profile");
        } catch (Exception e) {
            return handleInternalServerError(e);
        }
    }


    private boolean isAllowed(String action, Principal principal, Resource resource) {
        return cerbosBlockingClient.check(principal, resource, action).isAllowed(action);
    }

    private Profile getProfileOrNotFound(String profileId) {
        return profileRepository.findById(Long.parseLong(profileId)).orElse(null);
    }

    private Employee getEmployeeOrNotFound(String employeeId) {
        return employeeRepository.findById(Long.parseLong(employeeId)).orElse(null);
    }

    private ResponseEntity<String> handleProfileNotFound() {
        return ResponseEntity.badRequest().body("Profile not found");
    }

    private ResponseEntity<String> handleEmployeeNotFound() {
        return ResponseEntity.badRequest().body("Employee not found");
    }

    private ResponseEntity<String> handleForbidden() {
        return ResponseEntity.status(403).body("Forbidden");
    }

    private ResponseEntity<String> handleInternalServerError(Exception e) {
        log.error("Error processing request: ", e);
        return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
    }

}
