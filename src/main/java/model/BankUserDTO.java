package model;
public  class BankUserDTO{
   private  int userId;
   private String fName;
   private String lName;
   private String email;
   private String pinHash; // Added - Stores Base64 encoded hash
   private String pinSalt; // Added - Stores Base64 encoded salt
   private String phone;

   
   public BankUserDTO(int id, String fname, String lname, String email, String pinHash, String pinSalt, String phone) {
      this.userId = id;
      this.fName = fname;
      this.lName = lname;
      this.email = email;
      this.pinHash = pinHash; // Assign hash
      this.pinSalt = pinSalt; // Assign salt
      this.phone = phone;
  }
     
   
   public BankUserDTO(String userfName,String userlName,String userEmail,String phone ){
   
      this.fName= userfName;
      this.lName= userlName;
      this.email = userEmail;
      this.phone = phone;
         
   }
   public int getId(){
      return this.userId;
   }
   public String getFname(){
      return this.fName;
   }
   public String getLname(){
      return this.lName;
   }
   public String getEmail(){
      return this.email;
   }
   public String getPinHash() { 
      return pinHash; 
   } // Added getter

   public String getPinSalt() {
       return pinSalt; 
   } // Added getter

   public String getPhone(){
      return this.phone;
   }
   public void setId(int id) {
      this.userId = id;
   }
   public void setFname(String name) { this.fName = name; }
   public void setLname(String name) { this.lName = name; }
   public void setEmail(String email) { this.email = email; }
   public void setPhone(String phone) { this.phone = phone; }
   public void setPinHash(String pinHash) { this.pinHash = pinHash; } // Added setter
   public void setPinSalt(String pinSalt) { this.pinSalt = pinSalt; } // Added setter

   @Override
    public String toString() {
        return "BankUserDTO [id=" + userId + ", fname=" + fName + ", lname=" + lName + ", email=" + email + ", phone=" + phone
                + ", pinHash=HIDDEN, pinSalt=HIDDEN]"; 
    }
}