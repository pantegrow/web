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

package io.spine.web.firebase.given;

import io.spine.client.Subscription;
import io.spine.client.SubscriptionId;
import io.spine.client.SubscriptionIdVBuilder;
import io.spine.client.SubscriptionVBuilder;
import io.spine.client.Target;
import io.spine.client.TargetVBuilder;
import io.spine.client.Topic;
import io.spine.client.TopicFactory;
import io.spine.core.UserId;
import io.spine.core.UserIdVBuilder;
import io.spine.testing.client.TestActorRequestFactory;

/**
 * @author Mykhailo Drachuk
 * @see io.spine.web.firebase.FirebaseSubscribeResultTest
 */
public final class FirebaseSubscribeResultTestEnv {

    /**
     * Prevents instantiation of this test environment.
     */
    private FirebaseSubscribeResultTestEnv() {
    }

    public static TopicFactory topicFactory() {
        return TestActorRequestFactory.newInstance(testUser())
                                      .topic();
    }

    private static UserId testUser() {
        return UserIdVBuilder.newBuilder()
                             .setValue("test-user")
                             .build();
    }

    public static Target newTarget(String type) {
        return TargetVBuilder.newBuilder()
                             .setIncludeAll(true)
                             .setType(type)
                             .build();
    }

    public static Subscription newSubscription(Topic topic, String path) {
        SubscriptionId subscriptionId = newSubscriptionId(path);
        return SubscriptionVBuilder.newBuilder()
                                   .setTopic(topic)
                                   .setId(subscriptionId)
                                   .build();
    }

    private static SubscriptionId newSubscriptionId(String path) {
        return SubscriptionIdVBuilder.newBuilder()
                                     .setValue(path)
                                     .build();
    }
}
