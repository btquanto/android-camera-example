package com.theitfox.camera.presentation.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by btquanto on 21/10/2016.
 */
public class PrefsHelper {

    private static final String PREF_USERNAME = "jQgvUdz2v8iXw6pA";
    private static final String PREF_BASE64_ENCODED_ENCRYPTED_PASSWORD = "BSoBlKdbyPWiUp";
    private static final String PREF_ENCRYPTED_CRYPTO_KEY = "WPz8NjWBQgVH0Y5k41Pq6";

    private SharedPreferences sharedPreferences;

    /**
     * Instantiates a new Prefs helper.
     *
     * @param sharedPreferences the shared preferences
     */
    @Inject
    public PrefsHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return sharedPreferences.getString(PREF_USERNAME, "");
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        savePreferences(PREF_USERNAME, username);
    }

    /**
     * Is auto login boolean.
     *
     * @return the boolean
     */
    public boolean isAutoLogin() {
        return sharedPreferences.getBoolean("IS_AUTO_LOGIN", false);
    }

    /**
     * Sets auto login.
     *
     * @param isAutoLogin the is auto login
     */
    public void setAutoLogin(boolean isAutoLogin) {
        savePreferences("IS_AUTO_LOGIN", isAutoLogin);
    }

    /**
     * Gets base 64 encoded encrypted password.
     *
     * @return the base 64 encoded encrypted password
     */
    public String getEncryptedPassword() {
        return sharedPreferences.getString(PREF_BASE64_ENCODED_ENCRYPTED_PASSWORD, "");
    }

    /**
     * Sets base 64 encoded encrypted password.
     *
     * @param base64EncodedEncryptedPassword the base 64 encoded encrypted password
     */
    public void setEncryptedPassword(String base64EncodedEncryptedPassword) {
        savePreferences(PREF_BASE64_ENCODED_ENCRYPTED_PASSWORD, base64EncodedEncryptedPassword);
    }

    /**
     * Gets encrypted crypto key.
     *
     * @return the encrypted crypto key
     */
    public String getEncryptedCryptoKey() {
        return sharedPreferences.getString(PREF_ENCRYPTED_CRYPTO_KEY, "");
    }

    /**
     * Sets encrypted crypto key.
     *
     * @param base64EncodedCryptoKey the base 64 encoded crypto key
     */
    public void setEncryptedCryptoKey(String base64EncodedCryptoKey) {
        savePreferences(PREF_ENCRYPTED_CRYPTO_KEY, base64EncodedCryptoKey);
    }

    private void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.apply();
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
