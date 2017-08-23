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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Wrapper type used inplace of PasswordCredentials for caching purposes.
 */
public class CachedPasswordCredentials extends PasswordCredentials {
    private PasswordCredentials delegate;

    public CachedPasswordCredentials(PasswordCredentials delegate) {
        this.delegate = delegate;
    }

    @Override
    public Authenticatable getAuthenticatable() {
        return delegate.getAuthenticatable();
    }

    @Override
    public void setAuthenticatable(Authenticatable authenticatable) {
        delegate.setAuthenticatable(authenticatable);
    }

    @Override
    public String getSalt() {
        return delegate.getSalt();
    }

    @Override
    public void setSalt(String salt) {
        delegate.setSalt(salt);
    }

    @Override
    public String getEmailAddress() {
        return delegate.getEmailAddress();
    }

    @Override
    public void setEmailAddress(String emailAddress) {
        delegate.setEmailAddress(emailAddress);
    }

    @Override
    public String getPassword() {
        return delegate.getPassword();
    }

    @Override
    public void setPassword(String password) {
        delegate.setPassword(password);
    }

    public PasswordCredentials getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!PasswordCredentials.class.isAssignableFrom(o.getClass())) {
            return false;
        }

        return ((PasswordCredentials) o).getEmailAddress().equals(getEmailAddress());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getEmailAddress())
                .toHashCode();
    }

    @Override
    public String toString() {
        return getEmailAddress() + "-" + getPassword();
    }
}
