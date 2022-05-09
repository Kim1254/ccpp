package com.gachon.ccpp.util;

import androidx.annotation.NonNull;

public class RSA
{
    public static class RSAException extends Exception {
        private final String name;
        private final String reason;

        public RSAException(String name, String reason) {
            this.name = name;
            this.reason = reason;
        }

        @NonNull
        @Override
        public String toString() {
            String value = super.toString();

            if (name != null)
                value += ":" + name;

            if (reason != null)
                value += ":" + reason;

            return value;
        }
    }

    public static class Encrypt {
        public final int n, e;
        private final boolean valid;

        private final String name;

        public Encrypt(int n, int e)
        {
            this.n = n;
            this.e = e;

            this.name = null;

            valid = true;
        }

        public Encrypt(String name, int n, int e)
        {
            this.n = n;
            this.e = e;

            this.name = name;

            valid = true;
        }

        private int doEncrypt(int m)
        {
            if (!valid)
                return 0;

            int r = 1;
            int base = m;
            int id = e;

            while (id != 0) {
                if ((id & 1) == 1)
                    r = (r * base) % n;
                base = (base * base) % n;
                id >>= 1;
            }

            return r;
        }

        public String doEncrypt(String m_ptr, int len) throws RSAException
        {
            if (len <= 0)
                throw new RSAException(this.name, "len <= 0");

            StringBuilder output = new StringBuilder();
            for (int i = 0; i < len; i++)
                output.append((char)doEncrypt((int)m_ptr.charAt(i)));

            return output.toString();
        }
    }

    public static class Decrypt {
        public final int n, d;
        private final boolean valid;

        private final String name;

        public Decrypt(int n, int d) {
            this.n = n;
            this.d = d;

            this.name = null;

            valid = true;
        }

        public Decrypt(String name, int n, int d) {
            this.n = n;
            this.d = d;

            this.name = name;

            valid = true;
        }

        private int doDecrypt(int m)
        {
            if (!valid)
                return 0;

            int r = 1;
            int base = m;
            int id = d;

            while (id != 0) {
                if ((id & 1) == 1)
                    r = (r * base) % n;
                base = (base * base) % n;
                id >>= 1;
            }

            return r;
        }

        public String doDecrypt(String c_ptr, int len) throws RSAException
        {
            if (len <= 0)
                throw new RSAException(this.name, "len <= 0");

            StringBuilder output = new StringBuilder();

            for (int i = 0; i < len; i++)
                output.append((char)doDecrypt((int)c_ptr.charAt(i)));

            return output.toString();
        }
    }
};