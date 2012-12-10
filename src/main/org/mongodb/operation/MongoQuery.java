/**
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
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
 *
 */

package org.mongodb.operation;

import org.mongodb.MongoDocument;
import org.mongodb.ReadPreference;

public class MongoQuery {
    private MongoQueryFilter filter;
    private ReadPreference readPreference;
    private int batchSize;
    private MongoDocument fields;

    public MongoQuery() {
    }

    public MongoQuery(final MongoQueryFilter filter) {
        this.filter = filter;
    }

    public MongoQueryFilter getFilter() {
        return filter;
    }

    public MongoQuery where(MongoQueryFilter filter) {
        this.filter = filter;
        return this;
    }

    // TODO: Add MongoQuerySelector interface
    public MongoQuery select(MongoDocument fields) {
        this.fields = fields;
        return this;
    }


    public MongoQuery order(final String condition) {
        throw new UnsupportedOperationException();
    }

    public MongoQuery limit(final int value) {
        throw new UnsupportedOperationException();
    }

    public MongoQuery batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public MongoQuery offset(final int value) {
        throw new UnsupportedOperationException();
    }

    public MongoQuery hintIndex(final String idxName) {
        throw new UnsupportedOperationException();
    }

    public MongoQuery retrievedFields(final boolean include, final String... fields) {
        throw new UnsupportedOperationException();
    }

    public MongoQuery enableSnapshotMode() {
        throw new UnsupportedOperationException();
    }

    public MongoQuery disableSnapshotMode() {
        throw new UnsupportedOperationException();
    }

    // TODO: it's basically required that you call this.  Not too friendly
    public MongoQuery readPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    public MongoQuery readPreferenceIfAbsent(final ReadPreference readPreference) {
        if (this.readPreference == null) {
            this.readPreference = readPreference;
        }
        return this;
    }

    public MongoQuery disableTimeout() {
        throw new UnsupportedOperationException();
    }

    public MongoQuery enableTimeout() {
        throw new UnsupportedOperationException();
    }

    public int getOptions() {
        return 0;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public int getNumToSkip() {
        return 0;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public MongoDocument getFields() {
        return fields;
    }
}
