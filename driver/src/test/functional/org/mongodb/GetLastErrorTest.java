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

package org.mongodb;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class GetLastErrorTest extends DatabaseTestCase {
    @Test
    public void testDuplicateKeyException() {
        Document doc = new Document("_id", 1);
        collection.insert(doc);
        try {
            collection.insert(doc);
            fail("Should throw MongoDuplicateKeyException");
        } catch (MongoDuplicateKeyException e) {
            assertThat(e.getWriteResult().getErrorCode(), is(11000));
        }
    }
}
