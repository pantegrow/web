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

import com.google.common.testing.EqualsTester;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.spine.client.Query;
import io.spine.client.QueryFactory;
import io.spine.core.TenantId;
import io.spine.core.TenantIdVBuilder;
import io.spine.net.EmailAddress;
import io.spine.net.EmailAddressVBuilder;
import io.spine.net.InternetDomain;
import io.spine.net.InternetDomainVBuilder;
import io.spine.testing.client.TestActorRequestFactory;
import io.spine.time.ZoneOffsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static io.spine.time.ZoneIds.systemDefault;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Dmytro Dashenkov
 */
@DisplayName("FirebaseDatabasePath should")
class FirebaseDatabasePathTest {

    private static final QueryFactory queryFactory =
            TestActorRequestFactory.newInstance(FirebaseDatabasePathTest.class)
                                   .query();

    @Test
    @DisplayName("construct self for a Query")
    void testConstruct() {
        Query firstQuery = queryFactory.all(Empty.class);
        Query secondQuery = queryFactory.all(Timestamp.class);

        FirebaseDatabasePath firstPath = FirebaseDatabasePath.allocateForQuery(firstQuery);
        FirebaseDatabasePath secondPath = FirebaseDatabasePath.allocateForQuery(secondQuery);

        assertNotNull(firstPath);
        assertNotNull(secondPath);
        assertNotEquals(firstPath.toString(), secondPath.toString());
    }

    @Test
    @DisplayName("be tenant-aware")
    void testTenantAware() {
        InternetDomain domain = InternetDomainVBuilder.newBuilder()
                                                      .setValue("spine.io")
                                                      .build();
        TenantId domainTenant = TenantIdVBuilder.newBuilder()
                                                .setDomain(domain)
                                                .build();
        EmailAddress email = EmailAddressVBuilder.newBuilder()
                                                 .setValue("john@doe.org")
                                                 .build();
        TenantId emailTenant = TenantIdVBuilder.newBuilder()
                                               .setEmail(email)
                                               .build();
        TenantId firstValueTenant = TenantIdVBuilder.newBuilder()
                                                    .setValue("first tenant")
                                                    .build();
        TenantId secondValueTenant = TenantIdVBuilder.newBuilder()
                                                     .setValue("second tenant")
                                                     .build();
        List<String> paths = Stream.of(domainTenant,
                                       emailTenant,
                                       firstValueTenant,
                                       secondValueTenant)
                                   .map(FirebaseDatabasePathTest::tenantAwareQuery)
                                   .map(FirebaseDatabasePath::allocateForQuery)
                                   .map(FirebaseDatabasePath::toString)
                                   .collect(toList());
        new EqualsTester()
                .addEqualityGroup(paths.get(0))
                .addEqualityGroup(paths.get(1))
                .addEqualityGroup(paths.get(2))
                .addEqualityGroup(paths.get(3))
                .testEquals();
    }

    @Test
    @DisplayName("construct into a valid path")
    void testEscaped() {
        TestActorRequestFactory requestFactory =
                TestActorRequestFactory.newInstance("a.aa#@)?$0[abb-ab", ZoneOffsets.getDefault(), systemDefault());
        Query query = requestFactory.query().all(Any.class);
        String path = FirebaseDatabasePath.allocateForQuery(query).toString();
        assertFalse(path.contains("#"));
        assertFalse(path.contains("."));
        assertFalse(path.contains("["));

        assertTrue(path.contains("@"));
        assertTrue(path.contains("?"));
        assertTrue(path.contains(")"));
        assertTrue(path.contains("-"));
    }

    @Test
    @DisplayName("generate a database reference")
    void testReference() {
        Query query = queryFactory.all(Empty.class);
        FirebaseDatabase database = mock(FirebaseDatabase.class);

        FirebaseDatabasePath path = FirebaseDatabasePath.allocateForQuery(query);
        @SuppressWarnings("unused")
        DatabaseReference reference = path.reference(database);
        verify(database).getReference(eq(path.toString()));
    }

    private static Query tenantAwareQuery(TenantId tenantId) {
        TestActorRequestFactory requestFactory =
                TestActorRequestFactory.newInstance(FirebaseDatabasePathTest.class, tenantId);
        Query query = requestFactory.query().all(Any.class);
        return query;
    }
}
