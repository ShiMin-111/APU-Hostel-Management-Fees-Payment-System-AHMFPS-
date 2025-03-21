package ahmfps;

import java.time.LocalDateTime;

public class AHMFPS {
    public static MainPage first;
    public static RegistrationPage second;
    public static ManagerPage third;
    public static ResidentPage fourth;
    public static Users loginUser = null;
    public static LocalDateTime loginTime; // To record the login time
    public static LocalDateTime logoutTime;
    
    public static void main(String[] args) {
        DataIO.read();
        first = new MainPage();
        second = new RegistrationPage();
        third = new ManagerPage();
        fourth = new ResidentPage("");  
        loginTime = LocalDateTime.now();
        logoutTime = LocalDateTime.now();
    }    
}
