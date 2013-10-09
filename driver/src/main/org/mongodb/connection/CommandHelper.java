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

package org.mongodb.connection;

import org.mongodb.Codec;
import org.mongodb.CommandResult;
import org.mongodb.Document;
import org.mongodb.MongoCommandFailureException;
import org.mongodb.MongoNamespace;
import org.mongodb.protocol.message.CommandMessage;
import org.mongodb.protocol.message.MessageSettings;
import org.mongodb.protocol.message.ReplyMessage;

import static org.mongodb.MongoNamespace.COMMAND_COLLECTION_NAME;

final class CommandHelper {

    static CommandResult executeCommand(final String database, final Document command, final Codec<Document> codec,
                                        final InternalConnection internalConnection, final BufferProvider bufferProvider) {
        return receiveMessage(codec, internalConnection, sendMessage(database, command, codec, internalConnection, bufferProvider));
    }

    private static CommandMessage sendMessage(final String database, final Document command, final Codec<Document> codec,
                                              final InternalConnection internalConnection, final BufferProvider bufferProvider) {
        PooledByteBufferOutputBuffer buffer = new PooledByteBufferOutputBuffer(bufferProvider);
        try {
            CommandMessage message = new CommandMessage(new MongoNamespace(database, COMMAND_COLLECTION_NAME).getFullName(),
                                                        command, codec, MessageSettings.builder().build());
            message.encode(buffer);
            internalConnection.sendMessage(buffer.getByteBuffers(), message.getId());
            return message;
        } finally {
            buffer.close();
        }
    }

    private static CommandResult receiveMessage(final Codec<Document> codec, final InternalConnection internalConnection,
                                                final CommandMessage message) {
        ResponseBuffers responseBuffers = internalConnection.receiveMessage();
        try {
            ReplyMessage<Document> replyMessage = new ReplyMessage<Document>(responseBuffers, codec, message.getId());
            return createCommandResult(replyMessage, internalConnection.getServerAddress());
        } finally {
            responseBuffers.close();
        }
    }

    private static CommandResult createCommandResult(final ReplyMessage<Document> replyMessage, final ServerAddress serverAddress) {
        CommandResult commandResult = new CommandResult(serverAddress, replyMessage.getDocuments().get(0),
                                                        replyMessage.getElapsedNanoseconds());
        if (!commandResult.isOk()) {
            throw new MongoCommandFailureException(commandResult);
        }

        return commandResult;
    }


    private CommandHelper() {
    }
}
