package com.example.psycho.bootstrap;

import com.example.psycho.model.*;
import com.example.psycho.model.KnowledgeCategory;
import com.example.psycho.model.UserRole;
import com.example.psycho.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ClinicRepository clinicRepository;
    private final DepartmentRepository departmentRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final UserRepository userRepository;
    private final KnowledgeArticleRepository knowledgeArticleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ClinicRepository clinicRepository,
                           DepartmentRepository departmentRepository,
                           MedicalServiceRepository medicalServiceRepository,
                           UserRepository userRepository,
                           KnowledgeArticleRepository knowledgeArticleRepository,
                           PasswordEncoder passwordEncoder) {
        this.clinicRepository = clinicRepository;
        this.departmentRepository = departmentRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.userRepository = userRepository;
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ClinicEntity clinic = ensureClinic();
        DepartmentEntity therapy = ensureDepartment(clinic, "Therapy", "General outpatient therapy and follow-up");
        DepartmentEntity diagnostics = ensureDepartment(clinic, "Diagnostics", "Functional and laboratory diagnostics");

        ensureMedicalService(therapy, "THERAPY_CONSULT", "Therapist consultation", 30, new BigDecimal("50.00"));
        ensureMedicalService(therapy, "CARDIO_SCREEN", "Cardiology screening", 45, new BigDecimal("90.00"));
        ensureMedicalService(diagnostics, "BLOOD_PANEL", "Extended blood panel", 20, new BigDecimal("40.00"));

        UserEntity admin = ensureUser("admin@clinic.local", "Clinic Admin", "Admin123!", UserRole.ADMIN, clinic, null, 0);
        ensureUser("reception@clinic.local", "Front Desk", "Reception123!", UserRole.RECEPTIONIST, clinic, null, 2);
        ensureUser("doc.alex@clinic.local", "Dr. Alex Morgan", "Doctor123!", UserRole.DOCTOR, clinic, "Internal medicine", 12);
        ensureUser("doc.sara@clinic.local", "Dr. Sara Bennett", "Doctor123!", UserRole.DOCTOR, clinic, "Cardiology", 9);
        ensureUser("doc.mike@clinic.local", "Dr. Mike Rivera", "Doctor123!", UserRole.DOCTOR, clinic, "Diagnostics", 7);
        ensureUser("patient.demo@clinic.local", "Demo Patient", "Patient123!", UserRole.PATIENT, clinic, null, 0);

        ensureArticle(admin,
                "how-to-prepare-for-appointment",
                "How to Prepare for a Clinic Appointment",
                "A short checklist before visiting a doctor.",
                "Bring your previous test results, medication list, and symptom timeline. Arrive 10-15 minutes early.",
                KnowledgeCategory.PREVENTION,
                "appointment,checklist,clinic");

        ensureArticle(admin,
                "blood-pressure-basics",
                "Blood Pressure Basics",
                "Understand normal ranges and when to seek help.",
                "A consistent blood pressure above 140/90 should be discussed with a physician for diagnosis and care plan.",
                KnowledgeCategory.DIAGNOSTICS,
                "pressure,cardiology,health");

        ensureArticle(admin,
                "post-visit-follow-up",
                "Post-Visit Follow-up Plan",
                "What to do after your appointment.",
                "Follow prescribed treatment, track symptoms daily, and schedule control visits according to doctor recommendations.",
                KnowledgeCategory.REHABILITATION,
                "follow-up,treatment,monitoring");
    }

    private ClinicEntity ensureClinic() {
        return clinicRepository.findAll().stream().findFirst().orElseGet(() -> {
            ClinicEntity clinic = new ClinicEntity();
            clinic.setName("Psycho Health Clinic");
            clinic.setCity("New York");
            clinic.setAddress("101 Main Street");
            clinic.setPhone("+1-212-555-0181");
            clinic.setEmail("info@clinic.local");
            clinic.setDescription("Multidisciplinary outpatient clinic for adult and family care");
            clinic.setActive(true);
            return clinicRepository.save(clinic);
        });
    }

    private DepartmentEntity ensureDepartment(ClinicEntity clinic, String name, String description) {
        Optional<DepartmentEntity> existing = departmentRepository.findByClinic_IdAndName(clinic.getId(), name);
        if (existing.isPresent()) {
            return existing.get();
        }

        DepartmentEntity department = new DepartmentEntity();
        department.setClinic(clinic);
        department.setName(name);
        department.setDescription(description);
        return departmentRepository.save(department);
    }

    private MedicalServiceEntity ensureMedicalService(DepartmentEntity department,
                                                      String code,
                                                      String name,
                                                      int duration,
                                                      BigDecimal price) {
        return medicalServiceRepository.findByCode(code).orElseGet(() -> {
            MedicalServiceEntity medicalService = new MedicalServiceEntity();
            medicalService.setDepartment(department);
            medicalService.setCode(code);
            medicalService.setName(name);
            medicalService.setDurationMinutes(duration);
            medicalService.setBasePrice(price);
            medicalService.setDescription(name + " service");
            return medicalServiceRepository.save(medicalService);
        });
    }

    private UserEntity ensureUser(String email,
                                  String name,
                                  String password,
                                  UserRole role,
                                  ClinicEntity clinic,
                                  String specialization,
                                  int yearsOfExperience) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setClinic(clinic);
            user.setSpecialization(specialization);
            user.setYearsOfExperience(yearsOfExperience);
            user.setActive(true);
            user.setAbout(specialization != null ? "Specialist in " + specialization : "Clinic staff");
            return userRepository.save(user);
        });
    }

    private void ensureArticle(UserEntity author,
                               String slug,
                               String title,
                               String summary,
                               String content,
                               KnowledgeCategory category,
                               String tags) {
        if (knowledgeArticleRepository.findBySlug(slug).isPresent()) {
            return;
        }

        KnowledgeArticleEntity article = new KnowledgeArticleEntity();
        article.setSlug(slug);
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(content);
        article.setCategory(category);
        article.setTags(tags);
        article.setPublished(true);
        article.setPublishedAt(java.time.LocalDateTime.now());
        article.setAuthor(author);
        knowledgeArticleRepository.save(article);
    }
}

