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

package org.mongodb.protocol;

import org.bson.ByteBufNIO;
import org.bson.io.BasicInputBuffer;
import org.junit.Test;
import org.mongodb.Document;
import org.mongodb.MongoInternalException;
import org.mongodb.connection.ReplyHeader;
import org.mongodb.protocol.message.ReplyMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReplyMessageTest {
    @Test(expected = MongoInternalException.class)
    public void shouldThrowExceptionIfRequestIdDoesNotMatchResponseTo() {
        int badResponseTo = 34565;
        int expectedResponseTo = 5;

        ByteBuffer headerByteBuffer = ByteBuffer.allocate(36);
        headerByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        headerByteBuffer.putInt(36);
        headerByteBuffer.putInt(2456);
        headerByteBuffer.putInt(badResponseTo);
        headerByteBuffer.putInt(1);
        headerByteBuffer.putInt(0);
        headerByteBuffer.putLong(0);
        headerByteBuffer.putInt(0);
        headerByteBuffer.putInt(0);
        headerByteBuffer.flip();

        BasicInputBuffer headerInputBuffer = new BasicInputBuffer(new ByteBufNIO(headerByteBuffer));
        ReplyHeader replyHeader = new ReplyHeader(headerInputBuffer);
        new ReplyMessage<Document>(replyHeader, expectedResponseTo, 100);
    }

    @Test(expected = MongoInternalException.class)
    public void shouldThrowExceptionIfOpCodeIsIncorrect() {
        int badOpCode = 2;

        ByteBuffer headerByteBuffer = ByteBuffer.allocate(36);
        headerByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        headerByteBuffer.putInt(36);
        headerByteBuffer.putInt(2456);
        headerByteBuffer.putInt(5);
        headerByteBuffer.putInt(badOpCode);
        headerByteBuffer.putInt(0);
        headerByteBuffer.putLong(0);
        headerByteBuffer.putInt(0);
        headerByteBuffer.putInt(0);
        headerByteBuffer.flip();

        BasicInputBuffer headerInputBuffer = new BasicInputBuffer(new ByteBufNIO(headerByteBuffer));
        ReplyHeader replyHeader = new ReplyHeader(headerInputBuffer);
        new ReplyMessage<Document>(replyHeader, 5, 100);
    }
}
