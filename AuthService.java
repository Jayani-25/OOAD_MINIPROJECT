public class AuthService {

    private static String currentOTP = null;

    public static String generateOTP() {
        int otp = (int) (Math.random() * 9000) + 1000;
        currentOTP = String.valueOf(otp);
        return currentOTP;
    }

    public static boolean verifyOTP(String input) {
        return currentOTP != null && input != null && currentOTP.equals(input.trim());
    }

    public static void clearOTP() {
        currentOTP = null;
    }
}
