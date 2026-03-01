package com.example.psycho.bootstrap;

import com.example.psycho.model.*;
import com.example.psycho.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        ClinicEntity centralClinic = ensureClinic(
                "Bering Central Clinic",
                "Astana",
                "Arsenal Avenue 101",
                "+77-717-555-0181",
                "central@clinic.local",
                "Bering flagship clinic for diagnostics, therapy and integrated mental health support"
        );

        ClinicEntity riversideClinic = ensureClinic(
                "Bering Arsenal Clinic",
                "Astana",
                "Arsenal District 22",
                "+77-717-555-0175",
                "riverside@clinic.local",
                "Bering specialty center for cardiology and advanced diagnostics"
        );

        DepartmentEntity therapy = ensureDepartment(centralClinic, "Therapy", "General outpatient therapy and follow-up");
        DepartmentEntity diagnostics = ensureDepartment(centralClinic, "Diagnostics", "Functional and laboratory diagnostics");
        DepartmentEntity mentalHealth = ensureDepartment(centralClinic, "Mental Health", "Psychology and psychiatry consultations");
        DepartmentEntity rehab = ensureDepartment(centralClinic, "Rehabilitation", "Post-treatment recovery and wellness programs");

        DepartmentEntity cardiology = ensureDepartment(riversideClinic, "Cardiology", "Cardiovascular diagnostics and treatment");
        DepartmentEntity imaging = ensureDepartment(riversideClinic, "Imaging", "MRI, CT and ultrasound diagnostics");

        ensureMedicalService(therapy, "THERAPY_CONSULT", "Therapist consultation", 30, new BigDecimal("65.00"));
        ensureMedicalService(therapy, "ENDO_CHECK", "Endocrinology check-up", 40, new BigDecimal("95.00"));
        ensureMedicalService(diagnostics, "BLOOD_PANEL", "Extended blood panel", 20, new BigDecimal("45.00"));
        ensureMedicalService(diagnostics, "LIVER_SCREEN", "Liver function screening", 25, new BigDecimal("55.00"));
        ensureMedicalService(mentalHealth, "PSYCH_INTAKE", "Psychological intake session", 50, new BigDecimal("90.00"));
        ensureMedicalService(mentalHealth, "CBT_FOLLOWUP", "Cognitive therapy follow-up", 45, new BigDecimal("80.00"));
        ensureMedicalService(rehab, "POST_OP_REHAB", "Post-operative rehabilitation", 60, new BigDecimal("110.00"));
        ensureMedicalService(cardiology, "CARDIO_SCREEN", "Cardiology screening", 45, new BigDecimal("120.00"));
        ensureMedicalService(cardiology, "ECG_STRESS", "Stress ECG", 35, new BigDecimal("105.00"));
        ensureMedicalService(imaging, "MRI_BASIC", "MRI diagnostics", 50, new BigDecimal("230.00"));
        ensureMedicalService(imaging, "ULTRASOUND_ADV", "Advanced ultrasound", 30, new BigDecimal("85.00"));

        UserEntity admin = ensureUser(new UserSeed(
                "admin@clinic.local",
                "Clinic Admin",
                "Admin123!",
                UserRole.ADMIN,
                centralClinic,
                "+77-717-555-1001",
                "Healthcare platform operations",
                null,
                14,
                "Leads clinic digital operations, compliance and service quality governance."
        ));

        ensureUser(new UserSeed(
                "reception@clinic.local",
                "Front Desk",
                "Reception123!",
                UserRole.RECEPTIONIST,
                centralClinic,
                "+77-717-555-1002",
                "Patient coordination",
                null,
                6,
                "Coordinates patient check-in, referrals, confirmations and administrative support."
        ));

        ensureUser(new UserSeed(
                "doc.alex@clinic.local",
                "Dr. Alex Morgan",
                "Doctor123!",
                UserRole.DOCTOR,
                centralClinic,
                "+77-717-555-1101",
                "Internal medicine",
                "NY-IM-44718",
                12,
                "Specialist in internal medicine and preventive chronic disease management."
        ));

        ensureUser(new UserSeed(
                "doc.sara@clinic.local",
                "Dr. Sara Bennett",
                "Doctor123!",
                UserRole.DOCTOR,
                centralClinic,
                "+77-717-555-1102",
                "Cardiology",
                "NY-CARD-33892",
                9,
                "Cardiologist focused on risk screening, hypertension and integrated therapy planning."
        ));

        ensureUser(new UserSeed(
                "doc.mike@clinic.local",
                "Dr. Mike Rivera",
                "Doctor123!",
                UserRole.DOCTOR,
                riversideClinic,
                "+77-717-555-1103",
                "Diagnostics",
                "NY-DIAG-55217",
                7,
                "Leads diagnostics pathways for laboratory interpretation and pre-treatment assessment."
        ));

        ensureUser(new UserSeed(
                "doc.emma@clinic.local",
                "Dr. Emma Clarke",
                "Doctor123!",
                UserRole.DOCTOR,
                riversideClinic,
                "+77-717-555-1104",
                "Radiology",
                "NY-RAD-88124",
                8,
                "Radiologist with focus on MRI, CT and structured reporting standards."
        ));

        ensureUser(new UserSeed(
                "psy.julia@clinic.local",
                "Julia Holmes",
                "Doctor123!",
                UserRole.PSYCHOLOGIST,
                centralClinic,
                "+77-717-555-1201",
                "Clinical psychology",
                "NY-PSY-22460",
                11,
                "Clinical psychologist supporting anxiety, stress recovery and long-term therapeutic programs."
        ));

        ensureUser(new UserSeed(
                "patient.demo@clinic.local",
                "Demo Patient",
                "Patient123!",
                UserRole.PATIENT,
                centralClinic,
                "+77-717-555-2001",
                null,
                null,
                0,
                "Demo patient profile for booking, follow-up and personal appointment history tests."
        ));

        ensureUser(new UserSeed(
                "client.demo@clinic.local",
                "Demo Client",
                "Client123!",
                UserRole.CLIENT,
                centralClinic,
                "+77-717-555-2002",
                null,
                null,
                0,
                "Demo client account for public-to-private booking flow and self-service portal checks."
        ));

        ensureUser(new UserSeed(
                "patient.olivia@clinic.local",
                "Olivia Carter",
                "Patient123!",
                UserRole.PATIENT,
                riversideClinic,
                "+77-717-555-2003",
                null,
                null,
                0,
                "Seed profile used for appointment timeline and analytics examples."
        ));

        seedKnowledgeLibrary(admin);
    }

    private void seedKnowledgeLibrary(UserEntity author) {
        List<ArticleSeed> seeds = List.of(
                new ArticleSeed(
                        "appointment-checklist-2026",
                        "Appointment Checklist for First-Time Visitors",
                        "What to prepare before coming to the clinic for consultation.",
                        "Bring your previous records, medication list, and key symptoms timeline. Try to arrive 10-15 minutes early to complete intake and verify contact information.",
                        KnowledgeCategory.PREVENTION,
                        "appointment,checklist,intake"
                ),
                new ArticleSeed(
                        "hypertension-warning-signs",
                        "Hypertension Warning Signs and Monitoring",
                        "How to track blood pressure and when to seek a doctor.",
                        "Persistent blood pressure readings above 140/90 should be reviewed by a doctor. Home monitoring twice daily and lifestyle tracking improves treatment precision.",
                        KnowledgeCategory.DISEASES,
                        "hypertension,cardiology,monitoring"
                ),
                new ArticleSeed(
                        "understanding-blood-tests",
                        "Understanding Common Blood Test Panels",
                        "A simple guide to interpreting routine lab checks.",
                        "Blood panel values should be interpreted in context of symptoms and medical history. Avoid self-diagnosis and review abnormal values with your clinician.",
                        KnowledgeCategory.DIAGNOSTICS,
                        "lab,blood-test,diagnostics"
                ),
                new ArticleSeed(
                        "mri-preparation-guide",
                        "MRI Preparation Guide",
                        "Preparation steps before MRI diagnostics.",
                        "Inform staff about implants, allergies and claustrophobia. Remove metal accessories and follow food instructions if contrast imaging is planned.",
                        KnowledgeCategory.DIAGNOSTICS,
                        "mri,imaging,diagnostics"
                ),
                new ArticleSeed(
                        "cardio-risk-screening",
                        "Cardiovascular Risk Screening",
                        "Who should schedule a preventive cardiology check.",
                        "Patients with family history, smoking, diabetes or obesity benefit from early cardiology screening. Risk-based prevention can reduce long-term complications.",
                        KnowledgeCategory.PREVENTION,
                        "cardiology,screening,prevention"
                ),
                new ArticleSeed(
                        "mental-health-first-visit",
                        "What to Expect at a Mental Health Intake",
                        "How first psychological or psychiatric visits are structured.",
                        "First intake usually includes symptom history, stress factors and sleep patterns. Treatment may combine counseling, behavioral methods and regular follow-up.",
                        KnowledgeCategory.MENTAL_HEALTH,
                        "mental-health,psychology,intake"
                ),
                new ArticleSeed(
                        "sleep-hygiene-basics",
                        "Sleep Hygiene Basics for Better Recovery",
                        "Daily routines that improve sleep quality and resilience.",
                        "Consistent sleep schedule, reduced evening screen time and lower caffeine intake after midday can improve rest quality and recovery outcomes.",
                        KnowledgeCategory.MENTAL_HEALTH,
                        "sleep,recovery,wellness"
                ),
                new ArticleSeed(
                        "post-visit-follow-up-plan",
                        "Post-Visit Follow-Up Plan",
                        "Steps to take after consultation or procedure.",
                        "Follow prescribed treatment, track symptoms daily and attend control visits as recommended. Use reminders for medication and lab follow-up windows.",
                        KnowledgeCategory.REHABILITATION,
                        "follow-up,treatment,monitoring"
                ),
                new ArticleSeed(
                        "post-surgery-rehab-phases",
                        "Post-Surgery Rehabilitation Phases",
                        "How structured rehab supports safe return to activity.",
                        "Rehabilitation usually progresses from pain control and mobility recovery to strength rebuilding and long-term prevention of relapse.",
                        KnowledgeCategory.REHABILITATION,
                        "rehabilitation,post-op,physiotherapy"
                ),
                new ArticleSeed(
                        "nutrition-for-heart-health",
                        "Nutrition Plan for Heart Health",
                        "Core dietary rules for cardiology patients.",
                        "A diet focused on vegetables, whole grains, fish, legumes and reduced sodium helps blood pressure and lipid control. Individual adjustments should be discussed with clinicians.",
                        KnowledgeCategory.NUTRITION,
                        "nutrition,heart,cardiology"
                ),
                new ArticleSeed(
                        "faq-can-i-change-appointment",
                        "Can I Reschedule My Appointment?",
                        "FAQ on changing scheduled visits.",
                        "Yes. Contact reception or use your client account to request another slot. Early rescheduling improves availability and continuity of care.",
                        KnowledgeCategory.FAQ,
                        "faq,appointments,reschedule"
                ),
                new ArticleSeed(
                        "faq-what-to-bring-for-lab",
                        "What Should I Bring for Lab Diagnostics?",
                        "FAQ for laboratory and imaging visits.",
                        "Bring identification, referral information and previous results if available. Follow fasting instructions when required for accurate interpretation.",
                        KnowledgeCategory.FAQ,
                        "faq,lab,diagnostics"
                ),
                new ArticleSeed(
                        "news-riverside-imaging-launch",
                        "New Imaging Unit Opened at Bering Arsenal Clinic",
                        "Bering launched an expanded MRI and ultrasound unit in the Arsenal district.",
                        "Our Arsenal location now provides faster diagnostics with extended evening shifts and consultant-led interpretation.",
                        KnowledgeCategory.NEWS,
                        "news,imaging,clinic-update"
                ),
                new ArticleSeed(
                        "news-online-booking-update",
                        "Online Booking Update for Client Portal",
                        "Client application now supports faster appointment booking with specialist filters.",
                        "Patients can now view doctor specialization details, available clinics and upcoming visits in one interface.",
                        KnowledgeCategory.NEWS,
                        "news,booking,client-portal"
                ),
                new ArticleSeed(
                        "news-cardiology-program-2026",
                        "Cardiology Prevention Program 2026",
                        "Bering started a preventive program for high-risk cardiovascular patients.",
                        "The new pathway combines lab tracking, imaging, nutrition counseling and coordinated follow-up with cardiology specialists.",
                        KnowledgeCategory.NEWS,
                        "news,cardiology,prevention"
                ),
                new ArticleSeed(
                        "news-mental-health-hotline",
                        "24/7 Mental Health Support Line Expanded",
                        "Patient support center now includes dedicated mental health triage assistance.",
                        "Licensed specialists can help route urgent mental health concerns to appropriate care teams and appointments.",
                        KnowledgeCategory.NEWS,
                        "news,mental-health,support"
                )
        );

        for (ArticleSeed seed : seeds) {
            ensureArticle(author, seed.slug(), seed.title(), seed.summary(), seed.content(), seed.category(), seed.tags());
        }
    }

    private ClinicEntity ensureClinic(String name,
                                      String city,
                                      String address,
                                      String phone,
                                      String email,
                                      String description) {
        ClinicEntity clinic = clinicRepository.findByEmail(email)
                .or(() -> clinicRepository.findByName(name))
                .orElseGet(ClinicEntity::new);

        clinic.setName(name);
        clinic.setCity(city);
        clinic.setAddress(address);
        clinic.setPhone(phone);
        clinic.setEmail(email);
        clinic.setDescription(description);
        clinic.setActive(true);

        return clinicRepository.save(clinic);
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

    private UserEntity ensureUser(UserSeed seed) {
        Optional<UserEntity> existing = userRepository.findByEmail(seed.email());
        UserEntity user = existing.orElseGet(UserEntity::new);

        if (existing.isEmpty()) {
            user.setEmail(seed.email());
            user.setPassword(passwordEncoder.encode(seed.password()));
        } else if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(seed.password()));
        }

        user.setName(seed.name());
        user.setRole(seed.role());
        user.setClinic(seed.clinic());
        user.setPhone(seed.phone());
        user.setSpecialization(seed.specialization());
        user.setLicenseNumber(seed.licenseNumber());
        user.setYearsOfExperience(seed.yearsOfExperience());
        user.setAbout(seed.about());
        user.setActive(true);

        return userRepository.save(user);
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
        article.setPublishedAt(LocalDateTime.now());
        article.setAuthor(author);
        knowledgeArticleRepository.save(article);
    }

    private record ArticleSeed(String slug,
                               String title,
                               String summary,
                               String content,
                               KnowledgeCategory category,
                               String tags) {
    }

    private record UserSeed(String email,
                            String name,
                            String password,
                            UserRole role,
                            ClinicEntity clinic,
                            String phone,
                            String specialization,
                            String licenseNumber,
                            Integer yearsOfExperience,
                            String about) {
    }
}
