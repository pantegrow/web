/*
 * Copyright 2018, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.web.command;

import io.spine.base.Time;
import io.spine.client.CommandFactory;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.json.Json;
import io.spine.protobuf.AnyPacker;
import io.spine.testing.client.TestActorRequestFactory;
import io.spine.testing.client.c.CreateTask;
import io.spine.testing.client.c.CreateTaskVBuilder;
import io.spine.web.command.given.CommandServletTestEnv.TestCommandServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;

import static io.spine.base.Identifier.newUuid;
import static io.spine.core.Status.StatusCase.OK;
import static io.spine.web.given.Servlets.request;
import static io.spine.web.given.Servlets.response;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * @author Dmytro Dashenkov
 */
@DisplayName("CommandServlet should")
class CommandServletTest {

    private static final CommandFactory commandFactory =
            TestActorRequestFactory.newInstance(CommandServletTest.class)
                                   .command();

    @Test
    @DisplayName("fail to serialize")
    void testSerialize() throws IOException {
        CommandServlet servlet = new TestCommandServlet();
        ObjectOutputStream stream = new ObjectOutputStream(new ByteArrayOutputStream());
        assertThrows(UnsupportedOperationException.class, () -> stream.writeObject(servlet));
    }

    @Test
    @DisplayName("handle command POST requests")
    void testHandle() throws IOException {
        CommandServlet servlet = new TestCommandServlet();
        StringWriter response = new StringWriter();
        CreateTask createTask = CreateTaskVBuilder
                .newBuilder()
                .setId(newUuid())
                .build();
        Command command = commandFactory.create(createTask);
        servlet.doPost(request(command), response(response));
        Ack ack = Json.fromJson(response.toString(), Ack.class);
        assertEquals(OK, ack.getStatus().getStatusCase());
        assertEquals(command.getId(), AnyPacker.unpack(ack.getMessageId()));
    }

    @Test
    @DisplayName("respond 400 to an invalid command")
    void testInvalidCommand() throws IOException {
        CommandServlet servlet = new TestCommandServlet();
        HttpServletResponse response = response(new StringWriter());
        servlet.doPost(request(Time.getCurrentTime()), response);
        verify(response).sendError(400);
    }
}
