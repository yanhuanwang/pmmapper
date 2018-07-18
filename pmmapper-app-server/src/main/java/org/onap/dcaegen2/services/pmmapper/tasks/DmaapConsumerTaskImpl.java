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

import java.util.Optional;
import org.onap.dcaegen2.services.pmmapper.config.DmaapConsumerConfiguration;
import org.onap.dcaegen2.services.pmmapper.configuration.AppConfig;
import org.onap.dcaegen2.services.pmmapper.configuration.Config;
import org.onap.dcaegen2.services.pmmapper.exceptions.DmaapEmptyResponseException;
import org.onap.dcaegen2.services.pmmapper.exceptions.DmaapNotFoundException;
import org.onap.dcaegen2.services.pmmapper.exceptions.PrhTaskException;
import org.onap.dcaegen2.services.pmmapper.model.ConsumerDmaapModel;
import org.onap.dcaegen2.services.pmmapper.service.DmaapConsumerJsonParser;
import org.onap.dcaegen2.services.pmmapper.service.consumer.ExtendedDmaapConsumerHttpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:admin@est.tech">Przemysław Wąsala</a> on 3/23/18
 */
@Component
public class DmaapConsumerTaskImpl extends
    DmaapConsumerTask<String, ConsumerDmaapModel, DmaapConsumerConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Config pmmapperAppConfig;
    private ExtendedDmaapConsumerHttpClientImpl extendedDmaapConsumerHttpClient;
    private DmaapConsumerJsonParser dmaapConsumerJsonParser;

    @Autowired
    public DmaapConsumerTaskImpl(AppConfig pmmapperAppConfig) {
        this.pmmapperAppConfig = pmmapperAppConfig;
        this.dmaapConsumerJsonParser = new DmaapConsumerJsonParser();
    }

    DmaapConsumerTaskImpl(AppConfig pmmapperAppConfig, DmaapConsumerJsonParser dmaapConsumerJsonParser) {
        this.pmmapperAppConfig = pmmapperAppConfig;
        this.dmaapConsumerJsonParser = dmaapConsumerJsonParser;
    }


    @Override
    ConsumerDmaapModel consume(String message) throws PrhTaskException {
        logger.info("Consumed model from DmaaP: {}", message);
        return dmaapConsumerJsonParser.getJsonObject(message)
            .orElseThrow(() -> new DmaapNotFoundException("Null response from JSONObject in single reqeust"));

    }

    @Override
    protected void receiveRequest(String body) throws PrhTaskException {
        try {
            ConsumerDmaapModel response = execute(body);
            if (taskProcess != null && response != null) {
                taskProcess.receiveRequest(response);
            }
        } catch (DmaapEmptyResponseException e) {
            logger.warn("Nothing to consume from DmaaP {} topic.",
                resolveConfiguration().dmaapTopicName());
        }

    }

    @Override
    public ConsumerDmaapModel execute(String object) throws PrhTaskException {
        extendedDmaapConsumerHttpClient = resolveClient();
        logger.trace("Method called with arg {}", object);
        return consume((extendedDmaapConsumerHttpClient.getHttpConsumerResponse().orElseThrow(() ->
            new PrhTaskException("DmaapConsumerTask has returned null"))));
    }

    @Override
    void initConfigs() {
        pmmapperAppConfig.initFileStreamReader();
    }

    @Override
    protected DmaapConsumerConfiguration resolveConfiguration() {
        return pmmapperAppConfig.getDmaapConsumerConfiguration();
    }

    @Override
    ExtendedDmaapConsumerHttpClientImpl resolveClient() {
        return Optional.ofNullable(extendedDmaapConsumerHttpClient)
            .orElseGet(() -> new ExtendedDmaapConsumerHttpClientImpl(resolveConfiguration()));
    }
}