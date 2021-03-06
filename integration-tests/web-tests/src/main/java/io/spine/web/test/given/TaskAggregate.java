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

package io.spine.web.test.given;

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

/**
 * An aggregate with the state of type {@code spine.web.test.Task}.
 *
 * @author Dmytro Dashenkov
 */
public class TaskAggregate extends Aggregate<TaskId, Task, TaskVBuilder> {

    public TaskAggregate(TaskId id) {
        super(id);
    }

    @Assign
    TaskCreated handle(CreateTask command) {
        return TaskCreatedVBuilder.newBuilder()
                                  .setId(command.getId())
                                  .setName(command.getName())
                                  .setDescription(command.getDescription())
                                  .build();
    }

    @Assign
    TaskRenamed handle(RenameTask command) {
        return TaskRenamedVBuilder.newBuilder()
                                  .setId(command.getId())
                                  .setName(command.getName())
                                  .build();
    }

    @Apply
    private void on(TaskCreated event) {
        getBuilder().setId(event.getId())
                    .setName(event.getName())
                    .setDescription(event.getDescription());
    }

    @Apply
    private void on(TaskRenamed event) {
        getBuilder().setName(event.getName());
    }
}
