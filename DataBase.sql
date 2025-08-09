DROP DATABASE IF EXISTS university_management_system;
CREATE DATABASE university_management_system;
USE university_management_system;

CREATE TABLE Users (
                       User_Id INT AUTO_INCREMENT PRIMARY KEY,
                       Role ENUM('Student', 'Teacher', 'Staff', 'Admin', 'Accountant', 'Librarian') NOT NULL,
                       User_Status ENUM('Active', 'Inactive', 'Suspended', 'Archived') DEFAULT 'Inactive',
                       Joined_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       First_Name VARCHAR(30) NOT NULL,
                       Last_Name VARCHAR(30) NOT NULL,
                       Aadhar BIGINT UNIQUE,
                       Pan VARCHAR(20) UNIQUE,
                       Mobile BIGINT UNIQUE,
                       Alternate_Mobile BIGINT,
                       Email VARCHAR(40) UNIQUE,
                       Gender ENUM('Male', 'Female', 'Other'),
                       DOB DATE,
                       Blood_Group VARCHAR(5),
                       Marital_Status VARCHAR(15),
                       Nationality VARCHAR(30),
                       Emergency_Contact_Name VARCHAR(100),
                       Emergency_Contact_Relationship VARCHAR(50),
                       Emergency_Contact_Mobile BIGINT,
                       Temporary_Address VARCHAR(200),
                       Permanent_Address VARCHAR(200),
                       Fathers_Name VARCHAR(50),
                       Mothers_Name VARCHAR(50),
                       Photo_URL LONGBLOB,
                       Referenced_Via VARCHAR(50),
                       Admin_Approval_Status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
                       Created_By_User_Id INT,
                       FOREIGN KEY (Created_By_User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL
);
CREATE TABLE Departments (
                             Department_Id INT AUTO_INCREMENT PRIMARY KEY,
                             Department_Name VARCHAR(100) UNIQUE NOT NULL,
                             Department_Code VARCHAR(10) UNIQUE,
                             Head_Of_Department_User_Id INT,
                             Contact_Email VARCHAR(40),
                             Phone_Number BIGINT,
                             Description TEXT,
                             Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (Head_Of_Department_User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL
);


CREATE TABLE Authentication (
                                Auth_Id INT AUTO_INCREMENT PRIMARY KEY,
                                User_Id INT UNIQUE NOT NULL,
                                UserName VARCHAR(50) NOT NULL,
                                Password_Hash VARCHAR(255) NOT NULL,
                                Salt VARCHAR(64),
                                Last_Login TIMESTAMP,
                                Lockout_Until TIMESTAMP,
                                Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE CASCADE
);

CREATE TABLE Bank_Details (
                              Bank_Detail_Id INT AUTO_INCREMENT PRIMARY KEY,
                              User_Id INT UNIQUE NOT NULL,
                              Bank_Name VARCHAR(100) NOT NULL,
                              Account_Number VARCHAR(50) NOT NULL,
                              IFSC_Code VARCHAR(20) NOT NULL,
                              Branch_Name VARCHAR(100),
                              Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE CASCADE
);
CREATE TABLE Students (
                          User_Id INT PRIMARY KEY,
                          Registration_Number VARCHAR(20) UNIQUE,
                          Roll_Number VARCHAR(20) NULL,
                          Course VARCHAR(50),
                          Stream VARCHAR(100),
                          Batch VARCHAR(100),
                          Department_Id INT,
                          Enrolled_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          Current_Academic_Year INT,
                          Current_Semester INT,
                          School_10_Name VARCHAR(100),
                          School_10_Passing_Year INT,
                          School_10_Percentage FLOAT,
                          School_12_Name VARCHAR(100),
                          School_12_Passing_Year INT,
                          School_12_Percentage FLOAT,
                          FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE CASCADE,
                          FOREIGN KEY (Department_Id) REFERENCES Departments (Department_Id) ON DELETE SET NULL
);

CREATE TABLE Teachers (
                          User_Id INT PRIMARY KEY,
                          Registration_Number VARCHAR(20) UNIQUE NOT NULL,
                          Designation VARCHAR(50),
                          Department_Id INT,
                          Qualification VARCHAR(100),
                          Specialisation VARCHAR(100),
                          Employment_Type ENUM('Full-time', 'Part-time', 'Visiting') DEFAULT 'Full-time',
                          Teaching_Experience_Years INT,
                          Last_Taught_on DATE,
                          Salary DECIMAL(10 , 2 ) DEFAULT NULL,
                          FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE CASCADE,
                          FOREIGN KEY (Department_Id) REFERENCES Departments (Department_Id) ON DELETE SET NULL
);

CREATE TABLE Admins (
                        User_Id INT PRIMARY KEY,
                        Designation VARCHAR(50),
                        Department_Id INT,
                        Access_Level ENUM('Super', 'Departmental', 'Academic', 'Finance', 'Library', 'HR') DEFAULT 'Departmental',
                        FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE,
                        FOREIGN KEY (Department_Id) REFERENCES Departments(Department_Id) ON DELETE SET NULL
);
CREATE TABLE Accountants (
                             User_Id INT PRIMARY KEY,
                             Qualification VARCHAR(100),
                             Certification VARCHAR(100),
                             Experience_Years INT,
                             Designation VARCHAR(50),
                             Department_Id INT,
                             Salary DECIMAL(10,2),
                             FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE,
                             FOREIGN KEY (Department_Id) REFERENCES Departments(Department_Id) ON DELETE SET NULL
);

CREATE TABLE Librarians (
                            User_Id INT PRIMARY KEY,
                            Qualification VARCHAR(100),
                            Certification VARCHAR(100),
                            Experience_Years INT,
                            Designation VARCHAR(50),
                            Department_Id INT,
                            Salary DECIMAL(10,2),
                            FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE,
                            FOREIGN KEY (Department_Id) REFERENCES Departments(Department_Id) ON DELETE SET NULL
);

CREATE TABLE Staffs (
                        User_Id INT PRIMARY KEY,
                        Registration_Number VARCHAR(20) UNIQUE NOT NULL,
                        Designation VARCHAR(50),
                        Department_Id INT DEFAULT null,
                        Employment_Type ENUM('Full-time', 'Part-time', 'Contract') DEFAULT 'Full-time',
                        Work_Experience_Years INT,
                        Salary DECIMAL(10,2),
                        FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE,
                        FOREIGN KEY (Department_Id) REFERENCES Departments(Department_Id) ON DELETE SET NULL
);

CREATE TABLE Courses (
                         Course_Id INT AUTO_INCREMENT PRIMARY KEY,
                         Course_Code VARCHAR(20) UNIQUE NOT NULL,
                         Course_Name VARCHAR(150) NOT NULL,
                         Department_Id INT NOT NULL,
                         Credits DECIMAL(3 , 1 ) NOT NULL,
                         Description TEXT,
                         Is_Active BOOLEAN DEFAULT TRUE,
                         Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (Department_Id) REFERENCES Departments (Department_Id) ON DELETE RESTRICT
);

CREATE TABLE Fee_Types (
                           Departments VARCHAR(100),
                           Fee_Type_Id INT AUTO_INCREMENT PRIMARY KEY,
                           Fee_Type_Name VARCHAR(50) NOT NULL,
                           Description TEXT,
                           Default_Amount DECIMAL(10,2) DEFAULT 0.00,
                           Is_Recurring BOOLEAN DEFAULT FALSE,
                           UNIQUE (Fee_Type_Name, Departments) -- Composite unique constraint
);



CREATE TABLE Enrollments (
                             Enrollment_Id INT AUTO_INCREMENT PRIMARY KEY,
                             Student_Id INT NOT NULL,
                             Course_Id INT NOT NULL,
                             Academic_Year INT NOT NULL,
                             Semester ENUM('1', '2', '3','4', '5', '6','7', '8') NOT NULL,
                             Enrollment_Date DATE NOT NULL,
                             Completion_Date DATE,
                             Grade VARCHAR(5),
                             Status ENUM('Enrolled', 'Completed', 'Dropped', 'Withdrawn') DEFAULT 'Enrolled',
                             Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (Student_Id) REFERENCES Students (User_Id) ON DELETE CASCADE,
                             FOREIGN KEY (Course_Id) REFERENCES Courses (Course_Id) ON DELETE CASCADE,
                             UNIQUE (Student_Id , Course_Id , Academic_Year , Semester)
);

CREATE TABLE Teacher_Courses (
                                 Teacher_Course_Id INT AUTO_INCREMENT PRIMARY KEY,
                                 Teacher_Id INT NOT NULL,
                                 Course_Id INT NOT NULL,
                                 Academic_Year INT NOT NULL,
                                 Semester ENUM('1', '2', '3','4', '5', '6','7', '8'),
                                 Start_Date DATE,
                                 End_Date DATE,
                                 FOREIGN KEY (Teacher_Id) REFERENCES Teachers (User_Id) ON DELETE CASCADE,
                                 FOREIGN KEY (Course_Id) REFERENCES Courses (Course_Id) ON DELETE CASCADE,
                                 UNIQUE (Teacher_Id , Course_Id , Academic_Year , Semester)
);

CREATE TABLE Student_Fees (
                              Fee_Record_Id INT AUTO_INCREMENT PRIMARY KEY,
                              Fees_Paid_By_User_Id INT NOT NULL DEFAULT 23,
                              User_Id INT NOT NULL,
                              Fee_Type_Id INT NOT NULL,
                              Academic_Year INT NOT NULL,
                              Semester ENUM('1', '2', '3','4', '5', '6','7', '8') DEFAULT '1',
                              Fee_Month INT CHECK (Fee_Month BETWEEN 1 AND 12) DEFAULT NULL,
                              Amount_Due DECIMAL(10,2) DEFAULT 0.00,
                              Amount_Paid DECIMAL(10,2) DEFAULT 0.00,
                              Due_Date DATE NOT NULL,
                              Payment_Date DATE,
                              Payment_Status ENUM('Paid', 'Unpaid', 'Partial', 'Waived') DEFAULT 'Unpaid',
                              Transaction_Id VARCHAR(100) UNIQUE,
                              Payment_Mode VARCHAR(20) DEFAULT NULL,
                              Receipt_Number VARCHAR(50) UNIQUE,
                              Remarks TEXT,
                              FOREIGN KEY (User_Id) REFERENCES Students(User_Id) ON DELETE CASCADE ,
                              FOREIGN KEY (Fee_Type_Id) REFERENCES Fee_Types(Fee_Type_Id) ON DELETE RESTRICT
);

CREATE TABLE Salary_Components (
                                   Component_Id INT AUTO_INCREMENT PRIMARY KEY,
                                   Component_Name VARCHAR(50) UNIQUE NOT NULL,
                                   Component_Type ENUM('Earning', 'Deduction') NOT NULL,
                                   Default_Amount Double(10,2) default 100.0,
                                   Description TEXT
);

CREATE TABLE Salary_Payments (
                                 Salary_Payment_Id INT AUTO_INCREMENT PRIMARY KEY,
                                 User_Id INT NOT NULL,
                                 Salary_Month INT CHECK (Salary_Month BETWEEN 1 AND 12) NOT NULL,
                                 Salary_Year INT NOT NULL,
                                 Gross_Amount DECIMAL(10,2) NOT NULL,
                                 Total_Deductions DECIMAL(10,2) DEFAULT 0.00,
                                 Net_Amount_Paid DECIMAL(10,2) DEFAULT 0.00,
                                 Payment_Date DATE,
                                 Payment_Method ENUM('Bank Transfer', 'Cash', 'Cheque') DEFAULT 'Bank Transfer',
                                 Payment_Status ENUM('Paid', 'Unpaid', 'Partial') DEFAULT 'Unpaid',
                                 Transaction_Id VARCHAR(100),
                                 Remarks TEXT,
                                 FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE,
                                 UNIQUE (User_Id, Salary_Month, Salary_Year)
);
CREATE TABLE Salary_Payment_Details (
                                        Detail_Id INT AUTO_INCREMENT PRIMARY KEY,
                                        Salary_Payment_Id INT NOT NULL,
                                        Component_Id INT NOT NULL,
                                        Amount DECIMAL(10 , 2 ) NOT NULL,
                                        FOREIGN KEY (Salary_Payment_Id) REFERENCES Salary_Payments (Salary_Payment_Id) ON DELETE CASCADE,
                                        FOREIGN KEY (Component_Id) REFERENCES Salary_Components (Component_Id) ON DELETE RESTRICT
);
CREATE TABLE Attendances (
                             Attendance_Id INT AUTO_INCREMENT PRIMARY KEY,
                             User_Id INT NOT NULL,
                             Attendance_Date DATE NOT NULL,
                             Time_In TIME,
                             Time_Out TIME,
                             Status ENUM('Present', 'Absent', 'Leave','Late', 'Half Day','S.L','C.L') NOT NULL,
                             Remarks TEXT,
                             FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE CASCADE,
                             UNIQUE (User_Id , Attendance_Date)
);

CREATE TABLE Books (
                       Book_Id INT AUTO_INCREMENT PRIMARY KEY,
                       Title VARCHAR(255) NOT NULL,
                       Author VARCHAR(150) NOT NULL,
                       ISBN VARCHAR(20) UNIQUE,
                       Publisher VARCHAR(100),
                       Publication_Year INT,
                       Genre VARCHAR(50),
                       Total_Copies INT DEFAULT 0,
                       Available_Copies INT DEFAULT 0,
                       Location_Shelf VARCHAR(50),
                       Description TEXT,
                       Added_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
select * from Books;
CREATE TABLE Borrow_Records (
                                Borrow_Id INT AUTO_INCREMENT PRIMARY KEY,
                                Book_Id INT NOT NULL,
                                User_Id INT NULL, -- Set to NULL to allow ON DELETE SET NULL
                                Borrow_Date DATE NOT NULL,
                                Due_Date DATE NOT NULL,
                                Return_Date DATE,
                                Fine_Amount DECIMAL(8 , 2 ) DEFAULT 0.00,
                                Status ENUM('Borrowed', 'Returned', 'Overdue', 'Lost') DEFAULT 'Borrowed',
                                Librarian_User_Id INT,
                                Remarks TEXT,
                                FOREIGN KEY (Book_Id) REFERENCES Books (Book_Id) ON DELETE RESTRICT,
                                FOREIGN KEY (User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL,
                                FOREIGN KEY (Librarian_User_Id) REFERENCES Librarians (User_Id) ON DELETE SET NULL
);

CREATE TABLE Admin_Activity_Log (
                                    Log_Id INT AUTO_INCREMENT PRIMARY KEY,
                                    Admin_User_Id INT NULL, -- Set to NULL to allow ON DELETE SET NULL
                                    Action_Type VARCHAR(50) NOT NULL,
                                    Target_Table VARCHAR(50) NOT NULL,
                                    Target_Record_Id INT,
                                    Action_Details TEXT,
                                    Action_Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    IP_Address VARCHAR(45),
                                    FOREIGN KEY (Admin_User_Id) REFERENCES Admins (User_Id) ON DELETE SET NULL
);


CREATE TABLE Notifications (
                               Notification_Id INT AUTO_INCREMENT PRIMARY KEY,
                               Title VARCHAR(100) NOT NULL,
                               Message TEXT NOT NULL,
                               Target_Role ENUM('Student', 'Teacher', 'Staff', 'Admin', 'Accountant', 'Librarian', 'All Roles') DEFAULT 'All Roles',
                               Target_User_Id INT,
                               Created_By_User_Id INT NULL, -- Set to NULL to allow ON DELETE SET NULL and fix deletion errors
                               Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               Expiry_Date DATE,
                               Is_Read ENUM('Read','Un-Read')DEFAULT 'Un-Read',
                               Read_On TIMESTAMP DEFAULT null,
                               FOREIGN KEY (Created_By_User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL,
                               FOREIGN KEY (Target_User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL
);
CREATE TABLE Exams (
                       Exam_Id INT AUTO_INCREMENT PRIMARY KEY,
                       User_Id INT NOT NULL,
                       Exam_Type VARCHAR(50),
                       Subject VARCHAR(100),
                       Marks_Obtained INT DEFAULT 0,
                       Total_Marks INT DEFAULT 100,
                       Exam_Date DATE,
                       FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);
CREATE TABLE Library (
                         Library_Id INT AUTO_INCREMENT PRIMARY KEY,
                         User_Id INT NOT NULL,
                         Book_Name VARCHAR(255) NOT NULL,
                         Issue_Date DATE NOT NULL,
                         Return_Date DATE,
                         Status ENUM('Issued', 'Returned', 'Overdue') DEFAULT 'Issued',
                         Remarks TEXT,
                         FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);

CREATE TABLE Events (
                        Event_Id INT AUTO_INCREMENT PRIMARY KEY,
                        Event_Name VARCHAR(150) NOT NULL,
                        Event_Description TEXT,
                        Event_Type VARCHAR(50),
                        Event_Date DATE NOT NULL,
                        Start_Time TIME,
                        End_Time TIME,
                        Location VARCHAR(100),
                        Organized_By_User_Id INT,
                        Is_Public BOOLEAN DEFAULT TRUE,
                        Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        Updated_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (Organized_By_User_Id) REFERENCES Users (User_Id) ON DELETE SET NULL
);
CREATE TABLE Hostel (
                        Hostel_Id INT AUTO_INCREMENT PRIMARY KEY,
                        User_Id INT NOT NULL,
                        Room_No VARCHAR(20),
                        Allocation_Date DATE,
                        Status ENUM('Pending', 'Allotted', 'Cancelled') DEFAULT 'Pending',
                        FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);

CREATE TABLE Feedback (
                          Feedback_Id INT AUTO_INCREMENT PRIMARY KEY,
                          User_Id INT NOT NULL,
                          Feedback_Type VARCHAR(50),
                          Comments TEXT,
                          Submitted_On DATE,
                          FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);
CREATE TABLE Course_Mapping (
                                Mapping_Id INT AUTO_INCREMENT PRIMARY KEY,
                                User_Id INT NOT NULL,
                                Course_Name VARCHAR(100),
                                Enrollment_Date DATE,
                                Status ENUM('Pending', 'Enrolled', 'Completed') DEFAULT 'Pending',
                                FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);
CREATE TABLE Event_Participants (
                                    Participation_Id INT AUTO_INCREMENT PRIMARY KEY,
                                    Event_Id INT,
                                    User_Id INT,
                                    Status VARCHAR(50) DEFAULT 'Not Participated',
                                    Participation_Date DATE,
                                    FOREIGN KEY (Event_Id) REFERENCES Events(Event_Id) ON DELETE CASCADE,
                                    FOREIGN KEY (User_Id) REFERENCES Users(User_Id) ON DELETE CASCADE
);

-- Users table
CREATE INDEX idx_users_role ON Users (Role);
CREATE INDEX idx_users_name ON Users (Last_Name, First_Name);
CREATE INDEX idx_users_email ON Users (Email);
CREATE INDEX idx_users_aadhar ON Users (Aadhar); -- For faster lookup by Aadhar
CREATE INDEX idx_users_status ON Users (User_Status); -- For filtering by status

-- Authentication table
CREATE INDEX idx_auth_username ON Authentication (UserName);
CREATE INDEX idx_auth_user_id ON Authentication (User_Id); -- For joins with Users

-- Students table
CREATE INDEX idx_students_registration_number ON Students (Registration_Number);
CREATE INDEX idx_students_roll_number ON Students (Roll_Number);
CREATE INDEX idx_students_department_id ON Students (Department_Id);

-- Student_Fees table
CREATE INDEX idx_student_fees_user_id ON Student_Fees (User_Id);
CREATE INDEX idx_student_fees_fee_type_id ON Student_Fees (Fee_Type_Id);
CREATE INDEX idx_student_fees_academic_year ON Student_Fees (Academic_Year);
CREATE INDEX idx_student_fees_payment_status ON Student_Fees (Payment_Status); -- For filtering by payment status

-- Fee_Types table
CREATE INDEX idx_fee_types_name ON Fee_Types (Fee_Type_Name);

-- Courses table
CREATE INDEX idx_courses_department_id ON Courses (Department_Id);
CREATE INDEX idx_courses_code ON Courses (Course_Code);

-- Enrollments table
CREATE INDEX idx_enrollments_student_id ON Enrollments (Student_Id);
CREATE INDEX idx_enrollments_course_id ON Enrollments (Course_Id);
CREATE INDEX idx_enrollments_academic_year ON Enrollments (Academic_Year, Semester);

-- Teacher_Courses table
CREATE INDEX idx_teacher_courses_teacher_id ON Teacher_Courses (Teacher_Id);
CREATE INDEX idx_teacher_courses_course_id ON Teacher_Courses (Course_Id);

-- Library tables
CREATE INDEX idx_books_title ON Books (Title);
CREATE INDEX idx_books_author ON Books (Author);
CREATE INDEX idx_books_isbn ON Books (ISBN);
CREATE INDEX idx_borrow_records_book_id ON Borrow_Records (Book_Id);
CREATE INDEX idx_borrow_records_user_id ON Borrow_Records (User_Id);

-- Other tables
CREATE INDEX idx_notifications_target_user ON Notifications (Target_User_Id);
CREATE INDEX idx_notifications_creator ON Notifications (Created_By_User_Id);
CREATE INDEX idx_events_date ON Events (Event_Date);
CREATE INDEX idx_admin_log_admin_id ON Admin_Activity_Log (Admin_User_Id);
CREATE INDEX idx_attendance_date ON Attendances (Attendance_Date);
CREATE INDEX idx_attendance_user_id ON Attendances (User_Id);

INSERT INTO Departments (Department_Name, Department_Code,Contact_Email,Phone_Number, Description) VALUES
                                                                                                       ('Computer Science & Engineering', 'CSE', 'hod.cse@university.edu', 1112223301, 'Pioneering in computation and IT.'),
                                                                                                       ('Electrical Engineering', 'EE', 'hod.ee@university.edu', 1112223302, 'Exploring frontiers of electrical systems.'),
                                                                                                       ('Mechanical Engineering', 'ME', 'hod.me@university.edu', 1112223303, 'Innovating in mechanics and materials science.'),
                                                                                                       ('Civil Engineering', 'CE', 'hod.ce@university.edu', 1112223304, 'Building the future of infrastructure.'),
                                                                                                       ('Business Administration', 'BBA', 'hod.bba@university.edu', 1112223305, 'Nurturing future business leaders.'),
                                                                                                       ('Physics', 'PHY', 'hod.phy@university.edu', 1112223306, 'Unraveling the principles of the universe.'),
                                                                                                       ('Chemistry', 'CHEM', 'hod.chem@university.edu', 1112223307, 'Investigating the properties of matter.'),
                                                                                                       ('Mathematics', 'MATH', 'hod.math@university.edu', 1112223308, 'Advancing logical and quantitative analysis.'),
                                                                                                       ('Humanities & Social Sciences', 'HSS', 'hod.hss@university.edu', 1112223309, 'Understanding human culture and society.'),
                                                                                                       ('Biotechnology', 'BIOTECH', 'hod.biotech@university.edu', 1112223310, 'Applying biology to solve real-world problems.'),
                                                                                                       ('Aerospace Engineering', 'AERO', 'hod.aero@university.edu', 1112223311, 'Designing and building aircraft and spacecraft.'),
                                                                                                       ('Information Technology', 'IT', 'hod.it@university.edu', 1112223312, 'Managing and processing information.'),
                                                                                                       ('Economics', 'ECON', 'hod.econ@university.edu', 1112223313, 'Analyzing the production and distribution of wealth.'),
                                                                                                       ('Fine Arts', 'FINART', 'hod.finart@university.edu', 1112223314, 'Cultivating creativity and artistic expression.'),
                                                                                                       ('Law', 'LAW', 'hod.law@university.edu', 1112223315, 'Providing comprehensive legal education.'),
                                                                                                       ('Pharmacy', 'PHARM', 'hod.pharm@university.edu', 1112223316, 'Science and practice of preparing and dispensing drugs.'),
                                                                                                       ('Architecture', 'ARCH', 'hod.arch@university.edu', 1112223317, 'The art and science of designing buildings.'),
                                                                                                       ('Journalism & Mass Communication', 'JMC', 'hod.jmc@university.edu', 1112223318, 'Training for media and communication industries.'),
                                                                                                       ('Hotel Management', 'HM', 'hod.hm@university.edu', 1112223319, 'Education for the hospitality industry.'),
                                                                                                       ('Environmental Science', 'EVS', 'hod.evs@university.edu', 1112223320, 'Studying the interactions between physical and biological systems.'),
                                                                                                       ('Psychology', 'PSY', 'hod.psy@university.edu', 1112223321, 'The scientific study of the human mind and its functions.'),
                                                                                                       ('Sociology', 'SOC', 'hod.soc@university.edu', 1112223322, 'The study of social behavior and societies.'),
                                                                                                       ('History', 'HIST', 'hod.hist@university.edu', 1112223323, 'The study of past events.'),
                                                                                                       ('Geography', 'GEO', 'hod.geo@university.edu', 1112223324, 'The study of the earth and its features.'),
                                                                                                       ('Political Science', 'POLSCI', 'hod.polsci@university.edu', 1112223325, 'The study of systems of governance.'),
                                                                                                       ('Foreign Languages', 'FL', 'hod.fl@university.edu', 1112223326, 'Teaching and research in world languages.'),
                                                                                                       ('Physical Education', 'PED', 'hod.ped@university.edu', 1112223327, 'Promoting health and physical fitness.'),
                                                                                                       ('Music', 'MUS', 'hod.mus@university.edu', 1112223328, 'The art of sound in time.'),
                                                                                                       ('Dance', 'DAN', 'hod.dan@university.edu', 1112223329, 'The art of movement and expression.'),
                                                                                                       ('Library Sciences', 'LIBSCI', 'hod.libsci@university.edu', 1112223330, 'Managing and disseminating information resources.');

INSERT INTO Users (User_Id,Role, Admin_Approval_Status,First_Name, Last_Name, Aadhar, Pan, Mobile, Alternate_Mobile, Email, Gender, DOB, Blood_Group, Marital_Status, Nationality, Emergency_Contact_Name, Emergency_Contact_Relationship, Emergency_Contact_Mobile, Temporary_Address, Permanent_Address, Fathers_Name, Mothers_Name) VALUES
                                                                                                                                                                                                                                                                                                                                           (1,'Student','Approved', 'Aarav', 'Sharma', 123456789001, 'ABCDE0001F', 9876543201, 8765432101, 'aarav.sharma01@example.com', 'Male', '2004-05-10', 'O+', 'Single', 'Indian', 'Ramesh Sharma', 'Father', 9876543211, '123, Hostel A, University Campus, Delhi', '456, Green Park, New Delhi', 'Ramesh Sharma', 'Sunita Sharma'),
                                                                                                                                                                                                                                                                                                                                           (2,'Student', 'Approved','Vivaan', 'Singh', 123456789002, 'ABCDE0002G', 9876543202, 8765432102, 'vivaan.singh02@example.com', 'Male', '2003-08-15', 'A+', 'Single', 'Indian', 'Suresh Singh', 'Father', 9876543212, '124, Hostel A, University Campus, Delhi', '789, Karol Bagh, New Delhi', 'Suresh Singh', 'Anita Singh'),
                                                                                                                                                                                                                                                                                                                                           (3,'Teacher','Pending', 'Aditi', 'Verma', 123456789003, 'ABCDE0003H', 9876543203, 8765432103, 'aditi.verma03@example.com', 'Female', '1985-11-20', 'B+', 'Married', 'Indian', 'Anil Verma', 'Husband', 9876543213, 'Staff Quarters, Block C, University Campus, Delhi', '101, Lajpat Nagar, New Delhi', 'Rajesh Verma', 'Priya Verma'),
                                                                                                                                                                                                                                                                                                                                           (4,'Student','Approved', 'Arjun', 'Kumar', 123456789004, 'ABCDE0004I', 9876543204, 8765432104, 'arjun.kumar04@example.com', 'Male', '2004-02-25', 'AB+', 'Single', 'Indian', 'Vijay Kumar', 'Father', 9876543214, '125, Hostel B, University Campus, Delhi', '212, Rohini, New Delhi', 'Vijay Kumar', 'Meena Kumar'),
                                                                                                                                                                                                                                                                                                                                           (5,'Staff','Pending', 'Saanvi', 'Patel', 123456789005, 'ABCDE0005J', 9876543205, 8765432105, 'saanvi.patel05@example.com', 'Female', '1990-07-30', 'O-', 'Single', 'Indian', 'Bharat Patel', 'Father', 9876543215, 'Staff Quarters, Block D, University Campus, Delhi', '333, Ahmedabad, Gujarat', 'Bharat Patel', 'Leela Patel'),
                                                                                                                                                                                                                                                                                                                                           (6,'Student', 'Approved','Ishaan', 'Gupta', 123456789006, 'ABCDE0006K', 9876543206, 8765432106,'ishaan.gupta06@example.com', 'Male', '2003-12-01', 'B-', 'Single', 'Indian', 'Manoj Gupta', 'Father', 9876543216, '126, Hostel B, University Campus, Delhi', '444, Chandigarh', 'Manoj Gupta', 'Ritu Gupta'),
                                                                                                                                                                                                                                                                                                                                           (7,'Student','Approved', 'Diya', 'Jain', 123456789007, 'ABCDE0007L', 9876543207,8765432107,'diya.jain07@example.com', 'Female', '2004-09-18', 'A-', 'Single', 'Indian', 'Prakash Jain', 'Father', 9876543217, '127, Hostel C, University Campus, Delhi', '555, Jaipur, Rajasthan', 'Prakash Jain', 'Kavita Jain'),
                                                                                                                                                                                                                                                                                                                                           (8,'Admin','Pending', 'Rohan', 'Mishra', 123456789008, 'ABCDE0008M', 9876543208, 8765432108, 'rohan.mishra08@example.com', 'Male', '1982-01-12', 'B+', 'Married', 'Indian', 'Priya Mishra', 'Wife', 9876543218, 'Staff Quarters, Block A, University Campus, Delhi', '666, Lucknow, UP', 'Girish Mishra', 'Sarita Mishra'),
                                                                                                                                                                                                                                                                                                                                           (9,'Student','Approved', 'Aanya', 'Yadav', 123456789009, 'ABCDE0009N', 9876543209, 8765432109,'aanya.yadav09@example.com', 'Female', '2003-06-22', 'O+', 'Single', 'Indian', 'Dharmendra Yadav', 'Father', 9876543219, '128, Hostel C, University Campus, Delhi', '777, Gurgaon, Haryana', 'Dharmendra Yadav', 'Poonam Yadav'),
                                                                                                                                                                                                                                                                                                                                           (10,'Staff', 'Pending','Kabir', 'Mehta', 123456789010, 'ABCDE0010P', 9876543210, 8765432110, 'kabir.mehta10@example.com', 'Male', '1995-03-05', 'A+', 'Single', 'Indian', 'Sunil Mehta', 'Father', 9876543220, 'Staff Quarters, Block B, University Campus, Delhi', '888, Mumbai, Maharashtra', 'Sunil Mehta', 'Anjali Mehta'),
                                                                                                                                                                                                                                                                                                                                           (11,'Student','Approved', 'Advik', 'Chauhan', 123456789011, 'ABCDE0011Q', 9876543211, 8765432111,'advik.chauhan11@example.com', 'Male', '2004-01-01', 'B+', 'Single', 'Indian', 'Rajesh Chauhan', 'Father', 9876543221, '201, Hostel D, University Campus, Delhi', '111, Shimla, HP', 'Rajesh Chauhan', 'Geeta Chauhan'),
                                                                                                                                                                                                                                                                                                                                           (12,'Student', 'Approved','Anika', 'Roy', 123456789012, 'ABCDE0012R', 9876543212, 8765432112,'anika.roy12@example.com', 'Female', '2003-04-14', 'O-', 'Single', 'Indian', 'Amit Roy', 'Father', 9876543222, '202, Hostel D, University Campus, Delhi', '222, Kolkata, WB', 'Amit Roy', 'Soma Roy'),
                                                                                                                                                                                                                                                                                                                                           (13,'Teacher','Pending', 'Vikram', 'Nair', 123456789013, 'ABCDE0013S', 9876543213, 8765432113, 'vikram.nair13@example.com', 'Male', '1979-08-23', 'A-', 'Married', 'Indian', 'Lakshmi Nair', 'Wife', 9876543223, 'Staff Quarters, Block E, University Campus, Delhi', '333, Kochi, Kerala', 'Gopalakrishnan Nair', 'Radha Nair'),
                                                                                                                                                                                                                                                                                                                                           (14,'Student','Approved', 'Zara', 'Khan', 123456789014, 'ABCDE0014T', 9876543214, 8765432114,'zara.khan14@example.com', 'Female', '2004-07-07', 'B-', 'Single', 'Indian', 'Imran Khan', 'Father', 9876543224, '203, Hostel E, University Campus, Delhi', '444, Hyderabad, Telangana', 'Imran Khan', 'Fatima Khan'),
                                                                                                                                                                                                                                                                                                                                           (15,'Staff','Pending', 'Reyansh', 'Joshi', 123456789015, 'ABCDE0015U', 9876543215, 8765432115, 'reyansh.joshi15@example.com', 'Male', '1998-10-19', 'AB+', 'Single', 'Indian', 'Harish Joshi', 'Father', 9876543225, 'Staff Quarters, Block F, University Campus, Delhi', '555, Dehradun, Uttarakhand', 'Harish Joshi', 'Maya Joshi'),
                                                                                                                                                                                                                                                                                                                                           (16,'Student','Approved', 'Myra', 'Reddy', 123456789016, 'ABCDE0016V', 9876543216, 8765432116,'myra.reddy16@example.com', 'Female', '2003-02-11', 'O+', 'Single', 'Indian', 'Krishna Reddy', 'Father', 9876543226, '204, Hostel E, University Campus, Delhi', '666, Bengaluru, Karnataka', 'Krishna Reddy', 'Padma Reddy'),
                                                                                                                                                                                                                                                                                                                                           (17,'Student','Approved', 'Vihaan', 'Malhotra', 123456789017, 'ABCDE0017W', 9876543217, 8765432117, 'vihaan.malhotra17@example.com', 'Male', '2004-03-30', 'A+', 'Single', 'Indian', 'Sanjay Malhotra', 'Father', 9876543227, '205, Hostel F, University Campus, Delhi', '777, Pune, Maharashtra', 'Sanjay Malhotra', 'Neetu Malhotra'),
                                                                                                                                                                                                                                                                                                                                           (18,'Teacher','Pending', 'Kiara', 'Das', 123456789018, 'ABCDE0018X', 9876543218, 8765432118, 'kiara.das18@example.com', 'Female', '1988-05-09', 'B+', 'Single', 'Indian', 'Ashok Das', 'Father', 9876543228, 'Staff Quarters, Block G, University Campus, Delhi', '888, Bhubaneswar, Odisha', 'Ashok Das', 'Rekha Das'),
                                                                                                                                                                                                                                                                                                                                           (19,'Student','Approved', 'Sai', 'Rao', 123456789019, 'ABCDE0019Y', 9876543219, 8765432119,'sai.rao19@example.com', 'Male', '2003-11-16', 'O-', 'Single', 'Indian', 'Srinivas Rao', 'Father', 9876543229, '206, Hostel F, University Campus, Delhi', '999, Chennai, Tamil Nadu', 'Srinivas Rao', 'Lalitha Rao'),
                                                                                                                                                                                                                                                                                                                                           (20,'Staff', 'Pending','Anaya', 'Biswas', 123456789020, 'ABCDE0020Z', 9876543220, 8765432120, 'anaya.biswas20@example.com', 'Female', '1992-09-02', 'A-', 'Married', 'Indian', 'Rahul Biswas', 'Husband', 9876543230, 'Staff Quarters, Block H, University Campus, Delhi', '100, Guwahati, Assam', 'Deb Biswas', 'Rina Biswas'),
                                                                                                                                                                                                                                                                                                                                           (21,'Student','Approved','''Aryan', 'Iyer', 123456789021, 'ABCDE0021A', 9876543221, 8765432121,'aryan.iyer21@example.com', 'Male', '2004-06-13', 'B-', 'Single', 'Indian', 'Ramakrishnan Iyer', 'Father', 9876543231, '301, Hostel G, University Campus, Delhi', '201, Coimbatore, Tamil Nadu', 'Ramakrishnan Iyer', 'Sita Iyer'),
                                                                                                                                                                                                                                                                                                                                           (22,'Student','Approved', 'Amaira', 'Menon', 123456789022, 'ABCDE0022B', 9876543222, 8765432122,'amaira.menon22@example.com', 'Female', '2003-10-25', 'AB-', 'Single', 'Indian', 'Jayakrishnan Menon', 'Father', 9876543232, '302, Hostel G, University Campus, Delhi', '302, Thiruvananthapuram, Kerala', 'Jayakrishnan Menon', 'Geetha Menon'),
                                                                                                                                                                                                                                                                                                                                           (23,'Accountant','Pending', 'Dhruv', 'Pandey', 123456789023, 'ABCDE0023C', 9876543223, 8765432123, 'dhruv.pandey23@example.com', 'Male', '1984-04-17', 'O+', 'Married', 'Indian', 'Shilpa Pandey', 'Wife', 9876543233, 'Staff Quarters, Block I, University Campus, Delhi', '403, Varanasi, UP', 'Surendra Pandey', 'Urmila Pandey'),
                                                                                                                                                                                                                                                                                                                                           (24,'Student','Approved', 'Eva', 'Sinha', 123456789024, 'ABCDE0024D', 9876543224, 8765432124,'eva.sinha24@example.com', 'Female', '2004-08-29', 'A+', 'Single', 'Indian', 'Alok Sinha', 'Father', 9876543234, '303, Hostel H, University Campus, Delhi', '504, Patna, Bihar', 'Alok Sinha', 'Madhu Sinha'),
                                                                                                                                                                                                                                                                                                                                           (25,'Staff', 'Pending','Yuvan', 'Chopra', 123456789025, 'ABCDE0025E', 9876543225, 8765432125, 'yuvan.chopra25@example.com', 'Male', '1996-12-31', 'B+', 'Single', 'Indian', 'Vikram Chopra', 'Father', 9876543235, 'Staff Quarters, Block J, University Campus, Delhi', '605, Jalandhar, Punjab', 'Vikram Chopra', 'Simran Chopra'),
                                                                                                                                                                                                                                                                                                                                           (26,'Student','Approved', 'Pari', 'Thakur', 123456789026, 'ABCDE0026F', 9876543226, 8765432126,'pari.thakur26@example.com', 'Female', '2003-05-21', 'O-', 'Single', 'Indian', 'Anand Thakur', 'Father', 9876543236, '304, Hostel H, University Campus, Delhi', '706, Jammu, J&K', 'Anand Thakur', 'Shanti Thakur'),
                                                                                                                                                                                                                                                                                                                                           (27,'Student','Approved', 'Ayaan', 'Bhat', 123456789027, 'ABCDE0027G', 9876543227, 8765432127, 'ayaan.bhat27@example.com', 'Male', '2004-10-03', 'A-', 'Single', 'Indian', 'Farooq Bhat', 'Father', 9876543237, '305, Hostel I, University Campus, Delhi', '807, Srinagar, J&K', 'Farooq Bhat', 'Aisha Bhat'),
                                                                                                                                                                                                                                                                                                                                           (28,'Librarian','Pending', 'Rhea', 'Goswami', 123456789028, 'ABCDE0028H', 9876543228, 8765432128, 'rhea.goswami28@example.com', 'Female', '1989-02-14', 'B-', 'Single', 'Indian', 'Bikash Goswami', 'Father', 9876543238, 'Staff Quarters, Block K, University Campus, Delhi', '908, Jorhat, Assam', 'Bikash Goswami', 'Anima Goswami'),
                                                                                                                                                                                                                                                                                                                                           (29,'Student','Approved', 'Krish', 'Dubey', 123456789029, 'ABCDE0029I', 9876543229, 8765432129,'krish.dubey29@example.com', 'Male', '2003-07-17', 'AB+', 'Single', 'Indian', 'Kamlesh Dubey', 'Father', 9876543239, '306, Hostel I, University Campus, Delhi', '109, Bhopal, MP', 'Kamlesh Dubey', 'Vimla Dubey'),
                                                                                                                                                                                                                                                                                                                                           (30,'Staff', 'Pending','Zoya', 'Aggarwal', 123456789030, 'ABCDE0030J', 9876543230, 8765432130, 'zoya.aggarwal30@example.com', 'Female', '1993-11-28', 'O+', 'Married', 'Indian', 'Nitin Aggarwal', 'Husband', 9876543240, 'Staff Quarters, Block L, University Campus, Delhi', '210, Ambala, Haryana', 'Pawan Aggarwal', 'Suman Aggarwal'),
                                                                                                                                                                                                                                                                                                                                           (31,'Student', 'Approved','Arnav', 'Saxena', 123456789031, 'ABCDE0031K', 9876543231, 8765432131,'arnav.saxena31@example.com', 'Male', '2004-12-15', 'A+', 'Single', 'Indian', 'Alok Saxena', 'Father', 9876543241, '401, Hostel J, University Campus, Delhi', '311, Bareilly, UP', 'Alok Saxena', 'Nisha Saxena'),
                                                                                                                                                                                                                                                                                                                                           (32,'Student','Approved', 'Navya', 'Trivedi', 123456789032, 'ABCDE0032L', 9876543232, 8765432132,'navya.trivedi32@example.com', 'Female', '2003-09-09', 'B+', 'Single', 'Indian', 'Ashutosh Trivedi', 'Father', 9876543242, '402, Hostel J, University Campus, Delhi', '412, Kanpur, UP', 'Ashutosh Trivedi', 'Shobha Trivedi'),
                                                                                                                                                                                                                                                                                                                                           (33,'Teacher','Pending', 'Leo', 'Fernandes', 123456789033, 'ABCDE0033M', 9876543233, 8765432133, 'leo.fernandes33@example.com', 'Male', '1981-06-06', 'O-', 'Married', 'Indian', 'Maria Fernandes', 'Wife', 9876543243, 'Staff Quarters, Block M, University Campus, Delhi', '513, Panaji, Goa', 'John Fernandes', 'Anna Fernandes'),
                                                                                                                                                                                                                                                                                                                                           (34,'Student','Approved', 'Samaira', 'Pillai', 123456789034, 'ABCDE0034N', 9876543234, 8765432134, 'samaira.pillai34@example.com', 'Female', '2004-11-11', 'A-', 'Single', 'Indian', 'Ganesh Pillai', 'Father', 9876543244, '403, Hostel K, University Campus, Delhi', '614, Madurai, Tamil Nadu', 'Ganesh Pillai', 'Meenakshi Pillai'),
                                                                                                                                                                                                                                                                                                                                           (35,'Staff','Pending', 'Veer', 'Rathore', 123456789035, 'ABCDE0035P', 9876543235, 8765432135, 'veer.rathore35@example.com', 'Male', '1997-01-26', 'B-', 'Single', 'Indian', 'Bhim Singh Rathore', 'Father', 9876543245, 'Staff Quarters, Block N, University Campus, Delhi', '715, Jodhpur, Rajasthan', 'Bhim Singh Rathore', 'Kiran Rathore'),
                                                                                                                                                                                                                                                                                                                                           (36,'Student','Approved', 'Aarohi', 'Deshpande', 123456789036, 'ABCDE0036Q', 9876543236, 8765432136, 'aarohi.deshpande36@example.com', 'Female', '2003-08-08', 'AB+', 'Single', 'Indian', 'Milind Deshpande', 'Father', 9876543246, '404, Hostel K, University Campus, Delhi', '816, Nagpur, Maharashtra', 'Milind Deshpande', 'Varsha Deshpande'),
                                                                                                                                                                                                                                                                                                                                           (37,'Student','Approved', 'Rudra', 'Patil', 123456789037, 'ABCDE0037R', 9876543237, 8765432137,'rudra.patil37@example.com', 'Male', '2004-04-04', 'O+', 'Single', 'Indian', 'Sandeep Patil', 'Father', 9876543247, '405, Hostel L, University Campus, Delhi', '917, Kolhapur, Maharashtra', 'Sandeep Patil', 'Smita Patil'),
                                                                                                                                                                                                                                                                                                                                           (38,'Teacher','Pending', 'Ira', 'Chakraborty', 123456789038, 'ABCDE0038S', 9876543238, 8765432138, 'ira.chakraborty38@example.com', 'Female', '1986-07-21', 'A+', 'Single', 'Indian', 'Tapan Chakraborty', 'Father', 9876543248, 'Staff Quarters, Block P, University Campus, Delhi', '118, Agartala, Tripura', 'Tapan Chakraborty', 'Shipra Chakraborty'),
                                                                                                                                                                                                                                                                                                                                           (39,'Student','Approved', 'Zain', 'Shaikh', 123456789039, 'ABCDE0039T', 9876543239, 8765432139,'zain.shaikh39@example.com', 'Male', '2003-03-13', 'B+', 'Single', 'Indian', 'Mustafa Shaikh', 'Father', 9876543249, '406, Hostel L, University Campus, Delhi', '219, Aurangabad, Maharashtra', 'Mustafa Shaikh', 'Zarina Shaikh'),
                                                                                                                                                                                                                                                                                                                                           (40,'Staff', 'Pending','Mahi', 'Garg', 123456789040, 'ABCDE0040U', 9876543240, 8765432140, 'mahi.garg40@example.com', 'Female', '1994-06-16', 'O-', 'Single', 'Indian', 'Mahesh Garg', 'Father', 9876543250, 'Staff Quarters, Block Q, University Campus, Delhi', '320, Ludhiana, Punjab', 'Mahesh Garg', 'Anu Garg'),
                                                                                                                                                                                                                                                                                                                                           (41,'Student','Approved', 'Dev', 'Kashyap', 123456789041, 'ABCDE0041V', 9876543241, 8765432141,'dev.kashyap41@example.com', 'Male', '2004-09-27', 'A-', 'Single', 'Indian', 'Ravi Kashyap', 'Father', 9876543251, '501, Hostel M, University Campus, Delhi', '421, Haridwar, Uttarakhand', 'Ravi Kashyap', 'Savita Kashyap'),
                                                                                                                                                                                                                                                                                                                                           (42,'Student','Approved', 'Kavya', 'Rawat', 123456789042, 'ABCDE0042W', 9876543242, 8765432142,'kavya.rawat42@example.com', 'Female', '2003-12-24', 'B-', 'Single', 'Indian', 'Gopal Singh Rawat', 'Father', 9876543252, '502, Hostel M, University Campus, Delhi', '522, Nainital, Uttarakhand', 'Gopal Singh Rawat', 'Bimla Rawat'),
                                                                                                                                                                                                                                                                                                                                           (43,'Teacher','Pending', 'Neel', 'Dutta', 123456789043, 'ABCDE0043X', 9876543243, 8765432143, 'neel.dutta43@example.com', 'Male', '1980-03-19', 'AB+', 'Married', 'Indian', 'Rupa Dutta', 'Wife', 9876543253, 'Staff Quarters, Block R, University Campus, Delhi', '623, Durgapur, WB', 'Pradip Dutta', 'Aloka Dutta'),
                                                                                                                                                                                                                                                                                                                                           (44,'Student','Approved', 'Sara', 'Pawar', 123456789044, 'ABCDE0044Y', 9876543244, 8765432144,'sara.pawar44@example.com', 'Female', '2004-01-31', 'O+', 'Single', 'Indian', 'Sharad Pawar', 'Father', 9876543254, '503, Hostel N, University Campus, Delhi', '724, Baramati, Maharashtra', 'Sharad Pawar', 'Pratibha Pawar'),
                                                                                                                                                                                                                                                                                                                                           (45,'Staff','Pending', 'Om', 'Shinde', 123456789045, 'ABCDE0045Z', 9876543245, 8765432145, 'om.shinde45@example.com', 'Male', '1999-02-28', 'A+', 'Single', 'Indian', 'Eknath Shinde', 'Father', 9876543255, 'Staff Quarters, Block S, University Campus, Delhi', '825, Thane, Maharashtra', 'Eknath Shinde', 'Lata Shinde'),
                                                                                                                                                                                                                                                                                                                                           (46,'Student','Approved', 'Avani', 'Naidu', 123456789046, 'ABCDE0046A', 9876543246, 8765432146,'avani.naidu46@example.com', 'Female', '2003-06-06', 'B+', 'Single', 'Indian', 'Chandrababu Naidu', 'Father', 9876543256, '504, Hostel N, University Campus, Delhi', '926, Amaravati, AP', 'Chandrababu Naidu', 'Bhuvaneswari Naidu'),
                                                                                                                                                                                                                                                                                                                                           (47,'Student','Approved', 'Yash', 'Tiwari', 123456789047, 'ABCDE0047B', 9876543247, 8765432147,'yash.tiwari47@example.com', 'Male', '2004-05-05', 'O-', 'Single', 'Indian', 'Manoj Tiwari', 'Father', 9876543257, '505, Hostel P, University Campus, Delhi', '127, Rewa, MP', 'Manoj Tiwari', 'Rani Tiwari'),
                                                                                                                                                                                                                                                                                                                                           (48,'Teacher','Pending', 'Siya', 'Varrier', 123456789048, 'ABCDE0048C', 9876543248, 8765432148, 'siya.varrier48@example.com', 'Female', '1987-09-19', 'A-', 'Married', 'Indian', 'Prakash Varrier', 'Husband', 9876543258, 'Staff Quarters, Block T, University Campus, Delhi', '228, Thrissur, Kerala', 'Unnikrishnan Varrier', 'Prema Varrier'),
                                                                                                                                                                                                                                                                                                                                           (49,'Student','Approved', 'Parth', 'Soni', 123456789049, 'ABCDE0049D', 9876543249, 8765432149,'parth.soni49@example.com', 'Male', '2003-01-08', 'B-', 'Single', 'Indian', 'Rakesh Soni', 'Father', 9876543259, '506, Hostel P, University Campus, Delhi', '329, Rajkot, Gujarat', 'Rakesh Soni', 'Jayshree Soni'),
                                                                                                                                                                                                                                                                                                                                           (50,'Admin', 'Approved','Manish', 'Kumar', 123456789050, 'ABCDE0050E', 9876543250, 8765432150, 'manish@gmail.com', 'Male', '1991-04-12', 'AB-', 'Married', 'Indian', 'Satpal Choudhary', 'Father', 9876543260, 'Staff Quarters, Block U, University Campus, Delhi', '430, Meerut, UP', 'Satpal Choudhary', 'Usha Choudhary');

INSERT INTO Users (User_Id, Role, First_Name, Last_Name, Aadhar, Pan, Mobile, Email, Gender, DOB, Admin_Approval_Status, Created_By_User_Id) VALUES
                                                                                                                                                 (51, 'Accountant', 'Mukesh', 'Batra', 400000000051, 'ACCNT0051Y', 9300000051, 'mukesh.b@accountant.edu', 'Male', '1984-07-01', 'Approved', 1),
                                                                                                                                                 (52, 'Accountant', 'Sheela', 'Reddy', 400000000052, 'ACCNT0052Z', 9300000052, 'sheela.r@accountant.edu', 'Female', '1989-08-02', 'Approved', 1),
                                                                                                                                                 (53, 'Accountant', 'Girish', 'Kamat', 400000000053, 'ACCNT0053A', 9300000053, 'girish.k@accountant.edu', 'Male', '1983-01-01', 'Approved', 1),
                                                                                                                                                 (54, 'Accountant', 'Vanita', 'Rao', 400000000054, 'ACCNT0054B', 9300000054, 'vanita.r@accountant.edu', 'Female', '1990-02-02', 'Approved', 1),
                                                                                                                                                 (55, 'Accountant', 'Harish', 'Pai', 400000000055, 'ACCNT0055C', 9300000055, 'harish.p@accountant.edu', 'Male', '1985-03-03', 'Approved', 1),
                                                                                                                                                 (56, 'Accountant', 'Indu', 'Shenoy', 400000000056, 'ACCNT0056D', 9300000056, 'indu.s@accountant.edu', 'Female', '1991-04-04', 'Approved', 1),
                                                                                                                                                 (57, 'Accountant', 'Jayesh', 'Bhandari', 400000000057, 'ACCNT0057E', 9300000057, 'jayesh.b@accountant.edu', 'Male', '1986-05-05', 'Approved', 1),
                                                                                                                                                 (58, 'Accountant', 'Kalpana', 'Chawla', 400000000058, 'ACCNT0058F', 9300000058, 'kalpana.c@accountant.edu', 'Female', '1992-06-06', 'Approved', 1),
                                                                                                                                                 (59, 'Accountant', 'Mahesh', 'Manjrekar', 400000000059, 'ACCNT0059G', 9300000059, 'mahesh.m@accountant.edu', 'Male', '1987-07-07', 'Approved', 1),
                                                                                                                                                 (60, 'Accountant', 'Namrata', 'Shirodkar', 400000000060, 'ACCNT0060H', 9300000060, 'namrata.s@accountant.edu', 'Female', '1993-08-08', 'Approved', 1),
                                                                                                                                                 (61, 'Accountant', 'Paresh', 'Rawal', 400000000061, 'ACCNT0061I', 9300000061, 'paresh.r@accountant.edu', 'Male', '1988-09-09', 'Approved', 1),
                                                                                                                                                 (62, 'Accountant', 'Rani', 'Mukerji', 400000000062, 'ACCNT0062J', 9300000062, 'rani.m@accountant.edu', 'Female', '1994-10-10', 'Approved', 1),
                                                                                                                                                 (63, 'Accountant', 'Satish', 'Shah', 400000000063, 'ACCNT0063K', 9300000063, 'satish.s@accountant.edu', 'Male', '1982-11-11', 'Approved', 1),
                                                                                                                                                 (64, 'Accountant', 'Tabu', 'Hashmi', 400000000064, 'ACCNT0064L', 9300000064, 'tabu.h@accountant.edu', 'Female', '1995-12-12', 'Approved', 1),
                                                                                                                                                 (65, 'Accountant', 'Utkarsh', 'Sharma', 400000000065, 'ACCNT0065M', 9300000065, 'utkarsh.s@accountant.edu', 'Male', '1981-01-13', 'Approved', 1),
                                                                                                                                                 (66, 'Accountant', 'Vidya', 'Sinha', 400000000066, 'ACCNT0066N', 9300000066, 'vidya.s@accountant.edu', 'Female', '1996-02-14', 'Approved', 1),
                                                                                                                                                 (67, 'Accountant', 'Yogesh', 'Mehta', 400000000067, 'ACCNT0067O', 9300000067, 'yogesh.m@accountant.edu', 'Male', '1980-03-15', 'Approved', 1),
                                                                                                                                                 (68, 'Librarian', 'Gyan', 'Singh', 500000000068, 'LIBR0068P', 9400000068, 'gyan.s@librarian.edu', 'Male', '1982-04-16', 'Approved', 1),
                                                                                                                                                 (69, 'Librarian', 'Vidya', 'Balan', 500000000069, 'LIBR0069Q', 9400000069, 'vidya.b@librarian.edu', 'Female', '1985-05-17', 'Approved', 1),
                                                                                                                                                 (70, 'Librarian', 'Amrish', 'Puri', 500000000070, 'LIBR0070R', 9400000070, 'amrish.p@librarian.edu', 'Male', '1981-01-01', 'Approved', 1),
                                                                                                                                                 (71, 'Librarian', 'Bindu', 'Desai', 500000000071, 'LIBR0071S', 9400000071, 'bindu.d@librarian.edu', 'Female', '1986-02-02', 'Approved', 1),
                                                                                                                                                 (72, 'Librarian', 'Chetan', 'Anand', 500000000072, 'LIBR0072T', 9400000072, 'chetan.a@librarian.edu', 'Male', '1983-03-03', 'Approved', 1),
                                                                                                                                                 (73, 'Librarian', 'Deepti', 'Naval', 500000000073, 'LIBR0073U', 9400000073, 'deepti.n@librarian.edu', 'Female', '1988-04-04', 'Approved', 1),
                                                                                                                                                 (74, 'Librarian', 'Farooq', 'Sheikh', 500000000074, 'LIBR0074V', 9400000074, 'farooq.s@librarian.edu', 'Male', '1984-05-05', 'Approved', 1),
                                                                                                                                                 (75, 'Librarian', 'Gracy', 'Singh', 500000000075, 'LIBR0075W', 9400000075, 'gracy.s@librarian.edu', 'Female', '1989-06-06', 'Approved', 1),
                                                                                                                                                 (76, 'Librarian', 'Harman', 'Baweja', 500000000076, 'LIBR0076X', 9400000076, 'harman.b@librarian.edu', 'Male', '1980-07-07', 'Approved', 1),
                                                                                                                                                 (77, 'Librarian', 'Ila', 'Arun', 500000000077, 'LIBR0077Y', 9400000077, 'ila.a@librarian.edu', 'Female', '1990-08-08', 'Approved', 1),
                                                                                                                                                 (78, 'Librarian', 'Jatin', 'Kanetkar', 500000000078, 'LIBR0078Z', 9400000078, 'jatin.k@librarian.edu', 'Male', '1987-09-09', 'Approved', 1),
                                                                                                                                                 (79, 'Librarian', 'Kirron', 'Kher', 500000000079, 'LIBR0079A', 9400000079, 'kirron.k@librarian.edu', 'Female', '1991-10-10', 'Approved', 1),
                                                                                                                                                 (80, 'Librarian', 'Lalit', 'Pandit', 500000000080, 'LIBR0080B', 9400000080, 'lalit.p@librarian.edu', 'Male', '1982-11-11', 'Approved', 1),
                                                                                                                                                 (81, 'Librarian', 'Mahima', 'Chaudhry', 500000000081, 'LIBR0081C', 9400000081, 'mahima.c@librarian.edu', 'Female', '1992-12-12', 'Approved', 1),
                                                                                                                                                 (82, 'Librarian', 'Nitin', 'Mukesh', 500000000082, 'LIBR0082D', 9400000082, 'nitin.m@librarian.edu', 'Male', '1985-01-13', 'Approved', 1),
                                                                                                                                                 (83, 'Librarian', 'Parveen', 'Babi', 500000000083, 'LIBR0083E', 9400000083, 'parveen.b@librarian.edu', 'Female', '1993-02-14', 'Approved', 1),
                                                                                                                                                 (84, 'Student', 'Aarav', 'Patil', 600000000084, 'STUD0084F', 9500000084, 'aarav.p@student.edu', 'Male', '2004-03-15', 'Approved', 1),
                                                                                                                                                 (85, 'Student', 'Anika', 'Mehta', 600000000085, 'STUD0085G', 9500000085, 'anika.m@student.edu', 'Female', '2005-04-16', 'Approved', 1),
                                                                                                                                                 (86, 'Student', 'Advik', 'Shah', 600000000086, 'STUD0086H', 9500000086, 'advik.s@student.edu', 'Male', '2004-05-17', 'Approved', 1),
                                                                                                                                                 (87, 'Student', 'Ishani', 'Joshi', 600000000087, 'STUD0087I', 9500000087, 'ishani.j@student.edu', 'Female', '2005-06-18', 'Approved', 1),
                                                                                                                                                 (88, 'Student', 'Kabir', 'Dave', 600000000088, 'STUD0088J', 9500000088, 'kabir.d@student.edu', 'Male', '2004-07-19', 'Approved', 1),
                                                                                                                                                 (89, 'Student', 'Kiara', 'Agrawal', 600000000089, 'STUD0089K', 9500000089, 'kiara.a@student.edu', 'Female', '2005-08-20', 'Approved', 1),
                                                                                                                                                 (90, 'Student', 'Reyansh', 'Gupta', 600000000090, 'STUD0090L', 9500000090, 'reyansh.g@student.edu', 'Male', '2004-09-21', 'Approved', 1),
                                                                                                                                                 (91, 'Student', 'Saanvi', 'Pawar', 600000000091, 'STUD0091M', 9500000091, 'saanvi.p@student.edu', 'Female', '2005-10-22', 'Approved', 1),
                                                                                                                                                 (92, 'Student', 'Vihaan', 'Kadam', 600000000092, 'STUD0092N', 9500000092, 'vihaan.k@student.edu', 'Male', '2004-11-23', 'Approved', 1),
                                                                                                                                                 (93, 'Student', 'Zara', 'Khan', 600000000093, 'STUD0093O', 9500000093, 'zara.k@student.edu', 'Female', '2005-12-24', 'Approved', 1),
                                                                                                                                                 (94, 'Student', 'Arjun', 'Singh', 600000000094, 'STUD0094P', 9500000094, 'arjun.s@student.edu', 'Male', '2003-01-25', 'Approved', 1),
                                                                                                                                                 (95, 'Student', 'Diya', 'Sharma', 600000000095, 'STUD0095Q', 9500000095, 'diya.s@student.edu', 'Female', '2003-02-26', 'Approved', 1),
                                                                                                                                                 (96, 'Student', 'Krishna', 'Varma', 600000000096, 'STUD0096R', 9500000096, 'krishna.v@student.edu', 'Male', '2003-03-27', 'Approved', 1),
                                                                                                                                                 (97, 'Student', 'Myra', 'Patel', 600000000097, 'STUD0097S', 9500000097, 'myra.p@student.edu', 'Female', '2003-04-28', 'Approved', 1),
                                                                                                                                                 (98, 'Student', 'Rohan', 'Kumar', 600000000098, 'STUD0098T', 9500000098, 'rohan.k@student.edu', 'Male', '2002-05-29', 'Approved', 1),
                                                                                                                                                 (99, 'Student', 'Tara', 'Jain', 600000000099, 'STUD0099U', 9500000099, 'tara.j@student.edu', 'Female', '2002-06-30', 'Approved', 1),
                                                                                                                                                 (100, 'Student', 'Zain', 'Ansari', 600000000100, 'STUD0100V', 9500000100, 'zain.a@student.edu', 'Male', '2002-07-01', 'Approved', 1);

INSERT INTO Teachers (User_Id, Registration_Number, Designation, Department_Id, Qualification, Specialisation, Employment_Type, Teaching_Experience_Years, Salary) VALUES
                                                                                                                                                                       (3, 'TCH00003', 'Assistant Professor', 1, 'M.Tech in AI', 'Machine Learning', 'Full-time', 5, 95000.00),
                                                                                                                                                                       (13, 'TCH00013', 'Associate Professor', 5, 'MBA, Ph.D.', 'Marketing', 'Full-time', 12, 145000.00),
                                                                                                                                                                       (18, 'TCH00018', 'Professor', 2, 'Ph.D. in Power Systems', 'Renewable Energy', 'Full-time', 20, 180000.00),
                                                                                                                                                                       (33, 'TCH00033', 'Assistant Professor', 8, 'M.Sc. in Applied Mathematics', 'Calculus & Analysis', 'Full-time', 8, 110000.00),
                                                                                                                                                                       (38, 'TCH00038', 'Visiting Faculty', 14, 'MFA (Master of Fine Arts)', 'Sculpture', 'Visiting', 10, 75000.00),
                                                                                                                                                                       (43, 'TCH00043', 'Assistant Professor', 9, 'M.A. in Sociology', 'Urban Sociology', 'Part-time', 4, 65000.00),
                                                                                                                                                                       (48, 'TCH00048', 'Assistant Professor', 27, 'M.P.Ed (Master of Physical Education)', 'Athletics Coaching', 'Full-time', 6, 92000.00);

INSERT INTO Staffs (User_Id, Registration_Number, Designation, Department_Id, Employment_Type, Work_Experience_Years, Salary) VALUES
                                                                                                                                  (5, 'STF00005', 'IT Support Specialist', 12, 'Full-time', 7, 68000.00),
                                                                                                                                  (10, 'STF00010', 'Admissions Clerk', 5, 'Full-time', 6, 52000.00),
                                                                                                                                  (20, 'STF00020', 'Department Coordinator', 2, 'Full-time', 9, 75000.00),
                                                                                                                                  (25, 'STF00025', 'Lab Manager', 1, 'Full-time', 11, 82000.00),
                                                                                                                                  (30, 'STF00030', 'Research Assistant', 6, 'Part-time', 3, 45000.00),
                                                                                                                                  (35, 'STF00035', 'Office Assistant', 1, 'Full-time', 4, 48000.00),
                                                                                                                                  (40, 'STF00040', 'Maintenance Supervisor', 4, 'Full-time', 15, 78000.00),
                                                                                                                                  (45, 'STF00045', 'Hostel Warden', 9, 'Full-time', 8, 65000.00),
                                                                                                                                  (51, 'STF00051', 'Finance Clerk', 5, 'Contract', 5, 55000.00);

INSERT INTO Accountants (User_Id, Qualification, Certification, Experience_Years, Designation, Department_Id, Salary) VALUES
                                                                                                                          (23, 'B.Com', 'Tally Prime Certified', 4, 'Junior Accountant', NULL, 58000.00),
                                                                                                                          (51, 'M.Com', 'Chartered Accountant (CA)', 12, 'Chief Accountant', NULL, 140000.00),
                                                                                                                          (52, 'MBA (Finance)', 'CFA Level 2', 10, 'Senior Accountant', NULL, 115000.00),
                                                                                                                          (53, 'M.Com', 'GST Practitioner', 8, 'Senior Accountant', NULL, 95000.00),
                                                                                                                          (54, 'B.Com', 'Tally ERP 9', 3, 'Junior Accountant', NULL, 55000.00),
                                                                                                                          (55, 'M.Com', 'ACCA', 9, 'Senior Accountant', NULL, 110000.00),
                                                                                                                          (56, 'B.Com', NULL, 2, 'Accounts Assistant', NULL, 48000.00),
                                                                                                                          (57, 'M.Com', NULL, 7, 'Accountant', 5, 85000.00),
                                                                                                                          (58, 'B.Com (Hons)', 'Certified Public Accountant (CPA)', 6, 'Accountant', NULL, 90000.00),
                                                                                                                          (59, 'M.Com', 'Chartered Accountant (CA)', 11, 'Accounts Manager', NULL, 130000.00),
                                                                                                                          (60, 'B.Com', 'Tally Prime Certified', 1, 'Accounts Trainee', NULL, 42000.00),
                                                                                                                          (61, 'MBA (Finance)', NULL, 8, 'Finance Analyst', 5, 98000.00),
                                                                                                                          (62, 'B.Com', NULL, 2, 'Junior Accountant', NULL, 52000.00),
                                                                                                                          (63, 'M.Com', 'GST Practitioner', 9, 'Senior Tax Accountant', NULL, 105000.00),
                                                                                                                          (64, 'B.Com', 'Tally ERP 9', 4, 'Accountant', NULL, 62000.00),
                                                                                                                          (65, 'M.Com', 'Chartered Accountant (CA)', 14, 'Internal Auditor', NULL, 155000.00),
                                                                                                                          (66, 'B.Com', NULL, 1, 'Accounts Assistant', NULL, 46000.00),
                                                                                                                          (67, 'M.Com', 'CMA', 7, 'Cost Accountant', NULL, 93000.00);


INSERT INTO Admins (User_Id, Designation, Access_Level,Department_Id) VALUES
                                                                          (8, 'Admissions Coordinator', 'Academic',12),
                                                                          (50, 'IT Systems Manager', 'Departmental',23);

INSERT INTO Librarians (User_Id, Qualification, Certification, Experience_Years, Designation, Department_Id, Salary) VALUES
                                                                                                                         (28, 'M.Lib.Sc', 'UGC-NET', 14, 'Senior Librarian', 30, 95000.00),
                                                                                                                         (68, 'M.Lib.Sc', 'Ph.D. in Library Science', 20, 'Head Librarian', 30, 115000.00),
                                                                                                                         (69, 'B.Lib.Sc', 'Digital Curation', 12, 'Digital Archivist', 30, 88000.00),
                                                                                                                         (70, 'M.Lib.Sc', NULL, 15, 'Senior Librarian', 30, 98000.00),
                                                                                                                         (71, 'B.Lib.Sc', NULL, 8, 'Assistant Librarian', 30, 62000.00),
                                                                                                                         (72, 'M.Lib.Sc', 'UGC-NET', 10, 'Librarian (Cataloging)', 30, 78000.00),
                                                                                                                         (73, 'B.Lib.Sc', NULL, 7, 'Assistant Librarian', 30, 61000.00),
                                                                                                                         (74, 'M.Lib.Sc', NULL, 11, 'Librarian (Acquisitions)', 30, 81000.00),
                                                                                                                         (75, 'B.Lib.Sc', 'Certificate in Library Automation', 6, 'Library Assistant', 30, 55000.00),
                                                                                                                         (76, 'M.Lib.Sc', NULL, 9, 'Assistant Librarian', 30, 68000.00),
                                                                                                                         (77, 'B.Lib.Sc', NULL, 5, 'Library Assistant', 30, 54000.00),
                                                                                                                         (78, 'M.Lib.Sc', 'UGC-NET', 12, 'Librarian (Reference Desk)', 30, 83000.00),
                                                                                                                         (79, 'B.Lib.Sc', NULL, 4, 'Library Assistant', 30, 52000.00),
                                                                                                                         (80, 'M.Lib.Sc', NULL, 10, 'Librarian (Periodicals)', 30, 77000.00),
                                                                                                                         (81, 'B.Lib.Sc', 'Digital Curation', 6, 'Digital Library Assistant', 30, 64000.00),
                                                                                                                         (82, 'M.Lib.Sc', 'UGC-NET', 13, 'Senior Librarian', 30, 96000.00),
                                                                                                                         (83, 'B.Lib.Sc', NULL, 3, 'Junior Library Assistant', 30, 48000.00);

INSERT INTO Students (User_Id, Registration_Number, Roll_Number, Course, Stream, Batch, Department_Id, Current_Academic_Year, Current_Semester, School_10_Name, School_10_Passing_Year, School_10_Percentage, School_12_Name, School_12_Passing_Year, School_12_Percentage) VALUES
                                                                                                                                                                                                                                                                                (1, 'STU240001', 'CSE24001', 'B.Tech', 'Computer Science', '2024-2028', 1, 2, 3, 'Delhi Public School', 2022, 95.5, 'Delhi Public School', 2024, 92.8),
                                                                                                                                                                                                                                                                                (2, 'STU240002', 'EE24002', 'B.Tech', 'Electrical Engineering', '2024-2028', 2, 2, 3, 'Kendriya Vidyalaya', 2022, 91.2, 'Kendriya Vidyalaya', 2024, 88.5),
                                                                                                                                                                                                                                                                                (4, 'STU240004', 'CE24004', 'B.Tech', 'Civil Engineering', '2024-2028', 4, 2, 3, 'Modern School', 2022, 88.0, 'Modern School', 2024, 85.3),
                                                                                                                                                                                                                                                                                (6, 'STU240006', 'ME24006', 'B.Tech', 'Mechanical Engineering', '2024-2028', 3, 2, 3, 'DAV Public School', 2022, 92.1, 'DAV Public School', 2024, 90.0),
                                                                                                                                                                                                                                                                                (7, 'STU240007', 'BBA24007', 'BBA', 'Finance', '2024-2027', 5, 2, 3, 'Amity International', 2022, 85.5, 'Amity International', 2024, 89.2),
                                                                                                                                                                                                                                                                                (9, 'STU250009', 'AERO25009', 'B.Tech', 'Aerospace Engineering', '2025-2029', 11, 1, 1, 'Ryan International', 2023, 96.0, 'Ryan International', 2025, 94.1),
                                                                                                                                                                                                                                                                                (11, 'STU250011', 'BT25011', 'B.Sc', 'Biotechnology', '2025-2028', 10, 1, 1, 'Springdales School', 2023, 93.4, 'Springdales School', 2025, 91.7),
                                                                                                                                                                                                                                                                                (12, 'STU250012', 'ARCH25012', 'B.Arch', 'Architecture', '2025-2030', 17, 1, 1, 'The Scindia School', 2023, 89.8, 'The Scindia School', 2025, 90.5),
                                                                                                                                                                                                                                                                                (14, 'STU250014', 'LAW25014', 'BA LLB', 'Law', '2025-2030', 15, 1, 1, 'Bishop Cotton School', 2023, 94.5, 'Bishop Cotton School', 2025, 93.2),
                                                                                                                                                                                                                                                                                (16, 'STU250016', 'JMC25016', 'B.A.', 'Journalism & Mass Comm', '2025-2028', 18, 1, 1, 'St. Xaviers Collegiate School', 2023, 88.9, 'St. Xaviers Collegiate School', 2025, 87.6),
                                                                                                                                                                                                                                                                                (17, 'STU230017', 'CSE23017', 'B.Tech', 'Computer Science', '2023-2027', 1, 3, 5, 'Podar International', 2021, 97.2, 'Podar International', 2023, 95.0),
                                                                                                                                                                                                                                                                                (19, 'STU230019', 'ME23019', 'B.Tech', 'Mechanical Engineering', '2023-2027', 3, 3, 5, 'Apeejay School', 2021, 90.8, 'Apeejay School', 2023, 89.1),
                                                                                                                                                                                                                                                                                (21, 'STU230021', 'IT23021', 'B.Tech', 'Information Technology', '2023-2027', 12, 3, 5, 'Birla Vidya Mandir', 2021, 94.3, 'Birla Vidya Mandir', 2023, 92.5),
                                                                                                                                                                                                                                                                                (22, 'STU230022', 'ECO23022', 'B.A.', 'Economics', '2023-2026', 13, 3, 5, 'La Martiniere College', 2021, 96.1, 'La Martiniere College', 2023, 94.8),
                                                                                                                                                                                                                                                                                (24, 'STU230024', 'PHARM23024', 'B.Pharma', 'Pharmacy', '2023-2027', 16, 3, 5, 'G. D. Goenka Public School', 2021, 92.7, 'G. D. Goenka Public School', 2023, 91.2),
                                                                                                                                                                                                                                                                                (26, 'STU220026', 'CSE22026', 'B.Tech', 'Computer Science', '2022-2026', 1, 4, 7, 'Shriram School', 2020, 98.1, 'Shriram School', 2022, 96.5),
                                                                                                                                                                                                                                                                                (27, 'STU220027', 'EE22027', 'B.Tech', 'Electrical Engineering', '2022-2026', 2, 4, 7, 'Mothers International', 2020, 94.0, 'Mothers International', 2022, 93.3),
                                                                                                                                                                                                                                                                                (29, 'STU220029', 'CE22029', 'B.Tech', 'Civil Engineering', '2022-2026', 4, 4, 7, 'Vasant Valley School', 2020, 91.5, 'Vasant Valley School', 2022, 90.1),
                                                                                                                                                                                                                                                                                (31, 'STU220031', 'ME22031', 'B.Tech', 'Mechanical Engineering', '2022-2026', 3, 4, 7, 'Sanskriti School', 2020, 93.8, 'Sanskriti School', 2022, 92.4),
                                                                                                                                                                                                                                                                                (32, 'STU220032', 'BBA22032', 'BBA', 'Marketing', '2022-2025', 5, 4, 7, 'Step by Step School', 2020, 89.9, 'Step by Step School', 2022, 91.0),
                                                                                                                                                                                                                                                                                (34, 'STU220034', 'PHY22034', 'M.Sc', 'Physics', '2022-2024', 6, 2, 3, 'St. Stephens College', 2020, 85.0, 'St. Stephens College', 2022, 88.0),
                                                                                                                                                                                                                                                                                (36, 'STU250036', 'CHEM25036', 'B.Sc', 'Chemistry', '2025-2028', 7, 1, 1, 'National Public School', 2023, 92.2, 'National Public School', 2025, 90.9),
                                                                                                                                                                                                                                                                                (37, 'STU240037', 'MATH24037', 'B.Sc', 'Mathematics', '2024-2027', 8, 2, 3, 'Bombay Scottish School', 2022, 95.8, 'Bombay Scottish School', 2024, 94.2),
                                                                                                                                                                                                                                                                                (39, 'STU230039', 'HSS23039', 'B.A.', 'History', '2023-2026', 9, 3, 5, 'Cathedral and John Connon School', 2021, 93.1, 'Cathedral and John Connon School', 2023, 91.8),
                                                                                                                                                                                                                                                                                (41, 'STU220041', 'PSY22041', 'M.A.', 'Psychology', '2022-2024', 21, 2, 3, 'Lady Shri Ram College', 2020, 86.0, 'Lady Shri Ram College', 2022, 89.5),
                                                                                                                                                                                                                                                                                (42, 'STU250042', 'SOC25042', 'B.A.', 'Sociology', '2025-2028', 22, 1, 1, 'Carmel Convent School', 2023, 89.4, 'Carmel Convent School', 2025, 88.1),
                                                                                                                                                                                                                                                                                (44, 'STU240044', 'GEO24044', 'B.A.', 'Geography', '2024-2027', 24, 2, 3, 'Welham Girls School', 2022, 90.3, 'Welham Girls School', 2024, 89.6),
                                                                                                                                                                                                                                                                                (47, 'STU230047', 'POL23047', 'B.A.', 'Political Science', '2023-2026', 25, 3, 5, 'Mayo College Girls School', 2021, 91.9, 'Mayo College Girls School', 2023, 90.4),
                                                                                                                                                                                                                                                                                (49, 'STU220049', 'FL22049', 'B.A.', 'Foreign Languages', '2022-2025', 26, 4, 7, 'Doon International School', 2020, 87.7, 'Doon International School', 2022, 86.9),
                                                                                                                                                                                                                                                                                (84, 'STU250084', 'CSE25084', 'B.Tech', 'Computer Science', '2025-2029', 1, 1, 1, 'DPS Ruby Park', 2023, 95.1, 'DPS Ruby Park', 2025, 93.5),
                                                                                                                                                                                                                                                                                (85, 'STU250085', 'EE25085', 'B.Tech', 'Electrical Engineering', '2025-2029', 2, 1, 1, 'South Point High School', 2023, 94.6, 'South Point High School', 2025, 92.9),
                                                                                                                                                                                                                                                                                (86, 'STU250086', 'ME25086', 'B.Tech', 'Mechanical Engineering', '2025-2029', 3, 1, 1, 'Heritage School', 2023, 91.8, 'Heritage School', 2025, 90.2),
                                                                                                                                                                                                                                                                                (87, 'STU250087', 'CE25087', 'B.Tech', 'Civil Engineering', '2025-2029', 4, 1, 1, 'Don Bosco School', 2023, 90.5, 'Don Bosco School', 2025, 88.8),
                                                                                                                                                                                                                                                                                (88, 'STU250088', 'BBA25088', 'BBA', 'General Management', '2025-2028', 5, 1, 1, 'Mahadevi Birla World Academy', 2023, 88.4, 'Mahadevi Birla World Academy', 2025, 89.9),
                                                                                                                                                                                                                                                                                (89, 'STU250089', 'PHY25089', 'B.Sc', 'Physics', '2025-2028', 6, 1, 1, 'St. James School', 2023, 96.2, 'St. James School', 2025, 94.7),
                                                                                                                                                                                                                                                                                (90, 'STU250090', 'CHEM25090', 'B.Sc', 'Chemistry', '2025-2028', 7, 1, 1, 'La Martiniere for Boys', 2023, 95.3, 'La Martiniere for Boys', 2025, 93.6),
                                                                                                                                                                                                                                                                                (91, 'STU250091', 'MATH25091', 'B.Sc', 'Mathematics', '2025-2028', 8, 1, 1, 'Modern High School for Girls', 2023, 97.0, 'Modern High School for Girls', 2025, 96.1),
                                                                                                                                                                                                                                                                                (92, 'STU250092', 'HSS25092', 'B.A.', 'English', '2025-2028', 9, 1, 1, 'Ashok Hall Girls School', 2023, 89.1, 'Ashok Hall Girls School', 2025, 87.5),
                                                                                                                                                                                                                                                                                (93, 'STU250093', 'BT25093', 'B.Tech', 'Biotechnology', '2025-2029', 10, 1, 1, 'Birla High School', 2023, 93.9, 'Birla High School', 2025, 92.1),
                                                                                                                                                                                                                                                                                (94, 'STU250094', 'AERO25094', 'B.Tech', 'Aerospace Engineering', '2025-2029', 11, 1, 1, 'Lakshmipat Singhania Academy', 2023, 96.5, 'Lakshmipat Singhania Academy', 2025, 95.2),
                                                                                                                                                                                                                                                                                (95, 'STU250095', 'IT25095', 'B.Tech', 'Information Technology', '2025-2029', 12, 1, 1, 'Delhi Public School, Newtown', 2023, 94.8, 'Delhi Public School, Newtown', 2025, 93.3),
                                                                                                                                                                                                                                                                                (96, 'STU250096', 'ECO25096', 'B.Sc', 'Economics', '2025-2028', 13, 1, 1, 'Scottish Church Collegiate School', 2023, 95.7, 'Scottish Church Collegiate School', 2025, 94.0),
                                                                                                                                                                                                                                                                                (97, 'STU250097', 'FA25097', 'BFA', 'Fine Arts', '2025-2029', 14, 1, 1, 'Patha Bhavan', 2023, 86.8, 'Patha Bhavan', 2025, 85.4),
                                                                                                                                                                                                                                                                                (98, 'STU250098', 'LAW25098', 'BBA LLB', 'Law', '2025-2030', 15, 1, 1, 'St. Lawrence High School', 2023, 92.5, 'St. Lawrence High School', 2025, 91.1),
                                                                                                                                                                                                                                                                                (99, 'STU250099', 'PHARM25099', 'B.Pharma', 'Pharmacy', '2025-2029', 16, 1, 1, 'Aditya Academy Senior Secondary', 2023, 91.3, 'Aditya Academy Senior Secondary', 2025, 90.6),
                                                                                                                                                                                                                                                                                (100, 'STU250100', 'ARCH25100', 'B.Arch', 'Architecture', '2025-2030', 17, 1, 1, 'Future Foundation School', 2023, 90.9, 'Future Foundation School', 2025, 89.7);

INSERT INTO Courses (Course_Code, Course_Name, Department_Id, Credits) VALUES
                                                                           ('CS101', 'Introduction to Programming', 1, 4.0),
                                                                           ('CS202', 'Data Structures & Algorithms', 1, 4.0),
                                                                           ('EE101', 'Basic Electrical Engineering', 2, 4.0),
                                                                           ('EE205', 'Digital Circuits', 2, 3.5),
                                                                           ('ME101', 'Engineering Mechanics', 3, 4.0),
                                                                           ('ME203', 'Thermodynamics', 3, 4.0),
                                                                           ('CE102', 'Surveying', 4, 3.0),
                                                                           ('CE204', 'Structural Analysis', 4, 4.0),
                                                                           ('BB101', 'Principles of Management', 5, 3.0),
                                                                           ('BB202', 'Marketing Management', 5, 3.0),
                                                                           ('PH101', 'Classical Mechanics', 6, 4.0),
                                                                           ('CH101', 'Inorganic Chemistry', 7, 4.0),
                                                                           ('MA101', 'Calculus I', 8, 4.0),
                                                                           ('MA201', 'Linear Algebra', 8, 4.0),
                                                                           ('HS101', 'Introduction to Sociology', 9, 3.0),
                                                                           ('BT201', 'Molecular Biology', 10, 4.0),
                                                                           ('AE202', 'Aerodynamics', 11, 4.0),
                                                                           ('IT301', 'Database Management Systems', 12, 4.0),
                                                                           ('EC101', 'Microeconomics', 13, 3.0),
                                                                           ('FA201', 'History of Art', 14, 3.0),
                                                                           ('LW101', 'Constitutional Law', 15, 4.0),
                                                                           ('PM201', 'Pharmacology', 16, 4.0),
                                                                           ('AR101', 'Architectural Design I', 17, 5.0),
                                                                           ('JM102', 'Reporting and Editing', 18, 3.0),
                                                                           ('HM101', 'Food Production', 19, 4.0),
                                                                           ('EV301', 'Environmental Impact Assessment', 20, 3.5),
                                                                           ('PS201', 'Cognitive Psychology', 21, 3.0),
                                                                           ('SO202', 'Classical Sociological Theory', 22, 3.0),
                                                                           ('HI101', 'History of Modern India', 23, 3.0),
                                                                           ('GE102', 'Geomorphology', 24, 4.0);

INSERT INTO Notifications (Title, Message, Target_Role, Target_User_Id, Created_By_User_Id, Expiry_Date) VALUES
                                                                                                             ('Fee Payment Reminder', 'Last date for semester fee payment is 31st July 2025. Please pay to avoid late fees.', 'Staff', 5, 3, '2025-07-31'),
                                                                                                             ('Faculty Meeting', 'A meeting for all faculty of the CSE department is scheduled for 20th July 2025.', 'Student', 6, 1, '2025-07-20'),
                                                                                                             ('Library Closure', 'The central library will be closed this Saturday for maintenance.', 'Teacher', 18, 68, '2025-07-20'),
                                                                                                             ('Mid-Term Exams Schedule', 'The schedule for the upcoming mid-term examinations has been published on the portal.',  'Teacher', 18, 2, '2025-08-15'),
                                                                                                             ('HR Policy Update', 'There has been an update to the leave policy. Please review the document on the HR portal.', 'Staff', 15, 4, '2025-08-01'),
                                                                                                             ('Accounts Department Notice', 'All staff are requested to submit their tax investment proofs by July 25th.', 'Accountant', NULL, 51, '2025-07-25'),
                                                                                                             ('New Book Arrivals', 'Over 100 new titles have been added to the library this week. Come and explore!', 'All Roles', NULL, 69, '2025-08-10'),
                                                                                                             ('System Maintenance Alert', 'The University Management System will be down for scheduled maintenance from 2 AM to 4 AM tonight.', 'All Roles', NULL, 1, '2025-07-19'),
                                                                                                             ('Welcome New Students', 'A warm welcome to all the new students of the 2025 batch.', 'Student', NULL, 2, '2025-08-30'),
                                                                                                             ('Guest Lecture on AI', 'Dr. Anil Kapoor (Prof. CSE) will deliver a guest lecture on "The Future of AI". All are invited.', 'All Roles', NULL, 17, '2025-07-28'),
                                                                                                             ('Internship Opportunity', 'A leading IT firm is offering summer internships for 3rd year CSE students.', 'Student', 94, 2, '2025-08-05'),
                                                                                                             ('Research Grant Application', 'Applications are open for the annual research grant. Faculty are encouraged to apply.', 'Teacher', 38, 1, '2025-09-01'),
                                                                                                             ('Staff Training Program', 'A mandatory training program on "Office Productivity Tools" will be held next week.', 'Staff', 40, 4, '2025-07-26'),
                                                                                                             ('Financial Year End Closing', 'The accounts department will be working late for the next few days for year-end closing.', 'Accountant', NULL, 51, '2025-07-31'),
                                                                                                             ('Library Fines', 'Please clear all outstanding library fines before the end of the semester.', 'Student', NULL, 68, '2025-08-20'),
                                                                                                             ('Campus Security Update', 'New security measures will be implemented starting August 1st. Please cooperate.', 'All Roles', NULL, 1, '2025-08-01'),
                                                                                                             ('Sports Fest "Josh 2025"', 'Registrations for the annual sports festival are now open!', 'Student', NULL, 2, '2025-09-10'),
                                                                                                             ('Tenure Track Positions', 'Applications are invited for tenure track faculty positions in the ME department.', 'Teacher', 33, 1, '2025-08-25'),
                                                                                                             ('Office Renovation', 'The administrative block will undergo renovation from Aug 1 to Aug 15.', 'Staff', 45, 1, '2025-08-15'),
                                                                                                             ('GST Filing Deadline', 'A reminder for the accounts team about the upcoming GST filing deadline.', 'Accountant', NULL, 52, '2025-07-20'),
                                                                                                             ('Overdue Book Notice', 'You have a book that is overdue. Please return it at the earliest.', 'Student', 85, 69, '2025-07-25'),
                                                                                                             ('PhD Admission Interviews', 'Interviews for PhD admissions in the Physics department are scheduled for next week.', 'Teacher', 43, 22, '2025-07-30'),
                                                                                                             ('Holiday: Independence Day', 'The university will remain closed on August 15th on account of Independence Day.', 'All Roles', NULL, 1, '2025-08-15'),
                                                                                                             ('Codefest 2025', 'The annual coding festival "Codefest" is back. Get your teams ready!', 'Student', NULL, 17, '2025-09-01'),
                                                                                                             ('Workshop on Research Methodology', 'A workshop for faculty and PhD students will be held on July 29th.', 'Teacher', 48, 18, '2025-07-29'),
                                                                                                             ('New IT Support System', 'A new ticket-based IT support system is now live.', 'All Roles', NULL, 1, '2025-08-10'),
                                                                                                             ('Cultural Night Practice', 'Practice for the upcoming cultural night will be held daily at 5 PM in the auditorium.', 'Student', NULL, 2, '2025-08-05'),
                                                                                                             ('Curriculum Review Meeting', 'A meeting to review the curriculum for the BBA program is scheduled.', 'Teacher', 25, 1, '2025-07-24'),
                                                                                                             ('ID Card Issuance', 'First-year students can collect their ID cards from the admin office.', 'Student', NULL, 2, '2025-08-01'),
                                                                                                             ('Book Donation Drive', 'Donate your old books at the library and contribute to knowledge sharing.', 'All Roles', NULL, 70, '2025-08-15');

INSERT INTO Events (Event_Name,Event_Description, Event_Type, Event_Date, Start_Time, Location, Organized_By_User_Id) VALUES
                                                                                                                          ('Convocation 2025', 'Annual convocation ceremony for the graduating batch.', 'Ceremony', '2025-08-20', '11:00:00', 'University Auditorium', 1),
                                                                                                                          ('TechFest "Innovate 2025"', 'Annual technical festival of the university.', 'Festival', '2025-09-15', '09:00:00', 'Main Campus Ground', 17),
                                                                                                                          ('Cultural Fest "AURA 2025"', 'Annual cultural festival with music, dance, and drama.', 'Festival', '2025-10-10', '10:00:00', 'University Auditorium', 2),
                                                                                                                          ('Alumni Meet 2025', 'Annual get-together for the university alumni.', 'Meetup', '2025-12-20', '18:00:00', 'Hotel Maurya', 1),
                                                                                                                          ('Workshop on Machine Learning', 'A 3-day hands-on workshop on practical machine learning.', 'Workshop', '2025-07-25', '10:00:00', 'CSE Seminar Hall', 17),
                                                                                                                          ('Blood Donation Camp', 'Donate blood and save a life.', 'Social', '2025-08-05', '09:00:00', 'Health Center', 4),
                                                                                                                          ('Marathon for a Cause', 'Run for education. A 5K marathon to raise funds for underprivileged children.', 'Charity', '2025-11-02', '06:00:00', 'University Main Gate', 2),
                                                                                                                          ('Guest Lecture on Indian Economy', 'A talk by a renowned economist.', 'Lecture', '2025-09-05', '15:00:00', 'Economics Department', 25),
                                                                                                                          ('Inter-Department Cricket Tournament', 'The annual cricket tournament is back!', 'Sports', '2025-10-25', '08:00:00', 'Sports Complex', 2),
                                                                                                                          ('Science Exhibition', 'Exhibition of innovative science projects by students.', 'Exhibition', '2025-11-14', '10:00:00', 'Physics Department', 22),
                                                                                                                          ('Hackathon 24-Hour Challenge', 'A non-stop coding challenge to build innovative solutions.', 'Competition', '2025-08-09', '12:00:00', 'IT Department', 19),
                                                                                                                          ('Orientation Program 2025', 'Welcoming the new batch of students.', 'Orientation', '2025-08-01', '09:30:00', 'University Auditorium', 2),
                                                                                                                          ('Book Fair 2025', 'A week-long book fair with publishers from across the country.', 'Fair', '2025-11-18', '11:00:00', 'Library Lawns', 68),
                                                                                                                          ('Photography Exhibition', 'Display of stunning photographs captured by the university photography club.', 'Exhibition', '2025-09-20', '10:00:00', 'Fine Arts Gallery', 20),
                                                                                                                          ('Film Festival', 'Screening of classic and contemporary cinema.', 'Festival', '2025-10-01', '14:00:00', 'JMC Department', 24),
                                                                                                                          ('Career Counseling Fair', 'Meet with representatives from top companies.', 'Fair', '2025-09-12', '10:00:00', 'Main Campus Ground', 4),
                                                                                                                          ('Yoga and Meditation Camp', 'A 5-day camp to promote mental well-being.', 'Workshop', '2025-08-18', '06:00:00', 'Yoga Hall', 2),
                                                                                                                          ('Startup Pitching Competition', 'Pitch your startup idea and win seed funding.', 'Competition', '2025-11-08', '10:00:00', 'BBA Department', 25),
                                                                                                                          ('International Conference on Climate Change', 'A conference with speakers from around the globe.', 'Conference', '2025-12-05', '09:00:00', 'Main Seminar Hall', 28),
                                                                                                                          ('Theatre Fest', 'A week of plays and performances by the drama club.', 'Festival', '2025-11-22', '18:00:00', 'University Auditorium', 2),
                                                                                                                          ('Poetry Slam', 'An evening of powerful spoken word poetry.', 'Competition', '2025-09-27', '17:00:00', 'HSS Department', 21),
                                                                                                                          ('Debate Competition: The Big Fight', 'Inter-university debate on current affairs.', 'Competition', '2025-10-18', '10:00:00', 'Law Department', 23),
                                                                                                                          ('Musical Night with Local Bands', 'An evening of live music and entertainment.', 'Concert', '2025-11-29', '19:00:00', 'Open Air Theatre', 2),
                                                                                                                          ('Food Festival', 'A culinary journey with food stalls from different states.', 'Festival', '2025-10-12', '12:00:00', 'Main Campus Ground', 2),
                                                                                                                          ('Robotics Workshop', 'Build and program your own robot.', 'Workshop', '2025-08-23', '10:00:00', 'ME Department', 19),
                                                                                                                          ('Plantation Drive', 'Let\'s make our campus greener.', 'Social', '2025-08-22', '08:00:00', 'Campus Lawns', 4),
		('Civil Engineering Expo', 'Showcase of the latest trends and projects in civil engineering.', 'Exhibition', '2025-10-04', '10:00:00', 'CE Department', 20),
		('Faculty Development Program', 'A program on innovative teaching methodologies.', 'Workshop', '2025-12-15', '09:00:00', 'Admin Block', 1),
		('NSS Special Camp', 'A 7-day National Service Scheme camp in a nearby village.', 'Camp', '2025-11-15', '09:00:00', 'Rural Outreach Center', 4),
		('Farewell Party', 'A farewell for the final year students.', 'Party', '2026-04-25', '19:00:00', 'University Auditorium', 2);

INSERT INTO Authentication (User_Id, UserName, Password_Hash, Salt, Last_Login, Lockout_Until) VALUES
        (1, 'aarav.sharma01', 'Aarav@2004', NULL, NULL, NULL),
        (2, 'vivaan.singh02', 'Vivaan@2003', NULL, NULL, NULL),
        (3, 'aditi.verma03', 'Aditi@1985', NULL, NULL, NULL),
        (4, 'arjun.kumar04', 'Arjun@2004', NULL, NULL, NULL),
        (5, 'saanvi.patel05', 'Saanvi@1990', NULL, NULL, NULL),
        (6, 'ishaan.gupta06', 'Ishaan@2003', NULL, NULL, NULL),
        (7, 'diya.jain07', 'Diya@2004', NULL, NULL, NULL),
        (8, 'rohan.mishra08', 'Rohan@1982', NULL, NULL, NULL),
        (9, 'aanya.yadav09', 'Aanya@2003', NULL, NULL, NULL),
        (10, 'kabir.mehta10', 'Kabir@1995', NULL, NULL, NULL),
        (11, 'advik.chauhan11', 'Advik@2004', NULL, NULL, NULL),
        (12, 'anika.roy12', 'Anika@2003', NULL, NULL, NULL),
        (13, 'vikram.nair13', 'Vikram@1979', NULL, NULL, NULL),
        (14, 'zara.khan14', 'Zara@2004', NULL, NULL, NULL),
        (15, 'reyansh.joshi15', 'Reyansh@1998', NULL, NULL, NULL),
        (16, 'myra.reddy16', 'Myra@2003', NULL, NULL, NULL),
        (17, 'vihaan.malhotra17', 'Vihaan@2004', NULL, NULL, NULL),
        (18, 'kiara.das18', 'Kiara@1988', NULL, NULL, NULL),
        (19, 'sai.rao19', 'Sai@2003', NULL, NULL, NULL),
        (20, 'anaya.biswas20', 'Anaya@1992', NULL, NULL, NULL),
        (21, 'aryan.iyer21', 'Aryan@2004', NULL, NULL, NULL),
        (22, 'amaira.menon22', 'Amaira@2003', NULL, NULL, NULL),
        (23, 'dhruv.pandey23', 'Dhruv@1984', NULL, NULL, NULL),
        (24, 'eva.sinha24', 'Eva@2004', NULL, NULL, NULL),
        (25, 'yuvan.chopra25', 'Yuvan@1996', NULL, NULL, NULL),
        (26, 'pari.thakur26', 'Pari@2003', NULL, NULL, NULL),
        (27, 'ayaan.bhat27', 'Ayaan@2004', NULL, NULL, NULL),
        (28, 'rhea.goswami28', 'Rhea@1989', NULL, NULL, NULL),
        (29, 'krish.dubey29', 'Krish@2003', NULL, NULL, NULL),
        (30, 'zoya.aggarwal30', 'Zoya@1993', NULL, NULL, NULL),
        (31, 'arnav.saxena31', 'Arnav@2004', NULL, NULL, NULL),
        (32, 'navya.trivedi32', 'Navya@2003', NULL, NULL, NULL),
        (33, 'leo.fernandes33', 'Leo@1981', NULL, NULL, NULL),
        (34, 'samaira.pillai34', 'Samaira@2004', NULL, NULL, NULL),
        (35, 'veer.rathore35', 'Veer@1997', NULL, NULL, NULL),
        (36, 'aarohi.deshpande36', 'Aarohi@2003', NULL, NULL, NULL),
        (37, 'rudra.patil37', 'Rudra@2004', NULL, NULL, NULL),
        (38, 'ira.chakraborty38', 'Ira@1986', NULL, NULL, NULL),
        (39, 'zain.shaikh39', 'Zain@2003', NULL, NULL, NULL),
        (40, 'mahi.garg40', 'Mahi@1994', NULL, NULL, NULL),
        (41, 'dev.kashyap41', 'Dev@2004', NULL, NULL, NULL),
        (42, 'kavya.rawat42', 'Kavya@2003', NULL, NULL, NULL),
        (43, 'neel.dutta43', 'Neel@1980', NULL, NULL, NULL),
        (44, 'sara.pawar44', 'Sara@2004', NULL, NULL, NULL),
        (45, 'om.shinde45', 'Om@1999', NULL, NULL, NULL),
        (46, 'avani.naidu46', 'Avani@2003', NULL, NULL, NULL),
        (47, 'yash.tiwari47', 'Yash@2004', NULL, NULL, NULL),
        (48, 'siya.varrier48', 'Siya@1987', NULL, NULL, NULL),
        (49, 'parth.soni49', 'Parth@2003', NULL, NULL, NULL),
        (50, 'manish', 'Manish', NULL, NULL, NULL),
		(51, 'isha.bajwa51', 'Isha@2003', NULL, NULL, NULL),
		(52, 'arav.chandel52', 'Arav@1990', NULL, NULL, NULL),
		(53, 'meera.seth53', 'Meera@2004', NULL, NULL, NULL),
		(54, 'lakshya.kapoor54', 'Lakshya@1998', NULL, NULL, NULL),
		(55, 'priya.nanda55', 'Priya@1986', NULL, NULL, NULL),
		(56, 'daksh.rao56', 'Daksh@2003', NULL, NULL, NULL),
		(57, 'shruti.gill57', 'Shruti@1992', NULL, NULL, NULL),
		(58, 'karan.verma58', 'Karan@2004', NULL, NULL, NULL),
		(59, 'tanya.shah59', 'Tanya@1988', NULL, NULL, NULL),
		(60, 'harsh.banerjee60', 'Harsh@2003', NULL, NULL, NULL),
		(61, 'sana.puri61', 'Sana@1991', NULL, NULL, NULL),
		(62, 'raj.sharma62', 'Raj@2004', NULL, NULL, NULL),
		(63, 'nitya.dubey63', 'Nitya@2003', NULL, NULL, NULL),
		(64, 'arjun.basu64', 'Arjun@1987', NULL, NULL, NULL),
		(65, 'riya.rana65', 'Riya@2004', NULL, NULL, NULL),
		(66, 'yuvraj.singh66', 'Yuvraj@2003', NULL, NULL, NULL),
		(67, 'kriti.das67', 'Kriti@1985', NULL, NULL, NULL),
		(68, 'anirudh.iyer68', 'Anirudh@2004', NULL, NULL, NULL),
		(69, 'isha.joseph69', 'Isha@2003', NULL, NULL, NULL),
		(70, 'kabir.raja70', 'Kabir@1990', NULL, NULL, NULL),
		(71, 'ananya.gupta71', 'Ananya@2004', NULL, NULL, NULL),
		(72, 'samar.jain72', 'Samar@1995', NULL, NULL, NULL),
		(73, 'rihaan.patel73', 'Rihaan@2003', NULL, NULL, NULL),
		(74, 'vaidehi.goswami74', 'Vaidehi@1993', NULL, NULL, NULL),
		(75, 'aryan.kulkarni75', 'Aryan@2004', NULL, NULL, NULL),
		(76, 'mehul.agarwal76', 'Mehul@1996', NULL, NULL, NULL),
		(77, 'shreya.chopra77', 'Shreya@1989', NULL, NULL, NULL),
		(78, 'tanmay.singhania78', 'Tanmay@2003', NULL, NULL, NULL),
		(79, 'lavanya.joshi79', 'Lavanya@2004', NULL, NULL, NULL),
		(80, 'aditya.pandit80', 'Aditya@1984', NULL, NULL, NULL),
		(81, 'naina.khan81', 'Naina@1991', NULL, NULL, NULL),
		(82, 'atharv.yadav82', 'Atharv@2003', NULL, NULL, NULL),
		(83, 'muskan.kaur83', 'Muskan@2004', NULL, NULL, NULL),
		(84, 'soham.naik84', 'Soham@1997', NULL, NULL, NULL),
		(85, 'vani.dhawan85', 'Vani@2003', NULL, NULL, NULL),
		(86, 'tushar.rane86', 'Tushar@2004', NULL, NULL, NULL),
		(87, 'srishti.singh87', 'Srishti@1988', NULL, NULL, NULL),
		(88, 'veeran.giri88', 'Veeran@1990', NULL, NULL, NULL),
		(89, 'aashi.kapadia89', 'Aashi@2003', NULL, NULL, NULL),
		(90, 'rehan.nayak90', 'Rehan@2004', NULL, NULL, NULL),
		(91, 'amrita.dhillon91', 'Amrita@1992', NULL, NULL, NULL),
		(92, 'ranveer.bhatt92', 'Ranveer@2003', NULL, NULL, NULL),
		(93, 'niharika.ray93', 'Niharika@1986', NULL, NULL, NULL),
		(94, 'aravind.raju94', 'Aravind@2004', NULL, NULL, NULL),
		(95, 'pooja.bansal95', 'Pooja@2003', NULL, NULL, NULL),
		(96, 'aayush.mahajan96', 'Aayush@1994', NULL, NULL, NULL),
		(97, 'kanika.chauhan97', 'Kanika@2004', NULL, NULL, NULL),
		(98, 'harshit.goel98', 'Harshit@2003', NULL, NULL, NULL),
		(99, 'bhavya.sehgal99', 'Bhavya@1999', NULL, NULL, NULL),
		(100, 'manvi.saxena100', 'Manvi@2004', NULL, NULL, NULL);



INSERT INTO Notifications (Title, Message, Target_Role, Target_User_Id, Created_By_User_Id, Expiry_Date)VALUES
		('System Maintenance', 'Scheduled system maintenance on 5 th July from 10 PM to 12 AM.', 'All Roles', 50, 50, '2025-07-05'),
		('Library Notice', 'All students are requested to return overdue books before 10th July.', 'Student', 1, 50, '2025-07-10'),
		('Fee Reminder', 'Last date to pay semester fee is 15th July 2025.', 'Student', 50, 6, '2025-07-15'),
		('Staff Meeting', 'A general staff meeting is scheduled on 6th July at 11 AM in the seminar hall.', 'Staff', 8, 3, '2025-07-06'),
		('Performance Review', 'Annual teacher performance reviews begin from 10th July.', 'Teacher', 9, 2, '2025-07-20'),
		('Welcome Note', 'Welcome to the new academic year 2025-26. Lets make it productive!', 'All Roles', 50, 1, '2025-08-01'),
		('Transport ID Update', 'Students must update their transport ID by 12th July.', 'Student', 2, 7, '2025-07-12'),
		('Library Downtime', 'Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.Library system will be offline for maintenance on 8th July.', 'Librarian', 15, 1, '2025-07-08'),
		('Policy Update', 'New leave policy effective from 1st August. Check HR portal.', 'Admin', 11, 3, '2025-08-01'),
		('Account Audit', 'Monthly audit of finance department starts on 10th July.', 'Accountant', 48, 6, '2025-07-10'),
		('Fee Payment Reminder', 'Last date for semester fee payment is 31st July 2025. Please pay to avoid late fees.', 'Student', 2, 3, '2025-07-31'),
		('Faculty Meeting', 'A meeting for all faculty of the CSE department is scheduled for 20th July 2025.', 'Teacher', 3, 1, '2025-07-20'),
		('Library Closure', 'The central library will be closed this Saturday for maintenance.', 'All Roles', 1, 68, '2025-07-20'),
		('Mid-Term Exams Schedule', 'The schedule for the upcoming mid-term examinations has been published on the portal.', 'Student', 11, 2, '2025-08-15'),
		('HR Policy Update', 'There has been an update to the leave policy. Please review the document on the HR portal.', 'Staff', 14, 4, '2025-08-01'),
		('Accounts Department Notice', 'All staff are requested to submit their tax investment proofs by July 25th.', 'Accountant', 78, 51, '2025-07-25'),
		('New Book Arrivals', 'Over 100 new titles have been added to the library this week. Come and explore!', 'All Roles', 78, 69, '2025-08-10'),
		('System Maintenance Alert', 'The University Management System will be down for scheduled maintenance from 2 AM to 4 AM tonight.', 'All Roles', 12, 1, '2025-07-19'),
		('Welcome New Students', 'A warm welcome to all the new students of the 2025 batch.', 'Student', 5, 2, '2025-08-30'),
		('Guest Lecture on AI', 'Dr. Anil Kapoor (Prof. CSE) will deliver a guest lecture on "The Future of AI". All are invited.', 'All Roles', 5, 17, '2025-07-28'),
		('Internship Opportunity', 'A leading IT firm is offering summer internships for 3rd year CSE students.', 'Student', 94, 2, '2025-08-05'),
		('Research Grant Application', 'Applications are open for the annual research grant. Faculty are encouraged to apply.', 'Teacher', 8, 1, '2025-09-01'),
		('Staff Training Program', 'A mandatory training program on "Office Productivity Tools" will be held next week.', 'Staff', 8, 4, '2025-07-26'),
		('Financial Year End Closing', 'The accounts department will be working late for the next few days for year-end closing.', 'Accountant', 8, 51, '2025-07-31'),
		('Library Fines', 'Please clear all outstanding library fines before the end of the semester.', 'Student', 9, 68, '2025-08-20'),
		('Campus Security Update', 'New security measures will be implemented starting August 1st. Please cooperate.', 'All Roles', 7, 1, '2025-08-01'),
		('Sports Fest "Josh 2025"', 'Registrations for the annual sports festival are now open!', 'Student', 3, 2, '2025-09-10'),
		('Tenure Track Positions', 'Applications are invited for tenure track faculty positions in the ME department.', 'Teacher', 1, 1, '2025-08-25'),
		('Office Renovation', 'The administrative block will undergo renovation from Aug 1 to Aug 15.', 'Staff', 4, 1, '2025-08-15'),
		('GST Filing Deadline', 'A reminder for the accounts team about the upcoming GST filing deadline.', 'Accountant', 6, 52, '2025-07-20'),
		('Overdue Book Notice', 'You have a book that is overdue. Please return it at the earliest.', 'Student', 85, 69, '2025-07-25'),
		('PhD Admission Interviews', 'Interviews for PhD admissions in the Physics department are scheduled for next week.', 'Teacher', 4, 22, '2025-07-30'),
		('Holiday: Independence Day', 'The university will remain closed on August 15th on account of Independence Day.', 'All Roles', 6, 1, '2025-08-15'),
		('Codefest 2025', 'The annual coding festival "Codefest" is back. Get your teams ready!', 'Student', 5, 17, '2025-09-01'),
		('Workshop on Research Methodology', 'A workshop for faculty and PhD students will be held on July 29th.', 'Teacher', 2, 18, '2025-07-29'),
		('New IT Support System', 'A new ticket-based IT support system is now live.', 'All Roles', 4, 1, '2025-08-10'),
		('Cultural Night Practice', 'Practice for the upcoming cultural night will be held daily at 5 PM in the auditorium.', 'Student', 11, 2, '2025-08-05'),
		('Curriculum Review Meeting', 'A meeting to review the curriculum for the BBA program is scheduled.', 'Teacher', 25, 1, '2025-07-24'),
		('ID Card Issuance', 'First-year students can collect their ID cards from the admin office.', 'Student', 15, 2, '2025-08-01'),
		('Book Donation Drive', 'Donate your old books at the library and contribute to knowledge sharing.', 'All Roles', 22, 70, '2025-08-15');


INSERT INTO Books (Book_Id, Title, Author, ISBN, Publisher, Publication_Year, Genre, Total_Copies, Available_Copies, Location_Shelf, Description)
VALUES
		(1, 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Charles Scribner''s Sons', 1925, 'Classic', 5, 3, 'A1-01', 'A novel about the American dream.'),
		(3, 'To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'J. B. Lippincott & Co.', 1960, 'Classic', 4, 2, 'A1-02', 'A story of racial injustice and childhood innocence.'),
		(5, '1984', 'George Orwell', '9780451524935', 'Secker & Warburg', 1949, 'Dystopian', 6, 4, 'B2-05', 'A story about totalitarianism and surveillance.'),
		(7, 'The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 'Little, Brown and Company', 1951, 'Fiction', 3, 1, 'C3-07', 'A story about teenage angst and alienation.'),
		(8, 'Brave New World', 'Aldous Huxley', '9780060850524', 'Chatto & Windus', 1932, 'Dystopian', 5, 2, 'B2-06', 'A dystopian novel about a futuristic society.'),
		(11, 'The Hobbit', 'J.R.R. Tolkien', '9780618260300', 'George Allen & Unwin', 1937, 'Fantasy', 8, 5, 'D4-11', 'A fantasy novel and prequel to The Lord of the Rings.'),
		(12, 'Pride and Prejudice', 'Jane Austen', '9780141439518', 'T. Egerton, Whitehall', 1813, 'Romance', 7, 5, 'E5-12', 'A romantic novel of manners.'),
		(15, 'The Lord of the Rings', 'J.R.R. Tolkien', '9780618640157', 'Allen & Unwin', 1954, 'Fantasy', 4, 1, 'D4-15', 'An epic high-fantasy novel.'),
		(18, 'Fahrenheit 451', 'Ray Bradbury', '9781451673319', 'Ballantine Books', 1953, 'Dystopian', 6, 3, 'B2-18', 'A novel about a future where books are banned.'),
		(19, 'Jane Eyre', 'Charlotte Brontë', '9780141441146', 'Smith, Elder & Co.', 1847, 'Gothic', 5, 2, 'E5-19', 'A novel that follows the experiences of its eponymous heroine.'),
		(22, 'Animal Farm', 'George Orwell', '9780451526342', 'Secker and Warburg', 1945, 'Political Satire', 8, 6, 'B2-22', 'An allegorical novella about the Russian Revolution.'),
		(25, 'The Chronicles of Narnia', 'C.S. Lewis', '9780066238500', 'Geoffrey Bles', 1950, 'Fantasy', 5, 2, 'D4-25', 'A series of seven high fantasy novels.'),
		(28, 'Moby-Dick', 'Herman Melville', '9780142437247', 'Harper & Brothers', 1851, 'Adventure', 3, 1, 'A1-28', 'The narrative of Captain Ahab''s obsessive quest to seek revenge on Moby Dick.'),
		(30, 'War and Peace', 'Leo Tolstoy', '9781400079988', 'The Russian Messenger', 1869, 'Historical', 2, 1, 'F6-30', 'A novel that chronicles the history of the French invasion of Russia.'),
		(33, 'The Odyssey', 'Homer', '9780140268867', 'Ancient Greek', -800, 'Epic Poetry', 4, 3, 'F6-33', 'An ancient Greek epic poem.'),
		(38, 'Don Quixote', 'Miguel de Cervantes', '9780060934347', 'Francisco de Robles', 1605, 'Novel', 3, 1, 'F6-38', 'A Spanish epic novel.'),
		(40, 'One Hundred Years of Solitude', 'Gabriel García Márquez', '9780060883287', 'Editorial Sudamericana', 1967, 'Magic Realism', 4, 2, 'G7-40', 'A landmark 1967 novel by Colombian author Gabriel García Márquez.'),
		(42, 'The Alchemist', 'Paulo Coelho', '9780061122415', 'HarperCollins', 1988, 'Adventure', 10, 8, 'G7-42', 'An allegorical novel about a young Andalusian shepherd in his journey to the pyramids of Egypt.'),
		(45, 'The Diary of a Young Girl', 'Anne Frank', '9780553296983', 'Contact Publishing', 1947, 'Autobiography', 6, 4, 'H8-45', 'A book of the writings from the Dutch-language diary kept by Anne Frank.'),
		(50, 'Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', '9780062316097', 'Dvir Publishing House', 2011, 'Non-fiction', 7, 5, 'H8-50', 'A book by Yuval Noah Harari, first published in Hebrew in Israel in 2011.');

-- Step 3: Insert the sample data for the Borrow_Records table using the correct Librarian IDs.
INSERT INTO Borrow_Records (Book_Id, User_Id, Borrow_Date, Due_Date, Return_Date, Fine_Amount, Status, Librarian_User_Id, Remarks)
VALUES
		(5, 1, '2025-06-01', '2025-06-15', '2025-06-10', 0.00, 'Returned', 70, 'Returned in good condition.'),
		(12, 2, '2025-07-01', '2025-07-15', NULL, 4.50, 'Overdue', 71, 'First reminder sent on 2025-07-18.'),
		(25, 3, '2025-07-15', '2025-07-29', NULL, 0.00, 'Borrowed', 71, NULL),
		(30, 1, '2025-05-10', '2025-05-24', NULL, 25.00, 'Lost', 71, 'User reported book as lost. Replacement fee applied.'),
		(8, 2, '2025-04-01', '2025-04-15', '2025-04-20', 2.50, 'Returned', 71, 'Returned 5 days late. Fine paid.'),
		(18, 3, '2025-06-20', '2025-07-04', '2025-07-04', 0.00, 'Returned', 72, NULL),
		(42, 1, '2025-07-20', '2025-08-03', NULL, 0.00, 'Borrowed', 72, NULL),
		(3, 2, '2025-05-15', '2025-05-29', '2025-05-28', 0.00, 'Returned', 72, NULL),
		(15, 3, '2025-06-25', '2025-07-09', NULL, 7.50, 'Overdue', 73, 'Second reminder sent.'),
		(22, 1, '2025-03-10', '2025-03-24', '2025-03-15', 0.00, 'Returned', 73, NULL),
		(33, 2, '2025-07-11', '2025-07-25', NULL, 0.00, 'Borrowed', 73, NULL),
		(45, 3, '2025-02-01', '2025-02-15', '2025-02-25', 5.00, 'Returned', 73, 'Returned 10 days late.'),
		(1, 1, '2025-01-05', '2025-01-19', '2025-01-19', 0.00, 'Returned', 73, NULL),
		(50, 2, '2025-06-05', '2025-06-19', NULL, 30.00, 'Lost', 73, 'Replacement fee charged.'),
		(7, 3, '2025-07-18', '2025-08-01', NULL, 0.00, 'Borrowed', 73, NULL),
		(11, 1, '2024-12-10', '2024-12-24', '2024-12-26', 1.00, 'Returned', 73, 'Returned 2 days late.'),
		(28, 2, '2025-07-02', '2025-07-16', '2025-07-15', 0.00, 'Returned', 73, NULL),
		(38, 3, '2025-05-20', '2025-06-03', NULL, 25.50, 'Overdue', 73, 'Final notice sent.'),
		(19, 1, '2025-07-22', '2025-08-05', NULL, 0.00, 'Borrowed', 73, NULL),
		(40, 2, '2025-04-10', '2025-04-24', '2025-04-20', 0.00, 'Returned', 73, 'Returned with minor wear.');

-- Step 1: Insert sample data into the 'Fee_Types' table to satisfy foreign key constraints.
INSERT INTO Fee_Types (Departments, Fee_Type_Name, Description, Default_Amount, Is_Recurring) VALUES
		( 'B.Tech','Tuition Fee', 'Main academic tuition fee for the semester.', 85000.00, TRUE),
		( 'B.Tech', 'Hostel Fee', 'Accommodation charges for the semester.', 75000.00, TRUE),
		( 'B.Tech','Examination Fee', 'Fee for appearing in end-of-semester exams.', 9500.00, TRUE),
		( 'B.Tech', 'Library Fee', 'Annual library usage and resource fee.', 5000.00, FALSE),
		( 'B.Tech', 'Transport Fee', 'Fee for using university bus services.', 58000.00, TRUE),
		( 'B.Tech','Late Fee Fine', 'Fine applied for late payment of fees.', 2000.00, FALSE),

		( 'B.C.A','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		('B.C.A', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'B.C.A','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'B.C.A', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'B.C.A', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'B.C.A','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

		( 'Diploma','Tuition Fee', 'Main academic tuition fee for the semester.', 55000.00, TRUE),
		( 'Diploma', 'Hostel Fee', 'Accommodation charges for the semester.', 45000.00, TRUE),
		( 'Diploma','Examination Fee', 'Fee for appearing in end-of-semester exams.', 3500.00, TRUE),
		( 'Diploma', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'Diploma', 'Transport Fee', 'Fee for using university bus services.', 8000.00, TRUE),
		( 'Diploma','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for B.A.
		( 'B.A.','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'B.A.', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'B.A.','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'B.A.', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'B.A.', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'B.A.','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for B.Pharma
		( 'B.Pharma','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'B.Pharma', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'B.Pharma','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'B.Pharma', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'B.Pharma', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'B.Pharma','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for M.Sc
		( 'M.Sc','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'M.Sc', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'M.Sc','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'M.Sc', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'M.Sc', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'M.Sc','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for M.A.
		( 'M.A.','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'M.A.', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'M.A.','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'M.A.', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'M.A.', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'M.A.','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for BFA
		( 'BFA','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'BFA', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'BFA','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'BFA', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'BFA', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'BFA','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for BBA LLB
		( 'BBA LLB','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
        ( 'BBA LLB', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'BBA LLB','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'BBA LLB', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'BBA LLB', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'BBA LLB','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for BA LLB
		( 'BA LLB','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'BA LLB', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'BA LLB','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'BA LLB', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'BA LLB', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'BA LLB','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for B.Arch
		( 'B.Arch','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'B.Arch', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'B.Arch','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'B.Arch', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'B.Arch', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'B.Arch','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for BBA
		( 'BBA','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'BBA', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'BBA','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'BBA', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'BBA', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'BBA','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE),

-- Fees for B.Sc
		( 'B.Sc','Tuition Fee', 'Main academic tuition fee for the semester.', 75000.00, TRUE),
		( 'B.Sc', 'Hostel Fee', 'Accommodation charges for the semester.', 65000.00, TRUE),
		( 'B.Sc','Examination Fee', 'Fee for appearing in end-of-semester exams.', 5500.00, TRUE),
		( 'B.Sc', 'Library Fee', 'Annual library usage and resource fee.', 1500.00, FALSE),
		( 'B.Sc', 'Transport Fee', 'Fee for using university bus services.', 48000.00, TRUE),
		( 'B.Sc','Late Fee Fine', 'Fine applied for late payment of fees.', 1500.00, FALSE);


 INSERT INTO Student_Fees (User_Id, Fees_Paid_By_User_Id, Fee_Type_Id, Academic_Year, Semester, Fee_Month, Amount_Due, Amount_Paid, Due_Date,  Payment_Date, Payment_Status, Transaction_Id, Receipt_Number, Remarks) VALUES
		(4,62, 3, 2024, '3', 2, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728001', 'REC001', NULL),
		(19,56, 1, 2024, '2', 7, 500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(11,23, 2, 2024, '2', 3, 8000.00, 2932.71, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728003', 'REC003', 'Partial payment received.'),
		(21,23, 1, 2024, '3', 4, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728004', 'REC004', 'Transport fee.'),
		(19,54, 1, 2025, '3', 11, 8000.00, 8000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728005', 'REC005', 'Full payment received.'),
		(22,66, 2, 2024, '3', 7, 2500.00, 1172.42, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728006', 'REC006', 'Partial payment received.'),
		(7,56, 5, 2025, '1', 1, 2500.00, 1443.08, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728007', 'REC007', 'Partial payment received.'),
		(4,67, 5, 2024, '2', 5, 2500.00, 1746.66, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728008', 'REC008', 'Partial payment received.'),
		(19,23, 4, 2025, '1', 5, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728009', 'REC009', 'Full payment received.'),
		(12,52, 3, 2024, '2', 7, 15000.00, 15000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728010', 'REC010', 'Transport fee.'),
		(16,65, 4, 2024, '4', 10, 2500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(1,57, 5, 2024, '5', 9, 15000.00, 6153.10, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728012', 'REC012', 'Partial payment received.'),
		(12,51, 1, 2024, '6', 6, 15000.00, 8568.52, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728013', 'REC013', 'Partial payment received.'),
		(9,54, 4, 2024, '7', 7, 8000.00, 8000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728014', 'REC014', NULL),
		(12,57, 4, 2025, '8', 2, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728015', 'REC015', 'Includes late fee.'),
		(2,52, 5, 2025, '7', 8, 8000.00, 8000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728016', 'REC016', 'Includes late fee.'),
		(1,65, 1, 2025, '8', 11, 37500.00, 20474.11, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728017', 'REC017', 'Partial payment received.'),
		(21,54, 5, 2025, '7', 2, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728018', 'REC018', NULL),
		(21,58, 4, 2025, '5', 4, 1000.00, 542.43, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728019', 'REC019', 'Partial payment received.'),
		(16,61, 5, 2025, '4', 3, 8000.00, 8000.00, '2025-07-28', '2025-08-02', 'Paid', 'TXN20250728020', 'REC020', NULL),
		(9,54, 3, 2025, '5', 12, 15000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(22,55, 2, 2024, '6', 9, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728022', 'REC022', NULL),
		(9,57, 1, 2025, '7', 12, 8000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(2,56, 3, 2024, '4', 6, 8000.00, 4774.13, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728024', 'REC024', 'Partial payment received.'),
		(22,60, 1, 2025, '5', 5, 37500.00, 37500.00, '2025-07-28', '2025-08-02', 'Paid', 'TXN20250728025', 'REC025', 'Full payment received.'),
		(12,23, 3, 2025, '5', 12, 15000.00, 15000.00, '2025-07-28', '2025-08-02', 'Paid', 'TXN20250728026', 'REC026', 'Exam fee.'),
		(22,65, 4, 2025, '8', 1, 35000.00, 35000.00, '2025-07-28', '2025-07-02', 'Paid', 'TXN20250728027', 'REC027', 'Transport fee.'),
		(12,52, 4, 2024, '7', 3, 15000.00, 7870.05, '2025-07-28', '2025-07-02', 'Partial', 'TXN20250728028', 'REC028', 'Partial payment received.'),
		(12,66, 2, 2025, '7', 9, 8000.00, 4332.33, '2025-07-28', '2025-07-02', 'Partial', 'TXN20250728029', 'REC029', 'Partial payment received.'),
		(17,53, 4, 2024, '7', 3, 37500.00, 17149.89, '2025-07-28', '2025-08-02', 'Partial', 'TXN20250728030', 'REC030', 'Partial payment received.'),
		(12,63, 2, 2024, '8', 12, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728031', 'REC031', 'Includes late fee.'),
		(19,58, 2, 2025, '7', 11, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728032', 'REC032', NULL),
		(22,55, 5, 2025, '5', 10, 2500.00, 2500.00, '2025-07-28', '2025-08-02', 'Paid', 'TXN20250728033', 'REC033', 'Includes late fee.'),
		(14,57, 5, 2025, '4', 1, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728034', 'REC034', NULL),
		(22,59, 4, 2025, '4', 1, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728035', 'REC035', NULL),
		(19,61, 3, 2024, '5', 6, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728036', 'REC036', 'Exam fee.'),
		(22,55, 5, 2025, '5', 12, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728037', 'REC037', 'Full payment received.'),
		(12,54, 1, 2024, '7', 7, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728038', 'REC038', 'Full payment received.'),
		(4,55, 1, 2024, '2', 9, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728039', 'REC039', 'Includes late fee.'),
		(21,61, 5, 2024, '3', 9, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728040', 'REC040', 'Full payment received.'),
		(22,63, 4, 2025, '2', 5, 2500.00, 1475.06, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728041', 'REC041', 'Partial payment received.'),
		(7,59, 4, 2025, '4', 9, 8000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(12,59, 2, 2025, '2', 9, 500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(17,55, 3, 2024, '3', 11, 8000.00, 2922.37, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728044', 'REC044', 'Partial payment received.'),
		(12,53, 1, 2025, '1', 11, 15000.00, 15000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728045', 'REC045', 'Transport fee.'),
		(19,66, 2, 2024, '1', 1, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728046', 'REC046', 'Transport fee.'),
		(16,61, 2, 2025, '7', 9, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728047', 'REC047', 'Transport fee.'),
		(21,52, 1, 2025, '4', 1, 2500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(11,67, 1, 2024, '5', 8, 2500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(2,54, 3, 2024, '6', 10, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728050', 'REC050', 'Full payment received.'),
		(16,23, 2, 2025, '6', 5, 37500.00, 18908.00, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728051', 'REC051', 'Partial payment received.'),
		(19,63, 1, 2024, '4', 2, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728052', 'REC052', 'Includes late fee.'),
		(17,61, 2, 2025, '7', 11, 8000.00, 8000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728053', 'REC053', 'Exam fee.'),
		(14,66, 3, 2024, '4', 11, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728054', 'REC054', 'Full payment received.'),
		(6,53, 5, 2025, '5', 1, 1000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(4,64, 5, 2024, '5', 12, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728056', 'REC056', 'Exam fee.'),
		(11,66, 2, 2024, '4', 9, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728057', 'REC057', NULL),
		(21,66, 4, 2025, '4', 5, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728058', 'REC058', 'Full payment received.'),
		(1,62, 3, 2025, '5', 1, 500.00, 154.99, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728059', 'REC059', 'Partial payment received.'),
		(17,63, 3, 2025, '4', 4, 15000.00, 15000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728060', 'REC060', NULL),
		(16,67, 4, 2024, '1', 8, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728061', 'REC061', 'Includes late fee.'),
		(12,53, 4, 2024, '1', 9, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728062', 'REC062', NULL),
		(2,53, 1, 2024, '1', 10, 1000.00, 617.31, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728063', 'REC063', 'Partial payment received.'),
		(19,65, 4, 2025, '5', 6, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728064', 'REC064', 'Exam fee.'),
		(17,54, 2, 2025, '5', 9, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728065', 'REC065', 'Exam fee.'),
		(22,60, 1, 2025, '7', 5, 37500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(2,54, 1, 2025, '4', 11, 37500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(16,61, 3, 2024, '7', 1, 35000.00, 18860.35, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728068', 'REC068', 'Partial payment received.'),
		(1,52, 1, 2025, '4', 3, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728069', 'REC069', 'Includes late fee.'),
		(19,60, 1, 2024, '1', 7, 37500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(19,52, 1, 2024, '7', 10, 1000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(16,55, 4, 2025, '7', 4, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728072', 'REC072', 'Includes late fee.'),
		(1,66, 2, 2024, '5', 7, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728073', 'REC073', 'Exam fee.'),
		(4,67, 2, 2024, '5', 2, 500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(11,64, 5, 2024, '4', 5, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728075', 'REC075', 'Exam fee.'),
		(12,57, 4, 2024, '4', 8, 8000.00, 8000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728076', 'REC076', 'Full payment received.'),
		(22,58, 3, 2025, '6', 5, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728077', 'REC077', 'Exam fee.'),
		(9,57, 5, 2025, '4', 11, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728078', 'REC078', NULL),
		(6,58, 5, 2025, '7', 11, 37500.00, 25877.26, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728079', 'REC079', 'Partial payment received.'),
		(9,62, 2, 2024, '7', 9, 500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(12,62, 1, 2024, '5', 4, 35000.00, 35000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728081', 'REC081', NULL),
		(22,56, 1, 2024, '4', 10, 2500.00, 2500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728082', 'REC082', 'Full payment received.'),
		(14,54, 2, 2025, '5', 12, 15000.00, 15000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728083', 'REC083', NULL),
		(6,61, 4, 2024, '7', 3, 15000.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(9,59, 2, 2024, '7', 5, 37500.00, 0.00, '2025-07-28', NULL, 'Unpaid', NULL, NULL, 'Payment pending.'),
		(14,52, 5, 2024, '4', 8, 1000.00, 1000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728086', 'REC086', 'Exam fee.'),
		(2,51, 5, 2025, '5', 5, 2500.00, 1043.92, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728087', 'REC087', 'Partial payment received.'),
		(17,23, 2, 2024, '5', 6, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728088', 'REC088', NULL),
		(6,67, 5, 2025, '2', 11, 1000.00, 685.82, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728089', 'REC089', 'Partial payment received.'),
		(12,60, 3, 2025, '1', 12, 2500.00, 1440.65, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728090', 'REC090', 'Partial payment received.'),
		(4,58, 3, 2024, '4', 1, 500.00, 151.68, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728091', 'REC091', 'Partial payment received.'),
		(1,57, 3, 2024, '4', 11, 8000.00, 2572.37, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728092', 'REC092', 'Partial payment received.'),
		(2,58, 2, 2024, '7', 10, 35000.00, 16244.57, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728093', 'REC093', 'Partial payment received.'),
		(17,55, 2, 2024, '5', 2, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728094', 'REC094', 'Includes late fee.'),
		(19,61, 3, 2024, '4', 8, 500.00, 323.02, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728095', 'REC095', 'Partial payment received.'),
		(9,55, 5, 2025, '6', 7, 37500.00, 37500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728096', 'REC096', 'Exam fee.'),
		(12,59, 2, 2025, '6', 4, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728097', 'REC097', 'Includes late fee.'),
		(22,66, 1, 2024, '7', 10, 1000.00, 311.80, '2025-07-28', '2025-07-28', 'Partial', 'TXN20250728098', 'REC098', 'Partial payment received.'),
		(14,65, 4, 2025, '7', 10, 500.00, 500.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728099', 'REC099', 'Transport fee.'),
		(17,65, 3, 2024, '8', 1, 15000.00, 15000.00, '2025-07-28', '2025-07-28', 'Paid', 'TXN20250728100', 'REC100', 'Exam fee.');



INSERT INTO Attendances (User_Id, Attendance_Date, Time_In, Time_Out, Status, Remarks) VALUES
-- June 2025 Records
		(1, '2025-08-02', '08:58:00', '17:02:00', 'Present', NULL),
		(1, '2025-05-03', '09:01:00', '17:05:00', 'Present', NULL),
		(1, '2025-04-04', '09:15:00', '17:03:00', 'Late', 'Stuck in traffic'),
		(1, '2025-06-05', '08:55:00', '17:00:00', 'Present', NULL),
		(1, '2025-03-06', '09:00:00', '13:00:00', 'Half Day', 'Doctor appointment'),
		(1, '2025-02-09', '08:57:00', '17:01:00', 'Present', NULL),
		(1, '2025-01-10', '08:59:00', '17:04:00', 'Present', NULL),
		(1, '2025-06-11', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-09-12', '00:00:00', '00:00:00', 'Absent', NULL),
		(1, '2025-10-13', '08:56:00', '16:58:00', 'Present', NULL),
		(1, '2025-11-16', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-12-17', '09:02:00', '17:03:00', 'Present', NULL),
		(1, '2025-06-18', '09:25:00', '17:10:00', 'Late', NULL),
		(1, '2025-06-19', '00:00:00', '00:00:00', 'Leave', 'Family emergency'),
		(1, '2025-06-20', '00:00:00', '00:00:00', 'Leave', 'Family emergency'),
		(1, '2025-06-23', '08:59:00', '17:01:00', 'Present', NULL),
		(1, '2025-06-24', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-06-25', '00:00:00', '00:00:00', 'Absent', NULL),
		(1, '2025-06-26', '09:05:00', '17:05:00', 'Present', NULL),
		(1, '2025-06-27', '08:58:00', '12:45:00', 'Half Day', 'Personal work'),
		(1, '2025-06-30', '09:00:00', '17:00:00', 'Present', NULL),

-- July 2025 Records
		(1, '2025-07-01', '09:03:00', '17:06:00', 'Present', NULL),
		(1, '2025-07-02', '09:18:00', '17:01:00', 'Late', NULL),
		(1, '2025-07-03', '08:59:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-04', '09:00:00', '17:02:00', 'Present', NULL),
		(1, '2025-07-07', '00:00:00', '00:00:00', 'Absent', NULL),
		(1, '2025-07-08', '09:01:00', '17:03:00', 'Present', NULL),
		(1, '2025-07-09', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-10', '08:57:00', '17:01:00', 'Present', NULL),
		(1, '2025-07-11', '09:00:00', '13:10:00', 'Half Day', 'Bank work'),
		(1, '2025-07-14', '08:58:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-15', '08:59:00', '17:02:00', 'Present', NULL),
		(1, '2025-07-16', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-17', '09:04:00', '17:05:00', 'Present', NULL),
		(1, '2025-07-18', '09:22:00', '17:08:00', 'Late', NULL),
		(1, '2025-07-21', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-22', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(1, '2025-07-23', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(1, '2025-07-24', '09:01:00', '17:00:00', 'Present', NULL),
		(1, '2025-07-25', '08:59:00', '17:03:00', 'Present', NULL),
		(1, '2024-07-28', '00:00:00', '00:00:00', 'Absent', NULL),
		(1, '2023-07-29', '09:02:00', '17:01:00', 'Present', NULL),
		(1, '2022-07-30', '09:00:00', '17:00:00', 'Present', NULL),
		(1, '2026-07-31', '09:05:00', '17:05:00', 'Present', NULL),

        (23, '2025-07-01', '09:03:00', '17:06:00', 'Present', NULL),
		(23, '2025-07-02', '09:18:00', '17:01:00', 'Late', NULL),
		(23, '2025-07-03', '08:59:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-04', '09:00:00', '17:02:00', 'Present', NULL),
		(23, '2025-07-07', '00:00:00', '00:00:00', 'Absent', NULL),
		(23, '2025-07-08', '09:01:00', '17:03:00', 'Present', NULL),
		(23, '2025-07-09', '09:00:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-10', '08:57:00', '17:01:00', 'Present', NULL),
		(23, '2025-07-11', '09:00:00', '13:10:00', 'Half Day', 'Bank work'),
		(23, '2025-07-14', '08:58:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-15', '08:59:00', '17:02:00', 'Present', NULL),
		(23, '2025-07-16', '09:00:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-17', '09:04:00', '17:05:00', 'Present', NULL),
		(23, '2025-07-18', '09:22:00', '17:08:00', 'Late', NULL),
		(23, '2025-07-21', '09:00:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-22', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(23, '2025-07-23', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(23, '2025-07-24', '09:01:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-25', '08:59:00', '17:03:00', 'Present', NULL),
		(23, '2025-07-28', '00:00:00', '00:00:00', 'Absent', NULL),
		(23, '2025-07-29', '09:02:00', '17:01:00', 'Present', NULL),
		(23, '2025-07-30', '09:00:00', '17:00:00', 'Present', NULL),
		(23, '2025-07-31', '09:05:00', '17:05:00', 'Present', NULL),

		(2, CURDATE(), '08:58:00', '17:01:00', 'Absent', NULL),
        (3, CURDATE(), '09:00:00', '17:03:00', 'Present', NULL),
        (4, CURDATE(), '08:50:00', '17:10:00', 'Present', NULL),
        (5, CURDATE(), '08:52:00', '17:00:00', 'Present', NULL),
        (6, CURDATE(), '08:59:00', '17:08:00', 'Present', NULL),
        (7, CURDATE(), '08:57:00', '17:02:00', 'Present', NULL),
        (8, CURDATE(), '08:49:00', '17:05:00', 'Present', NULL),
        (9, CURDATE(), '08:53:00', '17:11:00', 'Present', NULL),
        (10, CURDATE(), '08:56:00', '17:04:00', 'Present', NULL),
        (11, CURDATE(), '08:51:00', '17:07:00', 'Present', NULL),
        (12, CURDATE(), '08:54:00', '17:09:00', 'Present', NULL),
        (13, CURDATE(), '08:48:00', '17:06:00', 'Present', NULL),
        (14, CURDATE(), '08:55:00', '17:12:00', 'Present', NULL),
        (15, CURDATE(), '08:58:00', '17:01:00', 'Present', NULL),
        (16, CURDATE(), '09:00:00', '17:03:00', 'Present', NULL),
        (17, CURDATE(), '08:50:00', '17:10:00', 'Present', NULL),
        (18, CURDATE(), '08:52:00', '17:00:00', 'Present', NULL),
        (19, CURDATE(), '08:59:00', '17:08:00', 'Present', NULL),
        (20, CURDATE(), '08:57:00', '17:02:00', 'Present', NULL),
        (21, CURDATE(), '08:49:00', '17:05:00', 'Present', NULL),
        (22, CURDATE(), '08:53:00', '17:11:00', 'Present', NULL),
        (24, CURDATE(), '08:51:00', '17:07:00', 'Present', NULL),
        (25, CURDATE(), '08:54:00', '17:09:00', 'Present', NULL),
        (26, CURDATE(), '08:48:00', '17:06:00', 'Present', NULL),
        (27, CURDATE(), '08:55:00', '17:12:00', 'Present', NULL),
        (28, CURDATE(), '08:58:00', '17:01:00', 'Present', NULL),
        (29, CURDATE(), '09:00:00', '17:03:00', 'Present', NULL),
        (30, CURDATE(), '08:50:00', '17:10:00', 'Present', NULL),
        (31, CURDATE(), '09:15:00', '17:05:00', 'Late', 'Stuck in traffic.'),
        (32, CURDATE(), '09:22:00', '17:01:00', 'Late', NULL),
        (33, CURDATE(), '09:30:00', '17:03:00', 'Late', 'Metro delay.'),
        (34, CURDATE(), '09:18:00', '17:10:00', 'Late', NULL),
        (35, CURDATE(), '09:25:00', '17:00:00', 'Late', NULL),
        (36, CURDATE(), NULL, NULL, 'Absent', 'Notified sick.'),
        (37, CURDATE(), NULL, NULL, 'Absent', 'Unplanned absence.'),
        (38, CURDATE(), NULL, NULL, 'Absent', NULL),
        (39, CURDATE(), NULL, NULL, 'Absent', 'Family emergency.'),
        (40, CURDATE(), NULL, NULL, 'Absent', NULL),
        (41, CURDATE(), NULL, NULL, 'Leave', 'Approved annual leave.'),
        (42, CURDATE(), NULL, NULL, 'Leave', 'Approved sick leave.'),
        (43, CURDATE(), NULL, NULL, 'Leave', 'Approved personal leave.'),
        (44, CURDATE(), NULL, NULL, 'Leave', 'Approved study leave.'),
        (45, CURDATE(), NULL, NULL, 'Leave', 'Approved maternity leave.'),
        (46, CURDATE(), '09:00:00', '13:00:00', 'Half Day', 'Doctor appointment.'),
        (47, CURDATE(), '08:55:00', '13:05:00', 'Half Day', 'Personal commitment.'),
        (48, CURDATE(), '08:50:00', '12:55:00', 'Half Day', NULL),
        (49, CURDATE(), '09:05:00', '13:10:00', 'Half Day', 'Bank work.'),


        (50, '2025-08-02', '08:58:00', '17:02:00', 'Present', NULL),
		(50, '2025-05-03', '09:01:00', '17:05:00', 'Present', NULL),
		(50, '2025-04-04', '09:15:00', '17:03:00', 'Late', 'Stuck in traffic'),
		(50, '2025-06-05', '08:55:00', '17:00:00', 'Present', NULL),
		(50, '2025-03-06', '09:00:00', '13:00:00', 'Half Day', 'Doctor appointment'),
		(50, '2025-02-09', '08:57:00', '17:01:00', 'Present', NULL),
		(50, '2025-01-10', '08:59:00', '17:04:00', 'Present', NULL),
		(50, '2025-06-11', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-09-12', '00:00:00', '00:00:00', 'Absent', NULL),
		(50, '2025-10-13', '08:56:00', '16:58:00', 'Present', NULL),
		(50, '2025-11-16', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-12-17', '09:02:00', '17:03:00', 'Present', NULL),
		(50, '2025-06-18', '09:25:00', '17:10:00', 'Late', NULL),
		(50, '2025-06-19', '00:00:00', '00:00:00', 'Leave', 'Family emergency'),
		(50, '2025-06-20', '00:00:00', '00:00:00', 'Leave', 'Family emergency'),
		(50, '2025-06-23', '08:59:00', '17:01:00', 'Present', NULL),
		(50, '2025-06-24', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-06-25', '00:00:00', '00:00:00', 'Absent', NULL),
		(50, '2025-06-26', '09:05:00', '17:05:00', 'Present', NULL),
		(50, '2025-06-27', '08:58:00', '12:45:00', 'Half Day', 'Personal work'),
		(50, '2025-06-30', '09:00:00', '17:00:00', 'Present', NULL),

-- July 2025 Records
		(50, '2025-07-01', '09:03:00', '17:06:00', 'Present', NULL),
		(50, '2025-07-02', '09:18:00', '17:01:00', 'Late', NULL),
		(50, '2025-07-03', '08:59:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-04', '09:00:00', '17:02:00', 'Present', NULL),
		(50, '2025-07-07', '00:00:00', '00:00:00', 'Absent', NULL),
		(50, '2025-07-08', '09:01:00', '17:03:00', 'Present', NULL),
		(50, '2025-07-09', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-10', '08:57:00', '17:01:00', 'Present', NULL),
		(50, '2025-07-11', '09:00:00', '13:10:00', 'Half Day', 'Bank work'),
		(50, '2025-07-14', '08:58:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-15', '08:59:00', '17:02:00', 'Present', NULL),
		(50, '2025-07-16', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-17', '09:04:00', '17:05:00', 'Present', NULL),
		(50, '2025-07-18', '09:22:00', '17:08:00', 'Late', NULL),
		(50, '2025-07-21', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-22', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(50, '2025-07-23', '00:00:00', '00:00:00', 'Leave', 'Sick leave'),
		(50, '2025-07-24', '09:01:00', '17:00:00', 'Present', NULL),
		(50, '2025-07-25', '08:59:00', '17:03:00', 'Present', NULL),
		(50, '2024-07-28', '00:00:00', '00:00:00', 'Absent', NULL),
		(50, '2023-07-29', '09:02:00', '17:01:00', 'Present', NULL),
		(50, '2022-07-30', '09:00:00', '17:00:00', 'Present', NULL),
		(50, '2026-07-31', '09:05:00', '17:05:00', 'Present', NULL),

		(51, CURDATE(), '08:52:27', '13:08:26', 'Half Day', 'Left early for personal work'),
		(52, CURDATE(), '08:51:18', '17:05:25', 'Present', NULL),
		(53, CURDATE(), '08:49:39', '17:08:55', 'Present', NULL),
		(54, CURDATE(), '08:48:36', '17:09:08', 'Present', NULL),
		(55, CURDATE(), '09:07:58', '17:12:45', 'Late', 'Medical appointment'),
		(56, CURDATE(), NULL, NULL, 'Leave', 'Emergency'),
		(57, CURDATE(), '08:49:28', '17:09:23', 'Present', NULL),
		(58, CURDATE(), '08:49:09', '17:02:32', 'Present', NULL),
		(59, CURDATE(), '08:54:21', '17:02:29', 'Present', NULL),
		(60, CURDATE(), NULL, NULL, 'Leave', 'Medical leave'),
		(61, CURDATE(), NULL, NULL, 'Absent', 'Personal reasons'),
		(62, CURDATE(), '09:24:08', '17:12:51', 'Late', 'Medical appointment'),
		(63, CURDATE(), '08:55:52', '17:04:40', 'Present', NULL),
		(64, CURDATE(), '08:48:21', '17:02:18', 'Present', NULL),
		(65, CURDATE(), '08:45:55', '17:03:46', 'Present', NULL),
		(66, CURDATE(), NULL, NULL, 'Leave', 'Emergency'),
		(67, CURDATE(), '08:52:40', '17:03:30', 'Present', NULL),
		(68, CURDATE(), '08:58:31', '12:58:55', 'Half Day', 'Left early for personal work'),
		(69, CURDATE(), '08:48:18', '17:09:11', 'Present', NULL),
		(70, CURDATE(), '08:55:31', '17:08:26', 'Present', NULL),
		(71, CURDATE(), NULL, NULL, 'Leave', 'Medical leave'),
		(72, CURDATE(), '08:50:01', '17:05:00', 'Present', NULL),
		(73, CURDATE(), '08:48:19', '17:00:27', 'Present', NULL),
		(74, CURDATE(), '09:24:04', '17:12:51', 'Late', 'Medical appointment'),
		(75, CURDATE(), '08:58:13', '17:01:29', 'Present', NULL),
		(76, CURDATE(), NULL, NULL, 'Leave', 'Maternity leave'),
		(77, CURDATE(), '08:51:12', '17:06:25', 'Present', NULL),
		(78, CURDATE(), '08:48:32', '13:10:48', 'Half Day', 'Left early for personal work'),
		(79, CURDATE(), NULL, NULL, 'Leave', 'Emergency'),
		(80, CURDATE(), '08:57:50', '17:09:48', 'Present', NULL),
		(81, CURDATE(), '08:58:50', '17:09:51', 'Present', NULL),
		(82, CURDATE(), '08:45:34', '17:05:08', 'Present', NULL),
		(83, CURDATE(), '08:55:18', '17:06:03', 'Present', NULL),
		(84, CURDATE(), '09:11:52', '12:44:19', 'Half Day', 'Half-day approved by manager'),
		(85, CURDATE(), '08:49:30', '17:06:41', 'Present', NULL),
		(86, CURDATE(), '08:50:17', '17:03:16', 'Present', NULL),
		(87, CURDATE(), NULL, NULL, 'Absent', 'Sick leave'),
		(88, CURDATE(), '08:46:48', '17:08:11', 'Present', NULL),
		(89, CURDATE(), '09:17:25', '17:04:27', 'Late', 'Medical appointment'),
		(90, CURDATE(), '08:50:31', '17:03:13', 'Present', NULL),
		(91, CURDATE(), '08:56:16', '17:08:03', 'Present', NULL),
		(92, CURDATE(), '09:26:17', '17:09:58', 'Late', 'Overslept'),
		(93, CURDATE(), NULL, NULL, 'Leave', 'Maternity leave'),
		(94, CURDATE(), '08:55:48', '17:05:01', 'Present', NULL),
		(95, CURDATE(), '08:53:18', '17:04:43', 'Present', NULL),
		(96, CURDATE(), '08:54:45', '17:01:08', 'Present', NULL),
		(97, CURDATE(), NULL, NULL, 'Leave', 'Maternity leave'),
		(98, CURDATE(), NULL, NULL, 'Absent', 'No update'),
		(99, CURDATE(), '08:51:08', '17:08:00', 'Present', NULL),
		(100, CURDATE(), '08:47:01', '17:06:36', 'Present', NULL);

INSERT INTO Salary_Components (Component_Name, Component_Type, Description) VALUES
		('Basic Salary', 'Earning', 'Base pay component of the salary'),
		('House Rent Allowance (HRA)', 'Earning', 'Allowance for housing expenses'),
		('Conveyance Allowance', 'Earning', 'Allowance for travel expenses'),
		('Special Allowance', 'Earning', 'Additional allowance'),
		('Professional Tax', 'Deduction', 'Mandatory tax on profession'),
		('Provident Fund (PF)', 'Deduction', 'Mandatory retirement savings contribution'),
		('Income Tax (TDS)', 'Deduction', 'Tax deducted at source'),
		('Loan Repayment', 'Deduction', 'Deduction for employee loan repayment');

INSERT INTO Salary_Payments (User_Id, Salary_Month, Salary_Year, Gross_Amount, Total_Deductions, Net_Amount_Paid, Payment_Date, Transaction_Id, Payment_Status)VALUES
		(77, 2, 2024, 60000.00, 5500.00, 0.00, '2024-02-05', 'TXN-20240205-001-USER26', 'Paid'),
		(77, 3, 2024, 60000.00, 5500.00, 0.00, '2024-03-05', 'TXN-20240305-001-USER26', 'Paid'),
		(77, 1, 2024, 75000.00, 1000.00, 67000.00, '2024-01-05', 'TXN-20240205-002-USER84', 'Paid'),
		(84, 2, 2024, 75000.00, 8000.00, 67000.00, '2024-03-05', 'TXN-20240305-002-USER84', 'Paid'),
		(2, 1, 2024, 85000.00, 9500.00, 75500.00, '2024-02-05', 'TXN-20240205-003-USER2', 'Paid'),
		(2, 2, 2024, 85000.00, 9500.00, 75500.00, '2024-03-05', 'TXN-20240305-003-USER2', 'Paid'),
		(85, 3, 2024, 45000.00, 4200.00, 40800.00, '2024-04-05', 'TXN-20240405-001-USER85', 'Paid'),
		(6, 1, 2024, 52000.00, 5000.00, 47000.00, '2024-02-05', 'TXN-20240205-004-USER6', 'Paid'),
		(6, 2, 2024, 52000.00, 5000.00, 47000.00, '2024-03-05', 'TXN-20240305-004-USER6', 'Paid'),
		(19, 3, 2024, 68000.00, 7000.00, 61000.00, '2024-04-05', 'TXN-20240405-002-USER19', 'Paid'),
		(19, 2, 2024, 68000.00, 7000.00, 61000.00, '2024-03-05', 'TXN-20240305-005-USER19', 'Paid'),
		(31, 3, 2024, 90000.00, 10500.00, 79500.00, '2024-04-05', 'TXN-20240405-003-USER31', 'Paid'),
		(50, 7, 2024, 55000.00, 5200.00, 49800.00, '2024-07-03', 'TXN-20240205-005-USER4', 'Paid'),
		(50, 8, 2024, 55000.00, 5200.00, 49800.00, '2024-08-03', 'TXN-20240305-006-USER4', 'Paid'),
		(7, 1, 2024, 48000.00, 4500.00, 43500.00, '2024-02-05', 'TXN-20240205-006-USER7', 'Paid'),
		(7, 2, 2024, 48000.00, 4500.00, 43500.00, '2024-03-05', 'TXN-20240305-007-USER7', 'Paid'),
		(11, 2, 2024, 72000.00, 7800.00, 64200.00, '2024-03-05', 'TXN-20240305-008-USER11', 'Paid'),
		(11, 3, 2024, 72000.00, 7800.00, 64200.00, '2024-04-05', 'TXN-20240405-004-USER11', 'Paid'),
		(9, 1, 2024, 88000.00, 10200.00, 77800.00, '2024-02-05', 'TXN-20240205-007-USER9', 'Paid'),
		(9, 2, 2024, 88000.00, 10200.00, 77800.00, '2024-03-05', 'TXN-20240305-009-USER9', 'Paid');

SELECT Status, COUNT(Attendance_Id) AS NumberOfPeople FROM Attendances WHERE User_Id = 8 AND MONTH(Attendance_Date) = MONTH(CURDATE()) AND YEAR(Attendance_Date) = YEAR(CURDATE()) GROUP BY Status;
select * from Attendances ;

SELECT SUM(Amount_Paid) FROM Student_Fees where User_Id=12;
SELECT * FROM Student_Fees;

Select SUM(Amount_Paid) AS TOTAL from Student_Fees where Fees_Paid_By_User_Id=55 AND Fee_Month=7;
select * from Users where User_Id=23;

select b.Book_Id, b.Title,br.User_Id, br.Borrow_Date, br.Due_Date, br.Status,br.Return_Date, br.Fine_Amount, br.Remarks from Books b JOIN Borrow_Records br ON b.Book_Id=br.Book_Id;
select * from Salary_Payments Where User_id=72;

update Users set User_Status='Inactive' where User_Id>0;
select DISTINCT Fee_Type_name from Fee_Types;

update Authentication set Lockout_Until =null where User_Id=14;
select * from Attendances where User_Id=50;

UPDATE Users SET Admin_Approval_Status = 'Approved' WHERE User_Id > 0	;
Select u.User_Id,u.Role,u.First_Name,u.Last_Name,a.UserName,a.Password_Hash,a.Last_Login,u.Admin_Approval_Status from Users u JOIN Authentication a ON u.User_Id=a.User_Id;

SELECT SUM(Default_Amount) AS TotalDefaultAmount FROM Fee_Types WHERE Departments ='BFA';
select (Amount_Paid) from Student_Fees where User_Id =12;

update Attendances set Time_In='8:58:00' where Attendance_Id=68;
SELECT * FROM Salary_Components;

select COUNT(*) from Salary_Payments where Salary_month=8;
select * from Salary_Payments ;
USE university_management_system;
