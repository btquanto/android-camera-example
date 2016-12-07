package com.theitfox.camera.presentation.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;


/**
 * Created by btquanto on 20/10/2016.
 */
public class CryptoHelper {

    private static final String BASE64_ENCODED_CRYPTO_KEY = "UTIwQU5PY0l0Mzd2T3hmMENoZ1ZqMEs2c3BKSElFbk9aU0Q3OHpFaGNveGpTSXhz";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String STRING_ENCODING = "UTF-8";
    private static final int KEY_SPEC_ITERATION_COUNT = 1024;
    private static final int KEY_SPEC_KEY_LENGTH = 256;

    private static String defaultCryptoKey;

    private String cryptoKey;
    private Scheduler executionThread;
    private Scheduler postExecutionThread;
    private PrefsHelper prefsHelper;
    private String PIN;

    /**
     * Instantiates a new Crypto helper.
     *
     * @param prefsHelper         the prefs helper
     * @param executionThread     the execution thread
     * @param postExecutionThread the post execution thread
     */
    @Inject
    public CryptoHelper(PrefsHelper prefsHelper,
                        @Named("executionThread") Scheduler executionThread,
                        @Named("postExecutionThread") Scheduler postExecutionThread) {
        this.prefsHelper = prefsHelper;
        this.executionThread = executionThread;
        this.postExecutionThread = postExecutionThread;
    }

    private static Key generateKeyFromPassword(String password, int keySpecIterationCount, int keySpectKeyLength) throws CryptoException {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEwithSHA256and256BITAES-CBC-BC");

            // Password-based encryption key spec, using the default encryption key as salt
            KeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(),
                    getDefaultCryptoKey().getBytes(STRING_ENCODING),
                    keySpecIterationCount,
                    keySpectKeyLength);

            SecretKey secretKey = keyFactory.generateSecret(pbeKeySpec);

