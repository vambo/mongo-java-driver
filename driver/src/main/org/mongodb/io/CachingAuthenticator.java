/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.io;

import org.mongodb.MongoConnector;
import org.mongodb.MongoCredential;
import org.mongodb.impl.MongoCredentialsStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CachingAuthenticator {
    private final MongoCredentialsStore credentialsStore;
    private final MongoConnector connector;
    // needs synchronization to ensure that modifications are published.
    private final Set<String> authenticatedDatabases = Collections.synchronizedSet(new HashSet<String>());

    public CachingAuthenticator(final MongoCredentialsStore credentialsStore, final MongoConnector connector) {
        this.credentialsStore = credentialsStore;
        this.connector = connector;
    }

    public void authenticateAll() {
        // get the difference between the set of credentialed databases and the set of authenticated databases on this connector
        Set<String> unauthenticatedDatabases = new HashSet<String>(credentialsStore.getDatabases());
        unauthenticatedDatabases.removeAll(authenticatedDatabases);

        for (String databaseName : unauthenticatedDatabases) {
            authenticate(credentialsStore.get(databaseName));
        }
    }

     private void authenticate(final MongoCredential credential) {
        Authenticator authenticator;
        if (credential.getMechanism().equals(MongoCredential.MONGODB_CR_MECHANISM)) {
            authenticator = new NativeAuthenticator(credential, connector);
        }
        else if (credential.getMechanism().equals(MongoCredential.GSSAPI_MECHANISM)) {
            authenticator = new GSSAPIAuthenticator(credential, connector);
        }
        else {
            throw new IllegalArgumentException("Unsupported authentication protocol: " + credential.getMechanism());
        }
        authenticator.authenticate();
        authenticatedDatabases.add(credential.getSource());
    }
}