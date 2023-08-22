package io.github.clouderhem.legym.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author Aaron Yeung
 */
public class EncryptUtils {
    /**
     * fixme salt找我拿
     */
    private static final String SALT = "the_fake_salt";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    public static String getSha1(String str) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                buf[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static String hs(String str) {
        str += SALT;
        return getSha1(str);
    }

    public static void main(String[] args) throws Exception {
        if (!"175a25447f71189f949a73de5a8bd9a627d909db".
                equals(hs("1660569053174"))) {
            throw new Exception("sha1 error");
        }
        if (!"ed99333309c0b71e7a3d256edd6cca7445df7f8a".
                equals(hs("dsgjudshkfy3hrthjtyi"))) {
            throw new Exception("sha1 error");
        }
    }
}
