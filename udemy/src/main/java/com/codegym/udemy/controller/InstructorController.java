package com.codegym.udemy.controller;

import com.codegym.udemy.dto.InstructorDto;
import com.codegym.udemy.entity.AppUser;
import com.codegym.udemy.entity.Instructor;
import com.codegym.udemy.repository.AppUserRepository;
import com.codegym.udemy.service.JwtService;
import com.codegym.udemy.service.impl.JwtServiceImpl;
import com.codegym.udemy.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.codegym.udemy.constant.VarConstant.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/instructor")
public class InstructorController {
    private final InstructorService instructorService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    @Autowired
    public InstructorController(InstructorService instructorService, JwtService jwtService,
                                AppUserRepository appUserRepository) {
        this.instructorService = instructorService;
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/get")
    public ResponseEntity<InstructorDto> getInstructorByUserId(@RequestHeader(AUTHORIZATION) String token) {
    String username = jwtService.extractUsername(token);
    AppUser user = appUserRepository.findFirstByUsername(username);
    InstructorDto instructorDto = instructorService.getInstructorByUserId(user.getId());
        if (instructorDto != null) {
            return ResponseEntity.ok(instructorDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/create",consumes = ("multipart/form-data")
    )
    public ResponseEntity<String> saveInstructor(
            @RequestHeader(AUTHORIZATION) String token,
            @ModelAttribute InstructorDto instructorDto,
            @RequestParam("file") MultipartFile file
            ) {
        Long userId = jwtService.extractUserId(token);
        instructorService.saveInstructor(userId, instructorDto, file);
        return ResponseEntity.ok("Instructor saved successfully.");
    }

    @PutMapping(value = "/edit", consumes = ("multipart/form-data"))
    public ResponseEntity<String> editInstructor(@RequestHeader(AUTHORIZATION) String token,
                                                 @ModelAttribute InstructorDto instructorDto,
                                                 @RequestParam("file") MultipartFile file) {
        Long userId = jwtService.extractUserId(token);
        InstructorDto existingInstructorDto = instructorService.getInstructorByUserId(userId);
        instructorService.editInstructor(existingInstructorDto.getId(), instructorDto, file);
        return ResponseEntity.ok("Instructor edited successfully.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteInstructorByUserId(@RequestHeader(AUTHORIZATION) String token) {
        Long userId = jwtService.extractUserId(token);
        instructorService.deleteInstructorByUserId(userId);
        return ResponseEntity.ok("Instructor deleted successfully.");
    }
}
