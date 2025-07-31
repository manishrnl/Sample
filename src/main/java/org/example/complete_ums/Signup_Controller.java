package org.example.complete_ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.NavigationManager;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Signup_Controller implements Initializable {
    Button3DEffect button3DEffect;
    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;

    NavigationManager navigationManager = NavigationManager.getInstance();
    private final ObservableList<String> BTech = FXCollections.observableArrayList(
            "Computer Science & Engineering", "Electronics & Communication Engg.",
            "Mechanical Engineering", "Civil Engineering", "Electrical Engineering",
            "Information Technology", "Aerospace Engineering"
    );
    private final ObservableList<String> BE = BTech; // Or a separate list like: FXCollections.observableArrayList("Software Engineering", "Chemical Engineering");

    private final ObservableList<String> BSc = FXCollections.observableArrayList(
            "Physics", "Chemistry", "Mathematics", "Biology", "Computer Science", "Environmental Science"
    );
    private final ObservableList<String> BCA = FXCollections.observableArrayList(
            "Software Development", "Web Design", "Networking", "Database Management");
    private final ObservableList<String> BBA = FXCollections.observableArrayList(
            "Marketing", "Finance", "Human Resources", "Operations Management");
    private final ObservableList<String> BA = FXCollections.observableArrayList(
            "English Literature", "History", "Political Science", "Sociology", "Economics", "Psychology");
    private final ObservableList<String> BCom = FXCollections.observableArrayList(
            "Accounting", "Finance", "Taxation", "Banking & Insurance");
    private final ObservableList<String> LLB = FXCollections.observableArrayList(
            "Constitutional Law", "Criminal Law", "Corporate Law", "International Law");
    private final ObservableList<String> BEd = FXCollections.observableArrayList(
            "Primary Education", "Secondary Education", "Special Education", "Physical Education");
    private final ObservableList<String> BArch = FXCollections.observableArrayList(
            "Architectural Design", "Urban Design", "Sustainable Architecture", "Landscape Architecture");
    private final ObservableList<String> BDes = FXCollections.observableArrayList(
            "Graphic Design", "Fashion Design", "Product Design", "Interior Design", "UX/UI Design");
    private final ObservableList<String> BFA = FXCollections.observableArrayList(
            "Painting", "Sculpture", "Applied Arts", "Visual Communication", "Animation");
    private final ObservableList<String> BHM = FXCollections.observableArrayList(
            "Hotel Management", "Hospitality Administration", "Food & Beverage Services", "Culinary Arts");
    private final ObservableList<String> BVoc = FXCollections.observableArrayList(
            "Retail Management", "Healthcare", "Software Development", "Banking & Finance", "Tourism");

    // Postgraduate Degrees
    private final ObservableList<String> MTech = FXCollections.observableArrayList(
            "Computer Science", "VLSI", "Thermal Engineering", "Structural Engineering", "Embedded Systems");
    private final ObservableList<String> ME = FXCollections.observableArrayList(
            "Civil Engineering", "Mechanical Engineering", "Electrical Engineering", "Electronics Engineering");
    private final ObservableList<String> MSc = FXCollections.observableArrayList(
            "Mathematics", "Physics", "Chemistry", "Computer Science", "Biochemistry", "Microbiology");
    private final ObservableList<String> MCA = FXCollections.observableArrayList(
            "Software Development", "Data Science", "Cyber Security", "AI & ML");
    private final ObservableList<String> MBA = FXCollections.observableArrayList(
            "Finance", "Marketing", "HRM", "Business Analytics", "Operations Management", "International Business");
    private final ObservableList<String> MA = FXCollections.observableArrayList(
            "English", "Hindi", "History", "Sociology", "Political Science", "Psychology");
    private final ObservableList<String> MCom = FXCollections.observableArrayList(
            "Accountancy", "Finance", "Banking", "Taxation", "Business Law");
    private final ObservableList<String> LLM = FXCollections.observableArrayList(
            "Constitutional Law", "Business Law", "International Law", "Human Rights Law");
    private final ObservableList<String> MEd = FXCollections.observableArrayList(
            "Educational Leadership", "Inclusive Education", "Curriculum Studies", "Educational Technology");
    private final ObservableList<String> MArch = FXCollections.observableArrayList(
            "Urban Design", "Sustainable Architecture", "Heritage Conservation");
    private final ObservableList<String> MDes = FXCollections.observableArrayList(
            "Interaction Design", "Industrial Design", "Fashion Design", "Visual Communication");
    private final ObservableList<String> MFA = FXCollections.observableArrayList(
            "Painting", "Applied Arts", "Photography", "Animation", "Sculpture");
    private final ObservableList<String> MVoc = FXCollections.observableArrayList(
            "Retail Management", "Tourism & Hospitality", "Banking", "Software Development");

    // Diploma & Certificate
    private final ObservableList<String> Diploma = FXCollections.observableArrayList(
            "Mechanical Engineering", "Civil Engineering", "Computer Engineering", "Electrical Engineering", "Electronics");
    private final ObservableList<String> AdvancedDiploma = FXCollections.observableArrayList(
            "Web Development", "Networking", "Data Science", "Animation", "Fashion Design");
    private final ObservableList<String> PGDiploma = FXCollections.observableArrayList(
            "Management", "Data Analytics", "Journalism", "Counseling", "Business Analytics");
    private final ObservableList<String> CertificateCourse = FXCollections.observableArrayList(
            "Python", "Digital Marketing", "Graphic Design", "Spoken English", "MS Office");
    private final ObservableList<String> SkillDevelopmentProgram = FXCollections.observableArrayList(
            "Electrician", "Plumber", "Mobile Repair", "Beautician", "Welding");

    // Professional & Doctorate Degrees
    private final ObservableList<String> PhD = FXCollections.observableArrayList(
            "Computer Science", "Physics", "Chemistry", "Mathematics", "Management", "Law", "Literature");
    private final ObservableList<String> CA = FXCollections.observableArrayList(
            "Foundation", "Intermediate", "Final");
    private final ObservableList<String> CFA = FXCollections.observableArrayList(
            "Level I", "Level II", "Level III");
    private final ObservableList<String> CS = FXCollections.observableArrayList(
            "Foundation", "Executive", "Professional");
    private final ObservableList<String> ICWA = FXCollections.observableArrayList(
            "Foundation", "Intermediate", "Final");
    private final ObservableList<String> MBBS = FXCollections.observableArrayList(
            "General Medicine", "Pediatrics", "Orthopedics", "ENT", "Gynecology");
    private final ObservableList<String> BDS = FXCollections.observableArrayList(
            "Oral Surgery", "Orthodontics", "Periodontics", "Prosthodontics");
    private final ObservableList<String> MDS = FXCollections.observableArrayList(
            "Oral Medicine", "Pedodontics", "Conservative Dentistry", "Oral Surgery");
    private final ObservableList<String> BAMS = FXCollections.observableArrayList(
            "Kayachikitsa", "Shalya Tantra", "Panchakarma", "Rasashastra");
    private final ObservableList<String> BHMS = FXCollections.observableArrayList(
            "Materia Medica", "Organon of Medicine", "Repertory", "Surgery");
    private final ObservableList<String> BUMS = FXCollections.observableArrayList(
            "Ilmul Advia", "Tahaffuzi wa Samaji Tib", "Moalajat", "Ilmul Qabalat wa Amraze Niswan");
    private final ObservableList<String> BPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmacology", "Pharmaceutical Chemistry", "Pharmacognosy");
    private final ObservableList<String> MPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmacology", "Industrial Pharmacy", "Quality Assurance");
    private final ObservableList<String> DPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmaceutical Chemistry", "Pharmacology", "Hospital Pharmacy");
    private final ObservableList<String> Nursing = FXCollections.observableArrayList(
            "General Nursing", "Midwifery", "Community Health", "Pediatric Nursing");
    private final ObservableList<String> GNM = FXCollections.observableArrayList(
            "Nursing Foundations", "Medical Surgical Nursing", "Mental Health Nursing");
    private final ObservableList<String> ANM = FXCollections.observableArrayList(
            "Community Health", "Child Health Nursing", "Midwifery", "Health Promotion");

    // Default stream list for courses without specific streams
    private final ObservableList<String> DEFAULT_STREAMS = FXCollections.observableArrayList("General", "Not Applicable", "No specific stream");

    private final Map<String, ObservableList<String>> courseStreamMap = new HashMap<>();

    private File selectedImageFile;  // Class-level variable
    @FXML
    private TextField firstNameField, lastNameField, emailField, mobileField, aadharField, panField, usernameField, emergencyContactNameField, emergencyContactNumberField, fathersNameField, mothersNameField, parentsMobileField, studentRegNumberField, studentRollNumberField, studentProgramField, studentCurrentAcademicYearField, studentCurrentSemesterField,
            school10NameField, school10PassingYearField, school10PercentageField, school12NameField, school12PassingYearField, school12PercentageField,
            teacherExperienceField, staffDesignationField, staffExperienceField, accountantQualificationField, accountantCertificationField, accountantExperienceField, accountantDesignationField,
            librarianQualificationField, librarianCertificationField, librarianExperienceField, librarianDesignationField,
            adminDesignationField, showPasswordField, showConfirmPasswordField;

    @FXML
    private ComboBox<String> genderComboBox, nationalityComboBox, maritalStatusComboBox, roleComboBox,
            emergencyRelationComboBox, referencedViaComboBox, studentStreamCombo, courseComboBox, studentBatchCombo,
            teacherEmploymentTypeComboBox, teacherDepartmentComboBox,
            teacherSpecialisationComboBox, staffDepartmentComboBox, staffEmploymentTypeComboBox,
            accountantDepartmentComboBox, librarianDepartmentComboBox, adminDepartmentComboBox, adminAccessLevelComboBox,
            bloodGroupComboBox, teacherQualificationComboBox, teacherDesignationComboBox;

    @FXML
    private ImageView eyeIconPassword, eyeIconConfirmPassword;
    private boolean isPasswordVisible = false, isConfirmPasswordVisible = false;
    @FXML
    private PasswordField passwordField, confirmPasswordField;
    @FXML
    private DatePicker dobPicker, studentEnrolledOnPicker, teacherLastTaughtOnPicker;
    @FXML
    private TextArea permanentAddressArea, temporaryAddressArea;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button CancelButton, RegisterButton, UploadImageButton;
    @FXML
    private VBox dynamicFieldsContainer, StudentsFields, teacherFields, staffFields, accountantFields, librarianFields, adminFields;
    static int count = 1;
    public static String REGISTRATION_NUMBER = "";
    String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";
    String MOBILE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
    String AADHAR_REGEX = "^(\\d{4}-?\\d{4}-?\\d{4}|\\d{12})$";
    String PAN_REGEX = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$";
    String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    int currentYear = LocalDate.now().getYear();
    Boolean userNameTaken = false, AadharTaken = false;

    private ObservableList<String> getAllCourseNames() {
        return FXCollections.observableArrayList("B.Tech", "B.E", "B.Sc", "BCA", "BBA", "BA", "B.Com", "LLB", "B.Ed", "B.Arch", "B.Des", "BFA", "BHM", "B.Voc", "M.Tech", "M.E", "M.Sc", "MCA", "MBA", "MA", "M.Com", "LLM", "M.Ed", "M.Arch", "M.Des", "MFA", "M.Voc", "Diploma", "Advanced Diploma", "PG Diploma", "Certificate Course", "Skill Development Program", "Ph.D", "CA", "CFA", "CS", "ICWA", "MBBS", "BDS", "MDS", "BAMS", "BHMS", "BUMS", "B.Pharm", "M.Pharm", "D.Pharm", "Nursing", "GNM", "ANM");
    }

    private ObservableList<String> getAllNationality() {
        return FXCollections.observableArrayList("Indian", "American", "British", "Canadian", "Australian", "Chinese", "Japanese", "German", "French", "Italian", "Spanish", "Russian", "Brazilian", "South African", "Mexican", "Other");
    }

    private void populateCourseStreamMap() {
        // Undergraduate Degrees
        courseStreamMap.put("B.Tech", BTech);
        courseStreamMap.put("B.E", BE);
        courseStreamMap.put("B.Sc", BSc);
        courseStreamMap.put("BCA", BCA);
        courseStreamMap.put("BBA", BBA);
        courseStreamMap.put("BA", BA);
        courseStreamMap.put("B.Com", BCom);
        courseStreamMap.put("LLB", LLB);
        courseStreamMap.put("B.Ed", BEd);
        courseStreamMap.put("B.Arch", BArch);
        courseStreamMap.put("B.Des", BDes);
        courseStreamMap.put("BFA", BFA);
        courseStreamMap.put("BHM", BHM);
        courseStreamMap.put("B.Voc", BVoc);

        // Postgraduate Degrees
        courseStreamMap.put("M.Tech", MTech);
        courseStreamMap.put("M.E", ME);
        courseStreamMap.put("M.Sc", MSc);
        courseStreamMap.put("MCA", MCA);
        courseStreamMap.put("MBA", MBA);
        courseStreamMap.put("MA", MA);
        courseStreamMap.put("M.Com", MCom);
        courseStreamMap.put("LLM", LLM);
        courseStreamMap.put("M.Ed", MEd);
        courseStreamMap.put("M.Arch", MArch);
        courseStreamMap.put("M.Des", MDes);
        courseStreamMap.put("MFA", MFA);
        courseStreamMap.put("M.Voc", MVoc);

        // Diploma & Certificate
        courseStreamMap.put("Diploma", Diploma);
        courseStreamMap.put("Advanced Diploma", AdvancedDiploma);
        courseStreamMap.put("PG Diploma", PGDiploma);
        courseStreamMap.put("Certificate Course", CertificateCourse);
        courseStreamMap.put("Skill Development Program", SkillDevelopmentProgram);

        // Professional & Doctorate Degrees
        courseStreamMap.put("Ph.D", PhD);
        courseStreamMap.put("CA", CA);
        courseStreamMap.put("CFA", CFA);
        courseStreamMap.put("CS", CS);
        courseStreamMap.put("ICWA", ICWA);
        courseStreamMap.put("MBBS", MBBS);
        courseStreamMap.put("BDS", BDS);
        courseStreamMap.put("MDS", MDS);
        courseStreamMap.put("BAMS", BAMS);
        courseStreamMap.put("BHMS", BHMS);
        courseStreamMap.put("BUMS", BUMS);
        courseStreamMap.put("B.Pharm", BPharm);
        courseStreamMap.put("M.Pharm", MPharm);
        courseStreamMap.put("D.Pharm", DPharm);
        courseStreamMap.put("Nursing", Nursing);
        courseStreamMap.put("GNM", GNM);
        courseStreamMap.put("ANM", ANM);
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {

        showPasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        showConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        button3DEffect.applyEffect(UploadImageButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(RegisterButton, "/sound/sound2.mp3");
        button3DEffect.applyEffect(CancelButton, "/sound/sound2.mp3");


        studentEnrolledOnPicker.setValue(LocalDate.now());
        LocalDate date = LocalDate.now();
        int currentYear = LocalDate.now().getYear();
        dobPicker.setValue(LocalDate.of(2000, 03, 14));
        String batch6Year = currentYear + "-" + (currentYear + 6);
        String batch5Year = currentYear + "-" + (currentYear + 5);
        String batch4year = currentYear + "-" + (currentYear + 4);
        String batch3Year = currentYear + "-" + (currentYear + 3);
        String batch2Year = currentYear + "-" + (currentYear + 2);
        studentBatchCombo.setItems(FXCollections.observableArrayList(batch2Year, batch3Year,
                batch4year, batch5Year, batch6Year));
        studentBatchCombo.setPromptText("Select Batch");
        genderComboBox.setValue("Male");
        maritalStatusComboBox.setValue("Single");
        nationalityComboBox.setValue("Indian");
        bloodGroupComboBox.setValue("A+");
        emergencyRelationComboBox.setValue("Father");

        school10PassingYearField.setText(String.valueOf(currentYear - 3));
        school12PassingYearField.setText(String.valueOf(currentYear - 1));

        populateCourseStreamMap();
        HideUnhideFields(false, false, false, false, false, false);

        nationalityComboBox.setItems(getAllNationality());
        courseComboBox.setItems(getAllCourseNames());
        courseComboBox.setPromptText("Select Course"); // Ensure prompt text is visible

        studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream"));
        studentStreamCombo.setPromptText("Select Stream");

        courseComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> streams = courseStreamMap.getOrDefault(newValue, DEFAULT_STREAMS);
                studentStreamCombo.setItems(streams);
                studentStreamCombo.getSelectionModel().selectFirst(); // Select the first item or clear if needed
            } else {
                studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream"));
                studentStreamCombo.setPromptText("Select Stream");
            }
        });


    }

    private Boolean checkIfValueTaken(String TableName, String FieldToFind, String value) {
        String Query = "SELECT " + FieldToFind + " FROM " + TableName + " WHERE " + FieldToFind + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(Query)) {

            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // returns true if value exists

        } catch (SQLException e) {
            LoadFrame.setMessage(errorMessageLabel,
                    "Database error while trying to fetch data in Signup_Controller" +
                            ".checkIfValueTaken : in " + Query + " " + e.getMessage(), "RED");
        }

        return false; //  Return false if any error occurs
    }


    @FXML
    void handleRegisterButton(ActionEvent event) {
        try {
            loadFrame.setMessage(errorMessageLabel, "", "");
            String role = roleComboBox.getSelectionModel().getSelectedItem();
            String username = usernameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String studentsMobile = mobileField.getText();
            String parentsMobile = parentsMobileField.getText();
            String aadhar = aadharField.getText();
            String bloodGroup = bloodGroupComboBox.getSelectionModel().getSelectedItem();
            String pan = panField.getText();
            String gender = genderComboBox.getSelectionModel().getSelectedItem();
            String temporaryAddress = temporaryAddressArea.getText();
            String permanentAddress = permanentAddressArea.getText();
            String nationality = nationalityComboBox.getSelectionModel().getSelectedItem();
            String maritalStatus = maritalStatusComboBox.getSelectionModel().getSelectedItem();
            String emergencyContactName = emergencyContactNameField.getText();
            String emergencyContactNumber = emergencyContactNumberField.getText();
            String emergencyContactRelation = emergencyRelationComboBox.getSelectionModel().getSelectedItem();
            String referencedVia = referencedViaComboBox.getSelectionModel().getSelectedItem();
            String fathersName = fathersNameField.getText();
            String mothersName = mothersNameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            Image image = profileImage.getImage();
            String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : ""; // YYYY-MM-DD


            // --- Start of Validation Logic (Unchanged) ---
            if (role == null || role.isEmpty()) {
                loadFrame.setMessage(errorMessageLabel, "Please select a role to get Additional details columns", "RED");
                return;
            }

            if (checkIfValueTaken("Authentication", "UserName", username)) {
                loadFrame.setMessage(errorMessageLabel, "User Name is already taken. Please " +
                        "choose another one.", "RED");
                return;
            }
            if (checkIfValueTaken("Users", "Aadhar", aadhar)) {
                loadFrame.setMessage(errorMessageLabel, "Aadhar is already registered. Please use a different Aadhar number.", "RED");
                return;
            }
            if (checkIfValueTaken("Users", "Pan", pan)) {
                loadFrame.setMessage(errorMessageLabel, "PAN is already registered. Please " +
                        "use a different Aadhar number.", "RED");
                return;
            }
            if (checkIfValueTaken("Users", "Mobile", studentsMobile)) {
                loadFrame.setMessage(errorMessageLabel, "StudentManagement Mobile is already " +
                        "registered. Please use a different Aadhar number.", "RED");
                return;
            }
            if (checkIfValueTaken("Users", "Email", email)) {
                loadFrame.setMessage(errorMessageLabel, "Email is already registered. Please" +
                        " use a different Aadhar number.", "RED");
                return;
            }

            if (firstName.isEmpty() || lastName.isEmpty() || studentsMobile.isEmpty() || aadhar.isEmpty() || username.isEmpty() || pan.isEmpty() || parentsMobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || maritalStatus.isEmpty() || gender.isEmpty() || nationality.isEmpty() || fathersName.isEmpty() || mothersName.isEmpty() || emergencyContactName.isEmpty() || emergencyContactNumber.isEmpty() || emergencyContactRelation.isEmpty() || temporaryAddress.isEmpty() || permanentAddress.isEmpty() || bloodGroup == null || bloodGroup.isEmpty()) {
                loadFrame.setMessage(errorMessageLabel, "Please fill in all general required fields.", "RED");
                return;
            }
            if (referencedVia == null) {
                loadFrame.setMessage(errorMessageLabel, "Please select how you got to know about us in reference via column", "RED");
                return;
            }
            if (image == null || image.getUrl() == null || image.getUrl().isEmpty()) {
                loadFrame.setMessage(errorMessageLabel, "Please upload a profile picture.", "RED");
                return;
            }


            if (!studentsMobile.matches(MOBILE_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Please enter a valid mobile number.", "RED");
                return;
            }
            if (!parentsMobile.matches(MOBILE_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Please enter a valid parent's mobile number.", "RED");
                return;
            }
            if (!pan.matches(PAN_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Please enter a valid PAN number.", "RED");
                return;
            }
            if (!aadhar.matches(AADHAR_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Please enter a valid Aadhar number.", "RED");
                return;
            }
            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Please enter a valid email address.", "RED");
                return;
            }
            if (!password.equals(confirmPassword)) {
                loadFrame.setMessage(errorMessageLabel, "Passwords do not match.", "RED");
                return;
            }
            if (!password.matches(PASSWORD_REGEX)) {
                loadFrame.setMessage(errorMessageLabel, "Password must contain at least 10 characters with a combination of uppercase, lowercase, digit, and special character.", "RED");
                return;
            }

            // --- Role-Specific Validation --- (StudentManagement)
            String studentCourse = null, stream = null, studentBatch = null;
            String school10Name = null, school10PassingYear = null, school10Percentage = null;
            String school12Name = null, school12PassingYear = null, school12Percentage = null;

            //Role based Validation for Admins
            String adminDesignation = null, adminDepartment = null, adminAccessLevel = null;

            //Role based Validation for Teachers
            String teacherEmploymentType = null, teacherDepartment = null, teacherDesignation = null,
                    teacherQualification = null, teacherLastTaught = null, teacherSpecialization = null, teacherExperience = null;


            //Role based Validation for Staff
            String staffDesignation = null, staffDepartment = null, staffEmploymentType = null, staffExperience = null;

            //Role based Validation for Accountants
            String accountantQualification = null, accountantCertification = null, accountantExperience = null, accountantDesignation = null, accountantDepartment = null;

            //Role based Validation for Librarians
            String librarianQualification = null, librarianCertification = null, librarianExperience = null, librarianDesignation = null, librarianDepartment = null;


            if (role.equals("Student")) {
                studentCourse = courseComboBox.getValue();
                stream = studentStreamCombo.getValue();
                studentBatch = studentBatchCombo.getValue();
                school10Name = school10NameField.getText();
                school10PassingYear = school10PassingYearField.getText();
                school10Percentage = school10PercentageField.getText();
                school12Name = school12NameField.getText();
                school12PassingYear = school12PassingYearField.getText();
                school12Percentage = school12PercentageField.getText();

                if (studentCourse == null || studentCourse.isEmpty() || stream == null || stream.isEmpty() || studentBatch == null || studentBatch.isEmpty() ||
                        school10Name.isEmpty() || school10PassingYear.isEmpty() || school10Percentage.isEmpty() ||
                        school12Name.isEmpty() || school12PassingYear.isEmpty() || school12Percentage.isEmpty()) {
                    loadFrame.setMessage(errorMessageLabel, "Please fill in all required student academic details.", "RED");
                    return;
                }
            } else if (role.equals("Admin")) {
                adminDesignation = adminDesignationField.getText();
                adminDepartment = adminDepartmentComboBox.getSelectionModel().getSelectedItem();
                adminAccessLevel = adminAccessLevelComboBox.getSelectionModel().getSelectedItem();
                if (adminDesignation.isEmpty() || adminDepartment == null || adminAccessLevel == null) {
                    loadFrame.setMessage(errorMessageLabel, "Please fill in all required admin details.", "RED");
                    return;
                }

            } else if (role.equals("Teacher")) {
                teacherDepartment =
                        teacherDepartmentComboBox.getSelectionModel().getSelectedItem();
                teacherDesignation = teacherDesignationComboBox.getSelectionModel().getSelectedItem();
                teacherQualification = teacherQualificationComboBox.getSelectionModel().getSelectedItem();
                teacherSpecialization = teacherSpecialisationComboBox.getSelectionModel().getSelectedItem();
                teacherEmploymentType = teacherEmploymentTypeComboBox.getSelectionModel().getSelectedItem();
                teacherExperience = teacherExperienceField.getText();
                teacherLastTaught = teacherLastTaughtOnPicker.getValue() != null ? teacherLastTaughtOnPicker.getValue().toString() : ""; // YYYY-MM-DD
                if (teacherEmploymentType.isEmpty() || teacherDesignation.isEmpty() || teacherSpecialization.isEmpty() ||
                        teacherQualification.isEmpty() || teacherExperience.isEmpty()) {
                    loadFrame.setMessage(errorMessageLabel, "Please fill in all required teacher details.", "RED");
                    return;
                }
            } else if (role.equals("Staff")) {
                staffDepartment = staffDepartmentComboBox.getSelectionModel().getSelectedItem();
                staffDesignation = staffDesignationField.getText();
                staffEmploymentType = staffEmploymentTypeComboBox.getSelectionModel().getSelectedItem();
                staffExperience = staffExperienceField.getText();

            } else if (role.equals("Accountant")) {
                accountantCertification = accountantCertificationField.getText();
                accountantQualification = accountantQualificationField.getText();
                accountantExperience = accountantExperienceField.getText();
                accountantDesignation = accountantDesignationField.getText();
                accountantDepartment = accountantDepartmentComboBox.getSelectionModel().getSelectedItem();
                if (accountantQualification.isEmpty() || accountantCertification.isEmpty() ||
                        accountantExperience.isEmpty() || accountantDesignation.isEmpty() || accountantDepartment == null) {
                    loadFrame.setMessage(errorMessageLabel, "Please fill in all required accountant details.", "RED");
                    return;
                }

            } else if (role.equals("Librarian")) {
                librarianQualification = librarianQualificationField.getText();
                librarianCertification = librarianCertificationField.getText();
                librarianExperience = librarianExperienceField.getText();
                librarianDesignation = librarianDesignationField.getText();
                librarianDepartment = librarianDepartmentComboBox.getSelectionModel().getSelectedItem();
                if (librarianQualification.isEmpty() || librarianCertification.isEmpty() ||
                        librarianExperience.isEmpty() || librarianDesignation.isEmpty() || librarianDepartment == null) {
                    loadFrame.setMessage(errorMessageLabel, "Please fill in all required librarian details.", "RED");
                    return;
                }

            }

            Connection connection = null;
            try {
                connection = DatabaseConnection.getConnection();
                connection.setAutoCommit(false);

                String insertUserSQL = "INSERT INTO Users (Role, First_Name, Last_Name, Aadhar, Pan, " +
                        "Mobile, Alternate_Mobile, Email, Gender, DOB, Blood_Group, Marital_Status, " +
                        "Nationality, Emergency_Contact_Name, Emergency_Contact_Relationship, " +
                        "Emergency_Contact_Mobile, Temporary_Address, Permanent_Address, Fathers_Name, " +
                        "Mothers_Name, Referenced_Via, Admin_Approval_Status, Photo_URL,User_Status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, " +
                        "?, ?, ?)";

                String insertIntAuthentication = "INSERT INTO Authentication (User_Id, UserName, Password_Hash) VALUES (?, ?, ?)";
                int newUserId = -1;

                // Using Statement.RETURN_GENERATED_KEYS to get the new User_Id efficiently
                try (PreparedStatement userPstmt = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
                    userPstmt.setString(1, role);
                    userPstmt.setString(2, firstName);
                    userPstmt.setString(3, lastName);
                    userPstmt.setString(4, aadhar);
                    userPstmt.setString(5, pan);
                    userPstmt.setString(6, studentsMobile);
                    userPstmt.setString(7, parentsMobile);
                    userPstmt.setString(8, email);
                    userPstmt.setString(9, gender);
                    userPstmt.setString(10, dob);
                    userPstmt.setString(11, bloodGroup);
                    userPstmt.setString(12, maritalStatus);
                    userPstmt.setString(13, nationality);
                    userPstmt.setString(14, emergencyContactName);
                    userPstmt.setString(15, emergencyContactRelation);
                    userPstmt.setString(16, emergencyContactNumber);
                    userPstmt.setString(17, temporaryAddress);
                    userPstmt.setString(18, permanentAddress);
                    userPstmt.setString(19, fathersName);
                    userPstmt.setString(20, mothersName);
                    userPstmt.setString(21, referencedVia);
                    String adminApprovalStatus = "Student".equals(role) ? "Approved" : "Pending";
                    userPstmt.setString(22, adminApprovalStatus);
                    userPstmt.setString(24, "Active"); // Assuming User_Status is a new column to track user status

                    if (selectedImageFile != null && selectedImageFile.exists()) {
                        try (InputStream is = new FileInputStream(selectedImageFile)) {
                            userPstmt.setBlob(23, is);
                            userPstmt.executeUpdate();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println("No image file selected or file does not exist. Setting Photo_URL to NULL.");
                        userPstmt.setNull(23, Types.BLOB);
                        userPstmt.executeUpdate();
                    }

                    try (ResultSet generatedKeys = userPstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            newUserId = generatedKeys.getInt(1);
                        } else {
                            // This will trigger the rollback if the insert succeeded but we couldn't get the ID
                            throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }


                    try (PreparedStatement authPstmt = connection.prepareStatement(insertIntAuthentication)) {
                        authPstmt.setInt(1, newUserId);
                        authPstmt.setString(2, username);
                        authPstmt.setString(3, password);

                        int authRowsAffected = authPstmt.executeUpdate();
                        if (authRowsAffected == 0) {
                            throw new SQLException("Creating authentication details failed, no rows affected.");
                        }
                    } catch (SQLException e) {
                        throw new SQLException("Error inserting authentication details: " + e.getMessage());
                    }
                }


                // PreparedStatement is closed here

                // 2. If the role is "Student", insert into the StudentManagement table
                if (role.equals("Student")) {
                    if (newUserId == -1) {
                        throw new SQLException("Cannot insert student details, failed to get User ID.");
                    }


                    String registrationNumber = "MANISH" + currentYear + newUserId; // Generate registration number

                    String insertStudentSQL = "INSERT INTO Students (User_Id, Roll_Number, Course, School_12_Percentage, " +
                            "Stream, Batch, Current_Academic_Year, Current_Semester, School_10_Name, " +
                            "School_10_Passing_Year, School_10_Percentage, School_12_Name, " +
                            "School_12_Passing_Year, Registration_Number) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


                    try (PreparedStatement studentPstmt = connection.prepareStatement(insertStudentSQL)) {
                        studentPstmt.setInt(1, newUserId);
                        studentPstmt.setInt(2, count++); // Assuming 'count' is a class member for roll number
                        studentPstmt.setString(3, studentCourse);
                        studentPstmt.setString(4, school12Percentage);
                        studentPstmt.setString(5, stream);
                        studentPstmt.setString(6, studentBatch);
                        studentPstmt.setInt(7, currentYear);
                        studentPstmt.setString(8, "1"); // Defaulting to 1st semester
                        studentPstmt.setString(9, school10Name);
                        studentPstmt.setString(10, school10PassingYear);
                        studentPstmt.setString(11, school10Percentage);
                        studentPstmt.setString(12, school12Name);
                        studentPstmt.setString(13, school12PassingYear);
                        studentPstmt.setString(14, registrationNumber);

                        int studentRowsAffected = studentPstmt.executeUpdate();
                        if (studentRowsAffected == 0) {
                            throw new SQLException("Creating student details failed, no rows affected.");
                        }
                    }
                } else if (role.equals("Teacher")) {
                    if (newUserId == -1) {
                        System.out.println("Cannot insert Teachers details, failed to get " +
                                "User ID.");
                    }
                    String REGISTRATION_NUMBER = "TEACHER" + currentYear + newUserId;
                    String insertIntoTeachers = "INSERT INTO Teachers " +
                            "(Registration_Number, Designation,  Qualification, " +
                            "Specialisation, Employment_Type,Teaching_Experience_Years," +
                            "Last_Taught_on,User_Id) VALUES (?, ?,?, ?, ?, ?, ?, ?)";


                    try (PreparedStatement teacherPstmt = connection.prepareStatement(insertIntoTeachers)) {
                        teacherPstmt.setString(1, REGISTRATION_NUMBER);
                        teacherPstmt.setString(2, teacherDesignation);
                        teacherPstmt.setString(3, teacherQualification);
                        teacherPstmt.setString(4, teacherSpecialization);
                        teacherPstmt.setString(5, teacherEmploymentType);
                        teacherPstmt.setString(6, teacherExperience);
                        teacherPstmt.setDate(7, Date.valueOf(teacherLastTaught));
                        teacherPstmt.setInt(8, newUserId);
                        System.out.println("Signup_Controller.handleRegisterButton" + newUserId);
                        int rowsAffected = teacherPstmt.executeUpdate();
                        if (rowsAffected == 0) {
                            System.out.println("Creating Teacher details failed, no rows " +
                                    "affected.");
                        } else
                            loadFrame.setMessage(errorMessageLabel, "Teacher registration successful!", "GREEN");
                    } catch (Exception e) {
                        loadFrame.setMessage(errorMessageLabel, "Something went wrong while registering Teacher: " + e.getMessage(), "RED");
                        throw new SQLException("Error inserting teacher details: " + e.getMessage());
                    }
                } else if (role.equals("Staff")) {
                } else if (role.equals("Accountant")) {
                } else if (role.equals("Librarian")) {
                } else if (role.equals("Admin")) {
                }

                // Add logic for other roles here, inside the transaction if they need to write to other tables

                // 3. If all operations were successful, commit the transaction
                connection.commit();

                // On successful commit, update session and show success message
                sessionManager.setUserID(newUserId);
                loadFrame.setMessage(errorMessageLabel, "Registration successful! Please login to access your dashboard.", "GREEN");

            } catch (SQLException e) {
                // 4. If any error occurs, roll back the transaction
                loadFrame.setMessage(errorMessageLabel, "Registration Failed: " + e.getMessage(), "RED");
                System.err.println("Transaction is being rolled back. Error: " + e.getMessage());
                e.printStackTrace(); // For debugging

                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        System.err.println("Error during transaction rollback: " + ex.getMessage());
                    }
                }
            } finally {
                // 5. Always close the connection and restore auto-commit mode
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (SQLException ex) {
                        System.err.println("Error closing connection: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (errorMessageLabel != null) {
                errorMessageLabel.setText("Unexpected error: " + ex.getMessage());
            }
        }
    }

    private void HideUnhideFields(boolean studentVisible, boolean teacherVisible, boolean staffVisible, boolean accountantVisible, boolean librarianVisible, boolean adminVisible) {
        StudentsFields.setVisible(studentVisible);
        teacherFields.setVisible(teacherVisible);
        staffFields.setVisible(staffVisible);
        accountantFields.setVisible(accountantVisible);
        librarianFields.setVisible(librarianVisible);
        adminFields.setVisible(adminVisible);

        StudentsFields.setManaged(studentVisible);
        teacherFields.setManaged(teacherVisible);
        staffFields.setManaged(staffVisible);
        accountantFields.setManaged(accountantVisible);
        librarianFields.setManaged(librarianVisible);
        adminFields.setManaged(adminVisible);
    }

    @FXML
    void handleCancelButton(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        navigationManager.navigateTo("Login.fxml");
    }

    @FXML
    void UploadImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            profileImage.setImage(new Image(file.toURI().toString()));
        } else {
            System.out.println("No image selected.");
        }
    }

    @FXML
    private void handleRoleSelection(ActionEvent event) {
        HideUnhideFields(false, false, false, false, false, false);
        dynamicFieldsContainer.getChildren().clear(); // Clear previously added VBoxes

        String selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            VBox fieldsToShow = null; // A temporary reference for the VBox to show
            switch (selectedRole) {
                case "Student":
                    fieldsToShow = StudentsFields;
                    courseComboBox.getSelectionModel().clearSelection();
                    studentStreamCombo.getSelectionModel().clearSelection();
                    studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream")); // Reset streams
                    studentBatchCombo.getSelectionModel().clearSelection();
                    break;
                case "Teacher":
                    fieldsToShow = teacherFields;
                    break;
                case "Staff":
                    fieldsToShow = staffFields;
                    break;
                case "Accountant":
                    fieldsToShow = accountantFields;
                    break;
                case "Librarian":
                    fieldsToShow = librarianFields;
                    break;
                case "Admin":
                    fieldsToShow = adminFields;
                    break;
                default:
                    break;
            }

            if (fieldsToShow != null) {
                fieldsToShow.setVisible(true);
                fieldsToShow.setManaged(true);
                dynamicFieldsContainer.getChildren().add(fieldsToShow);
            }
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        showPasswordField.setVisible(isPasswordVisible);
        showPasswordField.setManaged(isPasswordVisible);
        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        showConfirmPasswordField.setVisible(isConfirmPasswordVisible);
        showConfirmPasswordField.setManaged(isConfirmPasswordVisible);
        confirmPasswordField.setVisible(!isConfirmPasswordVisible);
        confirmPasswordField.setManaged(!isConfirmPasswordVisible);
    }

}

