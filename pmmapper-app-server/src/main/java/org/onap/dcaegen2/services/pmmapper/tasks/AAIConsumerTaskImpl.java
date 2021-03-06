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
package org.onap.dcaegen2.services.pmmapper.tasks;

import java.io.IOException;
import java.util.Optional;
import org.onap.dcaegen2.services.pmmapper.config.AAIClientConfiguration;
import org.onap.dcaegen2.services.pmmapper.configuration.AppConfig;
import org.onap.dcaegen2.services.pmmapper.configuration.Config;
import org.onap.dcaegen2.services.pmmapper.exceptions.AAINotFoundException;
import org.onap.dcaegen2.services.pmmapper.exceptions.PrhTaskException;
import org.onap.dcaegen2.services.pmmapper.model.ConsumerDmaapModel;
import org.onap.dcaegen2.services.pmmapper.service.AAIConsumerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AAIConsumerTaskImpl extends
    AAIConsumerTask<ConsumerDmaapModel, String, AAIClientConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Config pmmapperAppConfig;
    private AAIConsumerClient aaiConsumerClient;

    @Autowired
    public AAIConsumerTaskImpl(AppConfig pmmapperAppConfig) {
        this.pmmapperAppConfig = pmmapperAppConfig;
    }

    @Override
    Optional<String> consume(ConsumerDmaapModel consumerDmaapModel) throws AAINotFoundException {
        logger.trace("Method called with arg {}", consumerDmaapModel);
        try {
            return aaiConsumerClient.getHttpResponse(consumerDmaapModel);
        } catch (IOException e) {
            logger.warn("Get request not successful", e);
            throw new AAINotFoundException("Get request not successful");
        }
    }

    @Override
    protected void receiveRequest(ConsumerDmaapModel body) throws PrhTaskException {
        String response = execute(body);
        if (taskProcess != null && response != null && !response.isEmpty()) {
            taskProcess.receiveRequest(response);
        }
    }

    @Override
    public String execute(ConsumerDmaapModel consumerDmaapModel) throws AAINotFoundException {
        consumerDmaapModel = Optional.ofNullable(consumerDmaapModel)
            .orElseThrow(() -> new AAINotFoundException("Invoked null object to AAI task"));
        logger.trace("Method called with arg {}", consumerDmaapModel);
        aaiConsumerClient = resolveClient();
        return consume(consumerDmaapModel).orElseThrow(() -> new AAINotFoundException("Null response code"));
    }

    @Override
    protected AAIClientConfiguration resolveConfiguration() {
        return pmmapperAppConfig.getAAIClientConfiguration();
    }

    @Override
    AAIConsumerClient resolveClient() {
        return Optional.ofNullable(aaiConsumerClient).orElseGet(() -> new AAIConsumerClient(resolveConfiguration()));
    }
}
