package org.cerbos.demo.controller;

import dev.cerbos.sdk.CerbosBlockingClient;
import dev.cerbos.sdk.CheckResult;
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

        Profile profile = profileRepository.findById(Long.parseLong(profileId)).orElse(null);

        if (profile == null) {
            return ResponseEntity.badRequest().body("Profile not found by ID: " + profileId);
        }
        Employee employee = employeeRepository.findById(Long.parseLong(employeeId)).orElse(null);

        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee not found by ID: " + employeeId);
        }

        Principal principal = Principal.newInstance(employeeId, employee.getRole())
                .withAttribute("id", AttributeValue.stringValue(employeeId));

        Resource resource = Resource.newInstance("profile", profileId)
                .withAttribute("owner", AttributeValue.stringValue(String.valueOf(profile.getEmployee().getEmployeeId())));

        CheckResult result = cerbosBlockingClient.check(
                principal,
                resource,
                "read");

        if (!result.isAllowed("read")) {
            log.debug("Not allowed to read!");
            return ResponseEntity.status(403).body("Forbidden");
        } else {
            log.debug("Allowed to read!");
            return ResponseEntity.ok().body(profile.getEmployee().toString());

        }

    }

    @DeleteMapping("/delete/{profileId}/{employeeId}")
    public ResponseEntity<String> deleteProfile(@PathVariable String profileId, @PathVariable String employeeId) {
        try {
            Profile profile = profileRepository.findById(Long.parseLong(profileId)).orElse(null);

            if (profile == null) {
                return ResponseEntity.badRequest().body("Profile not found by ID: " + profileId);
            }
            Employee employee = employeeRepository.findById(Long.parseLong(employeeId)).orElse(null);

            if (employee == null) {
                return ResponseEntity.badRequest().body("Employee not found by ID: " + employeeId);
            }

            Principal principal = Principal.newInstance(employeeId, employee.getRole());

            Resource resource = Resource.newInstance("profile", profileId);

            CheckResult result = cerbosBlockingClient.check(
                    principal,
                    resource,
                    "delete");

            if (!result.isAllowed("delete")) {
                log.debug("Not allowed to delete!");
                return ResponseEntity.status(403).body("Forbidden");
            } else {
                log.debug("Allowed to delete!");
            }

            profileRepository.deleteById(Long.valueOf(profileId));

            boolean isDeleted = profileRepository.findById(Long.valueOf(profileId)).isEmpty();

            if (isDeleted) {
                return ResponseEntity.ok().body("Profile deleted successfully");
            } else {
                return ResponseEntity.internalServerError().body("Failed to delete profile");
            }
        } catch (Exception e) {
            log.error("Error processing request: ", e);
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }

    }

}
