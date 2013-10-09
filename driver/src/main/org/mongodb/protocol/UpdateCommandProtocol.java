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

import org.mongodb.Document;
import org.mongodb.Encoder;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.WriteResult;
import org.mongodb.connection.BufferProvider;
import org.mongodb.connection.Connection;
import org.mongodb.connection.ServerDescription;
import org.mongodb.diagnostics.Loggers;
import org.mongodb.operation.Update;
import org.mongodb.protocol.message.UpdateCommandMessage;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.mongodb.assertions.Assertions.notNull;
import static org.mongodb.protocol.ProtocolHelper.getMessageSettings;

public class UpdateCommandProtocol extends WriteCommandProtocol {

    private static final Logger LOGGER = Loggers.getLogger("protocol.update");

    private final List<Update> updates;
    private final Encoder<Document> queryEncoder;

    public UpdateCommandProtocol(final MongoNamespace namespace, final WriteConcern writeConcern, final List<Update> updates,
                                 final Encoder<Document> queryEncoder, final BufferProvider bufferProvider,
                                 final ServerDescription serverDescription, final Connection connection, final boolean closeConnection) {
        super(namespace, writeConcern, bufferProvider, serverDescription, connection, closeConnection);
        this.updates = notNull("update", updates);
        this.queryEncoder = notNull("queryEncoder", queryEncoder);
    }

    @Override
    public WriteResult execute() {
        LOGGER.fine(format("Updating documents in namespace %s on connection [%s] to server %s", getNamespace(), getConnection().getId(),
                           getConnection().getServerAddress()));
        WriteResult writeResult = super.execute();
        LOGGER.fine("Update completed");
        return writeResult;
    }

    @Override
    protected UpdateCommandMessage createRequestMessage() {
        return new UpdateCommandMessage(getNamespace(), getWriteConcern(), updates,
                                        new CommandCodec<Document>(queryEncoder), getMessageSettings(getServerDescription()));
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