            return new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);
        } catch (Exception ex) {
            if (ex instanceof CryptoException) {
                throw (CryptoException) ex;
            }
            throw new CryptoException(ex);
        }
    }

    private static String getDefaultCryptoKey() {
        if (defaultCryptoKey == null) {
            byte[] decoded = Base64.decode(BASE64_ENCODED_CRYPTO_KEY, Base64.DEFAULT);
            try {
                defaultCryptoKey = new String(decoded, STRING_ENCODING);
            } catch (UnsupportedEncodingException e) {
                defaultCryptoKey = new String(decoded);
            }
        }
        return defaultCryptoKey;
    }

    private static String generateRandomKeyString() throws CryptoException {
        Random random = new Random(System.currentTimeMillis());
        return new BigInteger(256, random).toString(32);
    }

    /**
     * Return a base64 encoded string from the byte array
     * resulted from encrypting {@param text} with {@param cryptoKey}
     *
     * @param text                  the text that we want to encrypt
     * @param encryptionKey         the encryption key that is used to encrypt {@param text}
     * @param keySpecIterationCount the key spec iteration count
     * @param keySpecKeyLength      the key spec key length
     * @return A base 64 encoded string to be stored somewhere.
     * @throws CryptoException the crypto exception
     */
    public static String encrypt(String text, String encryptionKey, int keySpecIterationCount, int keySpecKeyLength) throws CryptoException {
        try {
            // Generate a Key object from the encryption key
            Key aesKey = generateKeyFromPassword(encryptionKey, keySpecIterationCount, keySpecKeyLength);

            // Get the Cipher instance for the encryption
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            // Encrypt the text
            byte[] encrypted = cipher.doFinal(text.getBytes(STRING_ENCODING));

            // Encode the encrypted content byte array to a base64 encoded string
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            throw new CryptoException(ex);
        }
    }

    /**
     * Decrypt a base64 encoded encrypted text with {@param decryptionKey}
     *
     * @param base64Encoded         the base64 encoded encrypted text that we want to decrypt
     * @param decryptionKey         the decryption key that is used to decrypt {@param text}
     * @param keySpecIterationCount the key spec iteration count
     * @param keySpecKeyLength      the key spec key length
     * @return The decrypted content
     * @throws CryptoException the crypto exception
     */
    public static String decrypt(String base64Encoded, String decryptionKey, int keySpecIterationCount, int keySpecKeyLength) throws CryptoException {
        try {
            // Decode encrypted content byte array from the base64 encoded string
            byte[] encrypted = Base64.decode(base64Encoded, Base64.DEFAULT);

            // Generate a Key object from the decryption key
            Key aesKey = generateKeyFromPassword(decryptionKey, keySpecIterationCount, keySpecKeyLength);

            // Get the Cipher instance for the encryption
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            // Decrypt the byte array
            byte[] decrypted = cipher.doFinal(encrypted);

            // Convert the decrypted byte array to the original text
            return new String(decrypted, STRING_ENCODING);
        } catch (Exception ex) {
            if (ex instanceof CryptoException) {
                throw (CryptoException) ex;
            }
            throw new CryptoException(ex);
        }
    }

    /**
     * Sets pin.
     *
     * @param PIN the pin
     */
    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    /**
     * Encrypt observable.
     *
     * @param text the text
     * @return the observable
     */
    public Observable<String> encrypt(String text) {
        return getCryptoKey()
                .flatMap(encryptionKey -> Observable.<String>create(subscriber -> {
                    if (text != null && !text.isEmpty()) {
                        try {
                            subscriber.onNext(encrypt(text, encryptionKey, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH));
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                        }
                    } else {
                        subscriber.onNext(text);
                    }
                    subscriber.onCompleted();
                })).subscribeOn(executionThread)
                .observeOn(postExecutionThread);
    }

    /**
     * Decrypt observable.
     *
     * @param text the text
     * @return the observable
     */
    public Observable<String> decrypt(String text) {
        return getCryptoKey()
                .flatMap(encryptionKey -> Observable.<String>create(subscriber -> {
                    if (text != null && !text.isEmpty()) {
                        try {
                            subscriber.onNext(decrypt(text, encryptionKey, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH));
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                        }
                    } else {
                        subscriber.onNext(text);
                    }
                    subscriber.onCompleted();
                })).subscribeOn(executionThread)
                .observeOn(postExecutionThread);
    }

    private Observable<String> getCryptoKey() {
        return Observable.just(cryptoKey).map(key -> {
            if (key == null) {
                // Get the default encryption key
                final String defaultEncryptionKey = CryptoHelper.getDefaultCryptoKey();
                cryptoKey = defaultEncryptionKey;

                // Get the encrypted encryption key from SharedPreferences
                String encryptedCryptoKey = prefsHelper.getEncryptedCryptoKey();

                // If not encrypted encryption key is found in the SharedPreferences, then generate an encryption key
                final int iterationCount = KEY_SPEC_ITERATION_COUNT;
                final int keyLength = KEY_SPEC_KEY_LENGTH;
                if (encryptedCryptoKey.isEmpty()) {
                    try {
                        String encryptionKey = CryptoHelper.generateRandomKeyString();
                        // Encrypt the generated encryption key
                        encryptedCryptoKey = CryptoHelper.encrypt(encryptionKey, defaultEncryptionKey, iterationCount, keyLength);
                        // Save the generated encryption key to SharedPreferences
                        prefsHelper.setEncryptedCryptoKey(encryptedCryptoKey);
                    } catch (CryptoHelper.CryptoException e) {
                        e.printStackTrace();
                    }
                }

                // Decrypt the encrypted encryption key using the default encryption key
                if (!encryptedCryptoKey.isEmpty()) {
                    try {
                        cryptoKey = CryptoHelper.decrypt(encryptedCryptoKey, defaultEncryptionKey, iterationCount, keyLength);
                    } catch (CryptoHelper.CryptoException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (PIN != null) {
                return String.format("%s%s", PIN, cryptoKey);
            }
            return cryptoKey;
        });
    }

    /**
     * The type Crypto exception.
     */
    public static class CryptoException extends Exception {

        private CryptoException(Throwable cause) {
            super(cause);
        }
    }
}
