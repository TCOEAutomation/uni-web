package utility;

import org.jboss.aerogear.security.otp.Totp;

public class TOTPGenerator
{
    public static String getTwoFactorCode()
    {
        Totp totp = new Totp(Constant.googleKey); // 2FA secret key
        String twoFactorCode = totp.now();
        return twoFactorCode;
    }
}