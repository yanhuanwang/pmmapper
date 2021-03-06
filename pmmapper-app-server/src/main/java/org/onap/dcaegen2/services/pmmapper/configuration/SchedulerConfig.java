/*
 * ============LICENSE_START=======================================================
 * PNF-REGISTRATION-HANDLER
 * ================================================================================
 * Copyright (C) 2018 NOKIA Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.dcaegen2.services.pmmapper.configuration;

import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import org.onap.dcaegen2.services.pmmapper.tasks.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:admin@est.tech">Przemysław Wąsala</a> on 6/13/18
 */
@Configuration
@EnableScheduling
public class SchedulerConfig extends PmmapperAppConfig {

    private static final int SCHEDULING_DELAY = 2000;
    private static volatile List<ScheduledFuture> scheduledFutureList = new ArrayList<>();

    private final TaskScheduler taskScheduler;
    private final ScheduledTasks scheduledTask;

    @Autowired
    public SchedulerConfig(TaskScheduler taskScheduler, ScheduledTasks scheduledTask) {
        this.taskScheduler = taskScheduler;
        this.scheduledTask = scheduledTask;
    }

    @ApiOperation(value = "Get response on stopping task execution")
    public synchronized Mono<ResponseEntity<String>> getResponseFromCancellationOfTasks() {
        scheduledFutureList.forEach(x -> x.cancel(false));
        scheduledFutureList.clear();
        return Mono.defer(() ->
            Mono.just(new ResponseEntity<>("pmmapper service has already been stopped!", HttpStatus.CREATED))
        );
    }

    @PostConstruct
    @ApiOperation(value = "Start task if possible")
    public synchronized boolean tryToStartTask() {
        if (scheduledFutureList.isEmpty()) {
            scheduledFutureList.add(taskScheduler
                .scheduleWithFixedDelay(scheduledTask::scheduleMainPrhEventTask, SCHEDULING_DELAY));
            return true;
        } else {
            return false;
        }

    }
}
