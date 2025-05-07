package app;

// === IMPORTS ===
// JavaFX is used for building GUI applications
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

// Custom classes for the banking app
import model.Bank;
import model.BankUserDTO;
import dao.BankUserDAOImpl;
import model.PasswordEncryptionService;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;

import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class BankApplication extends Application {

   // === UI INPUT FIELDS ===
   // Signup form fields
   private TextField fNameTF, lNameTF, emailTF, phoneNoTF;
   private PasswordField pinTF;
   private Label error; // Displays error on signup

   // Login form fields
   private TextField emailLoginTF;
   private PasswordField passwordLoginTF;
   private Label lgError; // Displays error on login

   // Strings to store user input
   private String fname, lname, email, phoneNo, loginEmail, plainPinString, pinText;

   // Bank account page components
   private Label balLabel, errLbl;
   private TextField amtField;

   // Database access and user data objects
   BankUserDAOImpl userdb;       // Data access object for DB operations
   BankUserDTO bankUser;         // DTO holding user details
   Bank userBankAccount;         // Bank account instance

   private Stage primaryStage;   // Primary stage (window)

   @Override
   public void start(Stage primaryStage) {
      this.primaryStage = primaryStage;
      Scene signUpScene = signUpScene();  // Initial scene shown to user
      signUpScene.getStylesheets().add("bank.css");  
      switchScene(signUpScene);
   }

   // Method to switch between scenes in the application
   private void switchScene(Scene newScene) {
      if (newScene == null) {
         System.out.println("primaryStage is null!");
         return;
      }
      newScene.getStylesheets().add(getClass().getResource("bank.css").toExternalForm());
      primaryStage.setScene(newScene);
      primaryStage.show(); 
   }

   // === SIGNUP SCENE ===
   // This method builds the scene containing the sign-up form
   private Scene signUpScene() {
      HBox hbox = new HBox(20); // Horizontal layout
      hbox.getChildren().addAll(logo(), signUpForm()); // Add logo and form side by side
      hbox.setAlignment(Pos.CENTER);
      hbox.setPadding(new Insets(20)); 
      return new Scene(hbox, 440, 480); // Width: 440, Height: 480
   }

   // Builds the signup form with all fields and buttons
   private VBox signUpForm() {
      VBox vbox = new VBox(25);
      vbox.setId("sign-up");

      // Create text fields
      fNameTF = new TextField();
      lNameTF = new TextField();
      emailTF = new TextField();
      pinTF = new PasswordField();
      phoneNoTF = new TextField();
      error = new Label("");
      error.setId("error"); // Style ID for CSS

      Button signUpBtn = new Button("Sign Up");
      Button loginBtn = new Button("Login");

      // Button to register user
      signUpBtn.setOnAction(e -> {
         if (insertUser() && isTextValue()) {
            switchScene(accountScene());
         }
      });

      // Go to login scene
      loginBtn.setOnAction(e -> switchScene(loginScene()));

      // Form layout
      vbox.getChildren().addAll(
         new HBox(new Label("First Name: "), fNameTF),
         new HBox(new Label("Last Name: "), lNameTF),
         new HBox(new Label("Email: "), emailTF),
         new HBox(new Label("Enter PIN: "), pinTF),
         new HBox(new Label("Phone Num: "), phoneNoTF),
         signUpBtn, loginBtn, error
      );
      vbox.setAlignment(Pos.CENTER);
      return vbox;
   }

   // Displays the logo image on signup screen
   private VBox logo() {
      VBox vbox = new VBox(10);
      vbox.setId("logo");
      vbox.setMinWidth(155);
      Image img = new Image(getClass().getResource("visa.png").toExternalForm());
      ImageView imageView = new ImageView(img);
      vbox.getChildren().add(imageView);
      vbox.setAlignment(Pos.CENTER);
      vbox.setPadding(new Insets(20));
      return vbox;
   }

   // Validates all signup text fields using regex patterns
   private boolean isTextValue() {
      fname = fNameTF.getText().trim();
      lname = lNameTF.getText().trim();
      email = emailTF.getText().trim();
      plainPinString = pinTF.getText().trim();
      phoneNo = phoneNoTF.getText().trim();

      // Check if fields are empty
      if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || phoneNo.isEmpty() || plainPinString.isEmpty()) {
         error.setText("Enter all fields correctly");
         return false;
      }

      // Patterns for validation
      String emailRegex = "^\\w{5,15}@\\w+\\.[a-zA-Z]{2,3}$";
      String passRegex = "^\\d{4}$"; // PIN = 4 digits only
      String phoneRegex = "^\\+?\\d{7,15}$";

      Matcher emailMatcher = Pattern.compile(emailRegex).matcher(email);
      Matcher passMatcher = Pattern.compile(passRegex).matcher(plainPinString);
      Matcher phoneMatcher = Pattern.compile(phoneRegex).matcher(phoneNo);

      // Return false if any input is invalid
      if (!emailMatcher.matches()) return false;
      if (!phoneMatcher.matches()) return false;
      if (!passMatcher.matches()) {
         error.setText("PIN must be exactly 4 digits.");
         return false;
      }
      return true;
   }

   // Creates a new user in the database if inputs are valid
   private boolean insertUser() {
      if (isTextValue()) {
         try {
            userdb = new BankUserDAOImpl();
            // Create user DTO with entered data
            bankUser = new BankUserDTO(fname, lname, email, phoneNo);
            userdb.insert(bankUser, plainPinString); // store user + encrypted pin
            userBankAccount = new Bank(bankUser.getId()); // initialize bank account
            userdb.insertBank(userBankAccount); // insert into DB
            printUserDetails(bankUser);
         } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
         }
         return true;
      } else {
         error.setText("Sign-up unsuccessful. Make sure all fields are entered correctly.");
         printUserDetails(); // print anyway for debugging
         return false;
      }
   }

   // Helper to print input details (for debugging)
   private void printUserDetails() {
      System.out.println("First Name: " + fname);
      System.out.println("Last Name: " + lname);
      System.out.println("Email: " + email);
      System.out.println("PIN: " + plainPinString);
      System.out.println("Phone Number: " + phoneNo);
   }

   private void printUserDetails(BankUserDTO user) {
      System.out.println("Login successful!");
      System.out.println("User Details:");
      System.out.println("First Name: " + user.getFname());
      System.out.println("Last Name: " + user.getLname());
      System.out.println("Email: " + user.getEmail());
      System.out.println("Phone Number: " + user.getPhone());
   }

   // === ACCOUNT SCENE ===
   // Scene shown after successful login or registration
   private Scene accountScene() {
      VBox accountDashboard = new VBox(10);
      accountDashboard.getChildren().addAll(createLabel(), createButtonPane());
      return new Scene(accountDashboard, 300, 200);
   }

   // Displays current balance and amount field
   private VBox createLabel() {
      VBox pane = new VBox(10);
      HBox panes = new HBox(10);
      errLbl = new Label("");
      balLabel = new Label("Balance = " + userBankAccount.getBalance());
      amtField = new TextField("" + 0.0);
      Label amtLabel = new Label("Amount ");

      pane.getChildren().addAll(panes, amtLabel, amtField, errLbl);
      pane.setAlignment(Pos.CENTER);
      pane.setPadding(new Insets(10));
      panes.getChildren().addAll(balLabel);
      panes.setAlignment(Pos.CENTER);
      return pane;
   }

   // Buttons for depositing or withdrawing money
   private HBox createButtonPane() {
      HBox pane = new HBox(10);
      Button withBtn = new Button("Withdraw");
      Button deptBtn = new Button("Deposit");

      // Withdraw action
      withBtn.setOnAction(e -> {
         if (isDouble(amtField)) {
            double withamt = Double.parseDouble(amtField.getText());
            try {
               if (userBankAccount.withdraw(withamt)) {
                  balLabel.setText("Balance = " + userBankAccount.getBalance());
                  errLbl.setText("Withdraw successful");
               } else {
                  errLbl.setText("Withdraw unsuccessful");
               }
            } catch (SQLException e1) {
               e1.printStackTrace();
            }
         } else {
            errLbl.setText("Withdraw unsuccessful");
         }
      });

      // Deposit action
      deptBtn.setOnAction(e -> {
         if (isDouble(amtField)) {
            double depamt = Double.parseDouble(amtField.getText());
            try {
               if (userBankAccount.deposit(depamt)) {
                  balLabel.setText("Balance = " + userBankAccount.getBalance());
                  errLbl.setText("Deposit successful");
               } else {
                  errLbl.setText("Deposit unsuccessful");
               }
            } catch (SQLException e1) {
               e1.printStackTrace();
            }
         } else {
            errLbl.setText("Deposit unsuccessful");
         }
      });

      pane.getChildren().addAll(deptBtn, withBtn);
      pane.setAlignment(Pos.CENTER);
      return pane;
   }

   // Check if amount is a valid number
   private boolean isDouble(TextField fld) {
      try {
         Double.parseDouble(fld.getText());
         return true;
      } catch (NumberFormatException e) {
         System.out.println("Data Entry Error");
         return false;
      }
   }

   // === LOGIN SCENE ===
   // Shows login form
   private Scene loginScene() {
      VBox vbox = new VBox(10);
      vbox.getChildren().addAll(loginForm());
      vbox.setAlignment(Pos.CENTER);
      return new Scene(vbox, 300, 200);
   }

   // Login form layout
   private VBox loginForm() {
      VBox loginVBox = new VBox(10);
      emailLoginTF = new TextField();
      passwordLoginTF = new PasswordField();
      Button loginButton = new Button("Login");
      lgError = new Label();

      loginButton.setOnAction(e -> {
         if (validateLoginInput() && loggedUser()) {
            switchScene(accountScene());
         }
      });

      loginVBox.getChildren().addAll(
         new HBox(new Label("Email: "), emailLoginTF),
         new HBox(new Label("Pin: "), passwordLoginTF),
         lgError, loginButton
      );

      loginVBox.setAlignment(Pos.CENTER);
      loginVBox.setPadding(new Insets(20));
      return loginVBox;
   }

   // Validates login fields (email format and 4-digit pin)
   private boolean validateLoginInput() {
      loginEmail = emailLoginTF.getText().trim();
      pinText = passwordLoginTF.getText().trim();

      if (loginEmail.isEmpty() || pinText.isEmpty()) {
         lgError.setText("Please enter both email and PIN.");
         return false;
      }

      Matcher emailMatcher = Pattern.compile("^\\w{5,15}@\\w+\\.[a-zA-Z]{2,3}$").matcher(loginEmail);
      Matcher passMatcher = Pattern.compile("^\\d{4}$").matcher(pinText);

      if (!emailMatcher.matches()) return false;
      if (!passMatcher.matches()) {
         lgError.setText("PIN must be exactly 4 digits.");
         return false;
      }
      return true;
   }

   // Verifies user login credentials and authenticates password
   private boolean loggedUser() {
      if (validateLoginInput()) {
         try {
            userdb = new BankUserDAOImpl();
            BankUserDTO potentialUser = userdb.findUserByEmailForLogin(loginEmail);

            if (potentialUser != null) {
               byte[] storedHash = Base64.getDecoder().decode(potentialUser.getPinHash());
               byte[] storedSalt = Base64.getDecoder().decode(potentialUser.getPinSalt());

               // Securely check password
               if (PasswordEncryptionService.authenticate(pinText, storedHash, storedSalt)) {
                  if (mfaOtp(potentialUser)) {
                     bankUser = potentialUser;
                     userBankAccount = new Bank(bankUser.getId());
                     printUserDetails(bankUser);
                     return true;
                  }
               } else {
                  lgError.setText("Invalid email or PIN.");
               }
            }
         } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
            lgError.setText("An error occurred. Please try again.");
         }
      }
      return false;
   }

   // Simulates MFA by asking for an OTP input
   private boolean mfaOtp(BankUserDTO user) {
      System.out.println("Simulating OTP sent to phone: " + user.getPhone());
      TextInputDialog otpDialog = new TextInputDialog();
      otpDialog.setTitle("Multi-Factor Authentication");
      otpDialog.setHeaderText("OTP Verification Required");
      otpDialog.setContentText("An OTP has been sent to your registered phone number  " + user.getPhone() + ".\nPlease enter the OTP below:");
      otpDialog.getDialogPane().getStylesheets().add(getClass().getResource("bank.css").toExternalForm());
      String enteredOtp = otpDialog.showAndWait().orElse("").trim();

      if (enteredOtp.isEmpty()) {
         lgError.setText("OTP verification cancelled.");
         return false;
      }
      return validateOTP(enteredOtp);
   }

   // Validates entered OTP (hardcoded for demo)
   private boolean validateOTP(String otp) {
      String correctOTP = "5556";
      return otp.equals(correctOTP);
   }

   // Main method to launch the application
   public static void main(String[] args) {
      launch(args);
   }
}
