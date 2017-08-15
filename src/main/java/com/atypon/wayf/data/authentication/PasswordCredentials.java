/*
 * Copyright 2017 Atypon Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atypon.wayf.data.authentication;

import com.atypon.wayf.data.authentication.Authenticatable;
import com.atypon.wayf.data.authentication.AuthenticationCredentials;

public class PasswordCredentials implements AuthenticationCredentials {
    public static String PASSWORD_CREDETIALS_TYPE = "PASSWORD";

    private Authenticatable authenticatable;
    private String salt;
    private String emailAddress;
    private String password;

    public PasswordCredentials() {
    }

    @Override
    public Authenticatable getAuthenticatable() {
        return authenticatable;
    }

    @Override
    public void setAuthenticatable(Authenticatable authenticatable) {
        this.authenticatable = authenticatable;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
