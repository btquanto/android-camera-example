package com.theitfox.camera.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by btquanto on 21/10/2016.
 * This class wraps a shared preferences object,
 * then does some minimal obfuscation before storing shared preferences values
 */
public class ObscuredSharedPreferences implements SharedPreferences {

    /**
     * The shared preferences file name
     */
    public static final String PREFERENCES_FILE_NAME = "BRlvCXmlVvpHDJGHSg";
    /**
     * The constant STRING_ENCODING.
     */
    protected static final String STRING_ENCODING = "UTF-8";
    private static final String BASE64_ENCODED_CRYPTO_KEY = "VTIxUmVGSnNXazlXYTBwaFZGVmtNMlF4VWxsag==";
    private static final int KEY_SPEC_ITERATION_COUNT = 64;
    private static final int KEY_SPEC_KEY_LENGTH = 128;
    /**
     * The Context.
     */
    protected Context context;
    /**
     * The Delegate.
     */
    protected SharedPreferences delegate;

    private String cryptoKey;

    /**
     * Instantiates a new Obscured shared preferences.
     *
     * @param context  the context
     * @param delegate the delegate
     */
    @Inject
    public ObscuredSharedPreferences(Context context, SharedPreferences delegate) {
        this.context = context;
        this.delegate = delegate;
        this.cryptoKey = base64DecodeString(BASE64_ENCODED_CRYPTO_KEY);
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String encodedValue = delegate.getString(key, null);
        return encodedValue != null ? Boolean.parseBoolean(decrypt(encodedValue)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String encodedValue = delegate.getString(key, null);
        return encodedValue != null ? Float.parseFloat(decrypt(encodedValue)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String encodedValue = delegate.getString(key, null);
        return encodedValue != null ? Integer.parseInt(decrypt(encodedValue)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String encodedValue = delegate.getString(key, null);
        return encodedValue != null ? Long.parseLong(decrypt(encodedValue)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String encodedValue = delegate.getString(key, null);
        return encodedValue != null ? decrypt(encodedValue) : defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        final Set<String> encodedValue = delegate.getStringSet(key, defValues);
        if (encodedValue == null) {
            return defValues;
        }
        Set<String> values = new HashSet<>();
        for (Iterator<String> itr = encodedValue.iterator(); itr.hasNext(); ) {
            values.add(decrypt(itr.next()));
        }
        return values;
    }

    @Override
    public boolean contains(String s) {
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    /**
     * Encrypt string.
     *
     * @param value the value
     * @return the string
     */
    protected String encrypt(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            return base64EncodeString(CryptoHelper.encrypt(value, cryptoKey, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return base64EncodeString(value);
    }

    /**
     * Decrypt string.
     *
     * @param value the value
     * @return the string
     */
    protected String decrypt(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            return (CryptoHelper.decrypt(base64DecodeString(value), cryptoKey, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return base64DecodeString(value);
    }

    private String base64EncodeString(String value) {
        try {
            return Base64.encodeToString(value.getBytes(STRING_ENCODING), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        }
    }

    private String base64DecodeString(String value) {
        try {
            return new String(Base64.decode(value.getBytes(), Base64.DEFAULT), STRING_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
        }
    }


    /**
     * The type Editor.
     */
    protected class Editor implements SharedPreferences.Editor {
        /**
         * The Delegate.
         */
        protected SharedPreferences.Editor delegate;

        /**
         * Instantiates a new Editor.
         */
        public Editor() {
            this.delegate = ObscuredSharedPreferences.this.delegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            delegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            String encrypted = encrypt(value);
            delegate.putString(key, encrypted);
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            Set<String> encodedStringSet = new HashSet<>();
            for (Iterator<String> itr = values.iterator(); itr.hasNext(); ) {
                encodedStringSet.add(encrypt(itr.next()));
            }
            delegate.putStringSet(key, encodedStringSet);
            return this;
        }

        @Override
        public void apply() {
            delegate.apply();
        }

        @Override
        public Editor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

        @Override
        public Editor remove(String s) {
            delegate.remove(s);
            return this;
        }
    }
}
