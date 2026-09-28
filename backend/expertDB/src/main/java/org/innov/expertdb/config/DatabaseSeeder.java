package org.innov.expertdb.config;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.services.UserService;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final String RIID = "Research Innovation and Industrialisation Directorate";

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        seed(new RegisterRequest(
                "Super Admin",
                "Smith",
                "admin@expertdb.com",
                "AdminPassword123!",
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        ), Role.ADMIN);

        String gushaResearch = "Appropriate Animal Husbandry Technology, Animal Improvement and Genetics, "
                + "Rangeland and Pasture Nutrition, Forage and Fodder Production and Conservation";
        seed(new RegisterRequest(
                "Jacob",
                "Gusha",
                "jgusha@vet.uz.ac.zw",
                TEST_PASSWORD,
                "Dr",
                "Dr Jacob Gusha",
                "+263772252514",
                "PhD Agric, MSc Animal Science, BSc Agric Honours in Animal Science",
                gushaResearch,
                null,
                "Director",
                "jgusha@vet.uz.ac.zw",
                "+263772252514",
                "PhD Agric",
                null,
                null,
                null,
                "Agribusiness and Continuing Education",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                gushaResearch,
                null,
                "Publications: 28 journal articles, 10 book manuals",
                photo("jgusha@vet.uz.ac.zw"),
                null,
                null,
                null,
                null
        ), Role.VIEWER);

        seedRiidStaff();
    }

    // Staff profiles from seed_data.doc (RIID website content template).
    private void seedRiidStaff() {
        seedStaff("Eng", "Jeremiah Mavushe", "Matina", "jmmatina@innovhub.uz.ac.zw", "0718777131",
                RIID, "Facilities Manager",
                "Jeremiah is a Professional Electrical Engineer registered with the Engineering Council of Zimbabwe "
                        + "and a member of the Zimbabwe Institution of Engineers. He holds a Graduate Diploma in Electrical "
                        + "and Electronic Engineering (Engineering Council UK) and an Executive MBA from MSU. With several "
                        + "years' experience as a Senior Electrical Engineer in gold mines across Zimbabwe, he is passionate "
                        + "about integrating processing plants, downstream manufacturing, and local value chains to reduce "
                        + "raw material exports.",
                "Executive Masters in Business Administration (MSU)",
                "Smart Infrastructure Development", null);

        seedStaff("Dr", "Prosper", "Mapfumo", "pmapfumo@innovhub.uz.ac.zw", null,
                RIID, "Innovation Lead, Scientific Applications and Technological Advancement",
                "PhD-trained chemist with extensive research and applied experience spanning academia and industry. "
                        + "Former Doctoral Researcher at Friedrich Schiller University Jena and Research Assistant at the "
                        + "Fritz Lipmann Institute, Germany, with a strong track record in advanced material synthesis, "
                        + "nanoparticle formulation, multidisciplinary collaboration, and translation of scientific research "
                        + "into practical and scalable applications. Industry experience includes quality control, GMP "
                        + "implementation, and laboratory operations at ZIMASCO (Pvt) Ltd.",
                "PhD Chemistry",
                "Synthetic and Medicinal Chemistry, Organic Chemistry, Polymer Chemistry, Analytical Chemistry",
                "I am a highly motivated and specialized researcher in synthetic and medicinal chemistry, equipped with "
                        + "extensive skills in designing, synthesizing, formulating, isolating, and analyzing biologically "
                        + "functional molecules.");

        seedStaff("Mr", "Mwatamanta L", "Banda", "mbanda@innovhub.uz.ac.zw", "+263773214833",
                RIID, "Innovation Lead",
                "An Innovation Lead in food systems and agricultural technologies, specializing in developing and "
                        + "implementing solutions that enhance productivity, sustainability, and value chain efficiency. "
                        + "Work focuses on bridging research, technology, and practical application to address real-world "
                        + "challenges in agriculture. With experience in project development, stakeholder coordination, and "
                        + "emerging agri-tech systems, contributes to advancing innovative approaches that support economic "
                        + "growth and food security. Strong interest in scalable technologies, digital systems, and "
                        + "data-driven solutions for modern agriculture. Qualifications: BSc Hons Agricultural Engineering, MBA.",
                "MBA",
                "Agricultural Technologies", null);

        seedStaff("Ms", "R", "Mubaiwa", "rmubaiwa@innovhub.uz.ac.zw", "0773460566",
                RIID, "Innovation Lead, Food Systems and Agricultural Technologies",
                "Accomplished senior professional with progressive leadership experience across food manufacturing "
                        + "operations, quality assurance systems, and product development. Previously served as Production "
                        + "Supervisor and Product Development Assistant at Star Africa Corporation, in addition to holding "
                        + "the position of Quality Controller at TN Beverages. Qualifications: Honours Degree in Food Science "
                        + "and Technology; Master's Degree in Food Quality Monitoring and Safety; ISO 22000 Development and "
                        + "Implementation; HACCP Development and Implementation.",
                "Master's Degree in Food Quality Monitoring and Safety",
                "Food Systems", null);

        seedStaff("Mr", "Sodcanaan", "Momberume", "smomberume@vnagro.uz.ac.zw", "0786043202",
                RIID, "Graduate Trainee",
                "BSc Honours Degree in Animal Science.",
                "BSc Honours in Animal Science",
                "Animal Nutrition", null);

        seedStaff("Ms", "Lilian Mbirimi", "Paraffin", "lparaffin@innovhub.uz.ac.zw", null,
                RIID, "Innovation Lead, Science Discovery and Applications",
                "Experienced Chemist and Innovation Leader with over 18 years of professional experience across "
                        + "industrial manufacturing, mining and blasting operations, beverage quality control, and academic "
                        + "innovation systems. She drives research commercialization, supports multidisciplinary innovation "
                        + "projects, and strengthens linkages between academia and industry. Her expertise spans analytical "
                        + "chemistry, chemical safety, quality management, and regulatory compliance, with strong experience "
                        + "in HACCP, ISO 9001, ISO 14001:2004, and ISO 17025 quality and safety systems.",
                null,
                "Research Commercialization and Innovation Ecosystem Development, "
                        + "Industrial Chemistry and Sustainable Manufacturing Systems, "
                        + "Analytical Chemistry Applications in Quality Control and Product Development, "
                        + "Chemical Safety Regulatory Compliance and Environmental Management Systems, "
                        + "Value Addition and Mineral Beneficiation in Mining and Processing Industries, "
                        + "Agro-processing and Rural Industrialisation Technologies, "
                        + "Sustainable Product Formulation, "
                        + "Green Chemistry and Environmentally Responsible Production Systems",
                null);

        seedStaff("Mr", "Tafadzwa", "Gochayi", "tgochayi@innovhub.uz.ac.zw", "0771903287",
                RIID, "Business Development Specialist in Creative Arts and Social Entrepreneurship",
                "Master of Commerce Degree in Strategic Management, Bachelor of Commerce Hons Degree in "
                        + "Entrepreneurship, Post Graduate Diploma in Higher Education, Diploma in Development Studies, "
                        + "Certificate in Small Business Planning and Promotion. Work experience: Skills Trainer 2004-2015, "
                        + "Centre Head 2016-2022 at Jairos Jiri VTC, and Business Development Specialist at the University "
                        + "of Zimbabwe 2023 to date.",
                "Master of Commerce in Strategic Management",
                "Small Business Planning and Promotion, Corporate Governance, New Venture Capital Funding Models, "
                        + "Market Demand and Supply Issues, Branding Growth, Corporate Entrepreneurship, "
                        + "New Venture Creation, Family Business Management",
                "Book reviews: member of onlinebookclub.org, has reviewed 3 novels.");

        seedStaff("Mrs", "Chipo", "Nyandoro", "cnyandoro@admin.uz.ac.zw", "0716639258",
                RIID, "Executive Personal Assistant",
                "Diploma in Secretarial, Pitmans Secretarial.",
                "Diploma in Secretarial",
                null, null);

        seedStaff("Mr", "S", "Chapfunga", "schapfunga@innovhub.uz.ac.zw", "0774420633",
                RIID, "Millwright",
                "Artisan.",
                null,
                "Engineering", null);

        seedStaff("Mrs", "Olivia", "Chatsama", "ochatsama@innovhub.uz.ac.zw", "0773443023",
                RIID, "Grants Manager",
                "Olivia is a grants and project management professional with over 15 years of experience managing "
                        + "multi-million-dollar, multi-funder health, research, and development initiatives in Zimbabwe. "
                        + "She specializes in donor compliance, financial oversight, research administration, and capacity "
                        + "building, consistently strengthening institutional systems and ensuring effective use of donor "
                        + "resources. She holds an Honours degree in Business Studies, a Master's in Business Administration, "
                        + "and advanced qualifications in change leadership and project management.",
                "Master's in Business Administration",
                null, null);

        seedStaff("Mrs", "Diana", "Foroma", "dforoma@innovhub.uz.ac.zw", "+263772100917",
                RIID, "Administrator",
                "Has a strong background in coordinating research activities, supporting innovation initiatives, and "
                        + "managing institutional programs. Plays a key role in facilitating, organising and coordinating "
                        + "research-related projects, ensuring smooth implementation and alignment with institutional "
                        + "research objectives. Collaborates closely with innovators providing administrative support, "
                        + "coordination, and facilitation of activities that promote innovation development and visibility. "
                        + "Qualifications: MSc Social Anthropology and Sociology (UZ), BSc Sociology and Gender Studies (WUA), "
                        + "Diploma in Public Relations (LCCI, UK).",
                "MSc Social Anthropology and Sociology (UZ)",
                "Social Inequalities, Economic Shifts, Environmental Factors", null);

        seedStaff("Mr", "George H", "Penyaitu", "gpenyaitu@ceic.uz.ac.zw", "0773750959",
                RIID, "Innovation Lead (ICT)",
                "Master's Degree in Telecommunications Engineering, Honours Degree in Electronics and Telecommunications.",
                "Master's Degree in Telecommunications Engineering",
                "Wireless Communications, Internet of Things, Electronics", null);

        seedStaff("Ms", "Sandra", "Munemo", "smunemo@innovhub.uz.ac.zw", "0775780295",
                RIID, "Innovation Lead, Transformative Education and Social Innovation",
                "Sandra Munemo is a researcher and analyst dedicated to the political economy of inequality in Zimbabwe. "
                        + "As Innovation Lead for Transformative Education and Social Innovation at the University of "
                        + "Zimbabwe and a recipient of the prestigious WARiA British Academy bursary, her work investigates "
                        + "how structural dynamics shape access to health, education, and inclusion. Sandra's research "
                        + "explores how transformative education and social innovation can advance health and wellness, "
                        + "diversity, and social justice. This inquiry is grounded in extensive field experience, including "
                        + "research roles with UN Women (Spotlight Initiative) and Plan International (SAGE-GEC Project), "
                        + "contributions to the SADC RISDP Mid-Term Review, and program management roles with the Zimbabwe "
                        + "Council of Churches and Caritas Zimbabwe. In her current role, she bridges research and practice "
                        + "to design educational innovations that are contextually rooted and capable of driving lasting, "
                        + "equitable change in Zimbabwe.",
                null,
                "Transformative Education, Social Innovation, Political Economy of Inequality", null);

        seedStaff("Mr", "Clemence", "Chakuya", "cchakuya@innovhub.uz.ac.zw", "+263773996409",
                RIID, "Laboratory Technician",
                "Clemence Chakuya is an accomplished Environmental Systems Engineering specialist with a strong "
                        + "foundation in Chemical Technology, with 20 years' extensive experience in water quality analysis, "
                        + "pollution control, and ecological impact assessments, complemented by hands-on laboratory "
                        + "management and teaching expertise. Clemence has contributed to national and international research "
                        + "projects, including collaborations with the World Bank on water quality monitoring in Zimbabwe. "
                        + "His career highlights include developing innovative water treatment solutions. His expertise spans "
                        + "analytical chemistry, chemical safety, quality management, and regulatory compliance, with strong "
                        + "experience in HACCP, ISO 9001, ISO 14001:2004, and ISO 17025 quality and safety systems.",
                null,
                "Environment", "Alternative email: cchakuya@gmail.com");

        seedStaff("Mrs", "C", "Musarurwa", "cmusarurwa@innovhub.uz.ac.zw", "0788093953",
                RIID, "Business Development Lead",
                "Diana Musarurwa is an educator with a strong background in business, tourism, and international "
                        + "education. She is passionate about economic development and research, with experience in data "
                        + "analysis using tools such as STATA and Excel. Qualifications: Master of Economics Degree, BSc "
                        + "Degree in Tourism and Hospitality Management, Post Graduate Certificate in Education International, "
                        + "Cambridge Level 5 Certificate in Teaching English to Speakers of Other Languages (CELTA).",
                "Master of Economics",
                "Econometric Analysis of Export-Led Growth in Zimbabwe and Sub-Saharan African Countries", null);

        seedStaff("Mr", "B", "Mangwe", "bmangwe@innovhub.uz.ac.zw", "+263777508431",
                RIID, "Innovation Lead, Engineering and Manufacturing",
                "Professional Engineering and Manufacturing specialist with extensive experience in Automation, "
                        + "Electrical Engineering and Artificial Intelligence. I leverage cutting edge technologies to drive "
                        + "transformative solutions in various sectors. With a strong foundation in system integration, I "
                        + "have a proven ability to enhance operational efficiency and reliability through innovative "
                        + "strategies and advanced technologies. Qualifications: BSc Honours in Applied Physics and "
                        + "Instrumentation (Midlands State University), MSc in Mechatronics and Artificial Intelligence "
                        + "(University of Zimbabwe).",
                "MSc in Mechatronics and Artificial Intelligence (UZ)",
                "Robotics, Electrical Engineering, Automation, Artificial Intelligence", null);

        seedStaff("Mr", "Tonderai", "Madhayi", "tmadhayi@innovhub.uz.ac.zw", "0773365126",
                RIID, "Technician",
                "CNC Milling Machine Training.",
                "CNC Milling Machine Training",
                "Engineering", null);

        seedStaff("Mr", "Anold", "Nyandare", "anyandare@innovhub.uz.ac.zw", "+263773303211",
                RIID, "Driver",
                "Post Graduate Diploma in Monitoring and Evaluation; Dual Hons Degree in Geography and Disaster Management.",
                "Post Graduate Diploma in Monitoring and Evaluation",
                null, null);

        seedStaff("Mr", "Peter", "Mafukidze", "pmafukidze@innovhub.uz.ac.zw", "+263782479541",
                RIID, "Industrial Maintenance Millwright",
                "Class Millwright, Diploma in Purchasing and Supply.",
                "Diploma in Purchasing and Supply",
                "Maintenance and Machine Building", null);

        seedStaff("Mr", "Tumirai", "Musakatiza", "tmusakatiza@innovhub.uz.ac.zw", "0784318618",
                RIID, "Senior Technician",
                "National Certificate in Metal Fabrication, CNC Milling Machine Training.",
                "National Certificate in Metal Fabrication",
                "Metal Fabrication and Engineering", null);

        seedStaff("Mrs", "Jane", "Gono", "jgono@innovhub.uz.ac.zw", "0777342933",
                RIID, "Cleaner",
                "Ordinary Level, Advanced Level.",
                "Advanced Level",
                null, null);

        seedStaff("Mr", "Hebert", "Kanodeweta", "hnodeweta@innovhub.uz.ac.zw", "0777854904",
                RIID, "Cleaner",
                "Ordinary Level.",
                "Ordinary Level",
                null, null);

        seedStaff("Mr", "Leonard", "Mondo", "lmondo@innovhub.uz.ac.zw", "+263776934482",
                RIID, "Innovation Lead, Engineering and Manufacturing",
                "Leonard Mondo is an experienced Chemical Engineer and international chemical production technologist "
                        + "with over 25 years of expertise in chemical engineering, industrial verification, and technical "
                        + "leadership. He holds a Master of Engineering (MEng) in Chemical Engineering from the University "
                        + "of Veszprem, Hungary (2000), specializing in Crude Oil Processing and Organic Chemical Technology. "
                        + "Leonard has served as a Chemical Production Technologist and Chemical Weapons Inspector with the "
                        + "Organisation for the Prohibition of Chemical Weapons (OPCW) in the Netherlands (2011-2018 and "
                        + "2021-2024), where he inspected nearly 1,000 chemical production plants in about 40 countries. His "
                        + "work involved verifying that chemical industries, including oil refineries, petrochemical plants, "
                        + "pharmaceutical, pesticide, and detergent manufacturing facilities, were operating for legitimate "
                        + "industrial purposes in compliance with the Chemical Weapons Convention. Prior to his international "
                        + "career, he was General Manager Technical Services at Verify Engineering (2005-2010), leading a "
                        + "team of 30 engineers and technicians in the design of a Coal-to-Liquid fuels chemical plant. He "
                        + "also served as a Lecturer at the National University of Science and Technology (NUST) from 2004 "
                        + "to 2005. Leonard Mondo is recognized for his broad expertise in chemical production technologies, "
                        + "industrial process verification, engineering management, and global chemical safety.",
                "MEng in Chemical Engineering",
                "Oil Refineries, Fine Chemicals, Petrochemicals Production Plants",
                "Alternative email: learnardmondol@gmail.com");

        seedStaff("Mr", "Dennis Farai", "Mahuni", "dmahuni@innovhub.uz.ac.zw", "0786566885",
                RIID, "Business Development Lead",
                "My qualifications include a Master of Business Administration (MBA), Bachelor of Business Studies Hons "
                        + "in Marketing (HBBSMKT), Postgraduate Diploma in Education (PGDE), and Executive Diploma in Project "
                        + "Management Monitoring and Evaluation (EDPMME). I am a full member of the Marketers Association of "
                        + "Zimbabwe (MAZ), and I am currently pursuing a PhD in Business Administration and a Bachelor of "
                        + "Substantive Laws.",
                "Master of Business Administration (MBA)",
                "Social Entrepreneurship, Innovation, Commercial Law, Strategic Management", null);

        seedStaff("Dr", "K", "Mushipe", "kennmushipe@gmail.com", "+263783384245 / +263714119982",
                "Livestock", "Resident Veterinarian",
                "Bachelor of Veterinary Science (BVSc).",
                "Bachelor of Veterinary Science (BVSc)",
                "Automated and Digital Farming Systems", null);

        seedStaff("Miss", "Isheanesu", "Mukombe", "imukombe@innovhub.uz.ac.zw", "+263783902",
                RIID, "Graduate Trainee",
                "BSc Honours in Biochemistry.",
                "BSc Honours in Biochemistry",
                null, null);

        seedStaff("Ms", "T", "Chihuri", "tchihuri@vnagro.uz.ac.zw", "0775072903",
                "PMU", "Procurement Officer",
                "BCom in Purchasing and Supply, CIPS Level 5.",
                "BCom in Purchasing and Supply",
                null, null);

        seedStaff("Miss", "Oppah", "Maeresera", "omaeresera@innovhub.uz.ac.zw", "+263717578250 / +263784259437",
                RIID, "Graduate Trainee",
                "Oppah is a Research Scientist specializing in the intersection of microbiology and biotechnology. Her "
                        + "core focus is on the production of Biofertilizers. She leverages her background in Biological "
                        + "Sciences and Ecology (with specialization in Microbiology and Genetics) to develop sustainable "
                        + "agricultural solutions. Her work centers on optimizing microbial processes and bioreactor "
                        + "efficiency to create high-impact alternatives to chemical fertilizers.",
                "BSc Honours in Biological Sciences and Ecology",
                "Biotechnology and Genomics", null);

        seedStaff("Ms", "Thelma N", "Kupara", "tkupara@innovhub.uz.ac.zw", "0773046682",
                RIID, "Cleaner",
                "Ordinary Level.",
                "Ordinary Level",
                null, null);

        seedStaff("Ms", "Paidaishe", "Zowa", "pzowa@innovhub.uz.ac.zw", "0716151542",
                RIID, "Graduate Trainee",
                "BSc Biomedical Engineering, UZ.",
                "BSc Biomedical Engineering (UZ)",
                "Intelligent Medical Devices and Telemedicine", null);

        seedStaff("Ms", "Rosemary", "Mundhluli", "rmundhluli@innovhub.uz.ac.zw", "0772720423",
                RIID, "Administration Officer",
                "MSc in Development Studies (WUA), BSc Hons in Sociology and Gender Development Studies (WUA), Diploma "
                        + "in Industrial Relations (UZ), Executive Certificate in Strategic HIV/AIDS Project Management (UZ), "
                        + "Certificate in Practical Monitoring and Evaluation (UZ), LCCI Certificate in Public Relations, LCCI "
                        + "Certificate in Selling and Sales Management, Certificate in Systemic Family Counselling (CONNECT), "
                        + "Certificate in Systemic Approach to HIV/AIDS Counselling (Regional Aids Training Network).",
                "MSc in Development Studies (WUA)",
                null, null);

        seedStaff("Mr", "Welcome Takunda", "Chigwende", "wchigwende@arts.uz.ac.zw", "+263778304858",
                RIID, "Innovation Lead, Humanities and Creative Arts",
                "Welcome Takunda Chigwende is a Zimbabwean archaeologist and heritage conservationist currently serving "
                        + "as the Innovation Lead for Humanities and Creative Arts within the Research, Innovation, and "
                        + "Industrialisation Directorate (RIID). He is a PhD candidate with an MA in Heritage Studies from the "
                        + "University of Zimbabwe. His work bridges academic research, community engagement, and policy "
                        + "advocacy. Welcome Takunda specialises in transforming cultural heritage into sustainable economic "
                        + "drivers, focusing on the intersection of archaeology, creative economies, and industrial "
                        + "innovation. He has collaborated internationally to document and interpret cultural sites and "
                        + "remains dedicated to empowering local communities to take ownership of their heritage narratives.",
                "MA in Heritage Studies (UZ)",
                "Heritage Materials Production, LSA Archaeology Rock Art and Pigment Analysis, Experimental Archaeology, "
                        + "Heritage Management and Policy, Creative Economies and Cultural Industries, "
                        + "Digital Heritage and Augmented Reality (AR), Community Engagement and Heritage Rights, "
                        + "Archaeological Impact Assessments (AIA), Ethnobotany and Cultural Landscapes, "
                        + "Later Stone Age (LSA) Studies",
                null);

        seedStaff("Mr", "Davison Shingai", "Maseva", "dmaseva@vnagro.uz.ac.zw", "0774163046",
                "UZAIP", "Production Manager, Facilities Engineer",
                "B. Eng. Industrial Engineering Degree.",
                "B. Eng. Industrial Engineering",
                null, null);
    }

    private void seedStaff(String title, String name, String surname, String email, String phone,
                           String department, String position, String bio, String qualification,
                           String researchInterests, String notes) {
        boolean universityAddress = email.endsWith(".uz.ac.zw");
        seed(new RegisterRequest(
                name,
                surname,
                email,
                TEST_PASSWORD,
                title,
                title + " " + name + " " + surname,
                phone,
                bio,
                researchInterests,
                null,
                position,
                universityAddress ? email : null,
                phone,
                qualification,
                null,
                null,
                null,
                department,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                researchInterests,
                null,
                notes,
                photo(email),
                null,
                null,
                null,
                null
        ), Role.VIEWER);
    }

    // Staff photos extracted from seed_data.doc, named after the local part of the email.
    private String photo(String email) {
        var resource = new ClassPathResource("seed/photos/" + email.substring(0, email.indexOf('@')) + ".jpg");
        if (!resource.exists()) {
            return null;
        }
        try (var in = resource.getInputStream()) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (IOException e) {
            log.warn("Could not read seed photo for {}", email, e);
            return null;
        }
    }

    // Seeded accounts skip signup OTP, so they're created already verified.
    private void seed(RegisterRequest request, Role role) {
        User user = userRepository.findByEmail(request.email())
                .or(() -> migrateTestEmail(request.email()))
                .orElseGet(() -> {
                    log.info("Seeding {}", request.email());
                    var created = userService.createUser(request, AccountStatus.ACTIVE, role);
                    return userRepository.findById(created.id()).orElseThrow();
                });

        // Reconcile accounts seeded by an earlier version of this seeder.
        boolean changed = false;
        if (user.getProfilePhotoDataUrl() == null && request.profilePhotoDataUrl() != null) {
            user.setProfilePhotoDataUrl(request.profilePhotoDataUrl());
            changed = true;
        }
        if (!user.isEnabled()) {
            user.setEnabled(true);
            changed = true;
        }
        if (user.getRole() != role) {
            user.setRole(role);
            user.setTokenVersion(user.getTokenVersion() + 1);
            changed = true;
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            user.setStatus(AccountStatus.ACTIVE);
            changed = true;
        }
        if (changed) {
            userRepository.save(user);
        }
    }

    // Earlier versions seeded "name.test@domain"; move those accounts onto the real address.
    private Optional<User> migrateTestEmail(String email) {
        int at = email.indexOf('@');
        String testEmail = email.substring(0, at) + ".test" + email.substring(at);
        return userRepository.findByEmail(testEmail).map(user -> {
            log.info("Migrating {} to {}", testEmail, email);
            user.setEmail(email);
            if (testEmail.equals(user.getUniversityEmail())) {
                user.setUniversityEmail(email);
            }
            return userRepository.save(user);
        });
    }
}
