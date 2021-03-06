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

import org.onap.dcaegen2.services.pmmapper.config.AAIClientConfiguration;
import org.onap.dcaegen2.services.pmmapper.config.DmaapConsumerConfiguration;
import org.onap.dcaegen2.services.pmmapper.config.DmaapPublisherConfiguration;

/**
 * @author <a href="mailto:admin@est.tech">Przemysław Wąsala</a> on 4/25/18
 */
public interface Config {

    DmaapConsumerConfiguration getDmaapConsumerConfiguration();

    AAIClientConfiguration getAAIClientConfiguration();

    DmaapPublisherConfiguration getDmaapPublisherConfiguration();

    void initFileStreamReader();
}
