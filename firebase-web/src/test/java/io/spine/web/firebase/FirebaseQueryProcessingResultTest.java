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

package io.spine.web.firebase;

import com.google.protobuf.Any;
import io.spine.client.Query;
import io.spine.client.QueryFactory;
import io.spine.testing.client.TestActorRequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static io.spine.json.Json.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmytro Dashenkov
 */
@DisplayName("FirebaseQueryProcessingResult should")
class FirebaseQueryProcessingResultTest {

    private static final QueryFactory queryFactory =
            TestActorRequestFactory.newInstance(FirebaseQueryProcessingResultTest.class)
                                   .query();

    private FirebaseDatabasePath databasePath;

    @BeforeEach
    void setUp() {
        Query query = queryFactory.all(Any.class);
        databasePath = FirebaseDatabasePath.allocateForQuery(query);
    }

    @Test
    @DisplayName("write DB path to servlet response")
    void testWritePath() throws IOException {
        ServletResponse response = mock(ServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        int count = 2;
        FirebaseQueryProcessingResult queryResult =
                new FirebaseQueryProcessingResult(databasePath, count);
        queryResult.writeTo(response);
        verify(response).getWriter();

        FirebaseQueryResponse expected = toQueryResponse(databasePath, count);
        FirebaseQueryResponse actual = fromJson(stringWriter.toString(),
                                                FirebaseQueryResponse.class);

        assertEquals(expected, actual);
    }

    private static FirebaseQueryResponse toQueryResponse(FirebaseDatabasePath path, long count) {
        FirebaseQueryResponse response =
                FirebaseQueryResponseVBuilder.newBuilder()
                                             .setPath(path.toString())
                                             .setCount(count)
                                             .build();
        return response;
    }
}
