/*-
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.dcaegen2.services.pmmapper.IT.junit5.mockito.MockitoExtension;

/**
 * @author <a href="mailto:przemyslaw.wasala@nokia.com">Przemysław Wąsala</a> on 4/9/18
 */
@ExtendWith({MockitoExtension.class})
class PmmapperAppConfigTest {

    private static final String PRH_ENDPOINTS = "prh_endpoints.json";
    private static final String jsonString = "{\"configs\":{\"aai\":{\"aaiClientConfiguration\":{\"aaiHost\":\"localhost\",\"aaiHostPortNumber\":8080,\"aaiIgnoreSSLCertificateErrors\":true,\"aaiProtocol\":\"https\",\"aaiUserName\":\"admin\",\"aaiUserPassword\":\"admin\",\"aaiBasePath\":\"/aai/v11\",\"aaiPnfPath\":\"/network/pnfs/pnf\",\"aaiHeaders\":{\"X-FromAppId\":\"prh\",\"X-TransactionId\":\"9999\",\"Accept\":\"application/json\",\"Real-Time\":\"true\",\"Authorization\":\"Basic QUFJOkFBSQ==\"}}},\"dmaap\":{\"dmaapConsumerConfiguration\":{\"consumerGroup\":\"other\",\"consumerId\":\"1\",\"dmaapContentType\":\"application/json\",\"dmaapHostName\":\"localhost\",\"dmaapPortNumber\":2222,\"dmaapProtocol\":\"http\",\"dmaapTopicName\":\"temp\",\"dmaapUserName\":\"admin\",\"dmaapUserPassword\":\"admin\",\"messageLimit\":1000,\"timeoutMS\":1000},\"dmaapProducerConfiguration\":{\"dmaapContentType\":\"application/json\",\"dmaapHostName\":\"localhost\",\"dmaapPortNumber\":2223,\"dmaapProtocol\":\"http\",\"dmaapTopicName\":\"temp\",\"dmaapUserName\":\"admin\",\"dmaapUserPassword\":\"admin\"}}}}";
    private static final String incorrectJsonString = "{\"configs\":{\"aai\":{\"aaiClientConfiguration\":{\"aaiHost\":\"localhost\",\"aaiHostPortNumber\":8080,\"aaiIgnoreSSLCertificateErrors\":true,\"aaiProtocol\":\"https\",\"aaiUserName\":\"admin\",\"aaiUserPassword\":\"admin\",\"aaiBasePath\":\"/aai/v11\",\"aaiPnfPath\":\"/network/pnfs/pnf\",\"aaiHeaders\":{\"X-FromAppId\":\"prh\",\"X-TransactionId\":\"9999\",\"Accept\":\"application/json\",\"Real-Time\":\"true\",\"Authorization\":\"Basic QUFJOkFBSQ==\"}}},\"dmaap\":{\"dmaapConsumerConfiguration\":{\"consumerGroup\":\"other\",\"consumerId\":\"1\",\"dmaapContentType\":\"application/json\",\"dmaapHostName\":\"localhost\",\"dmaapPortNumber\":2222,\"dmaapProtocol\":\"http\",\"dmaapTopicName\":\"temp\",\"dmaapUserName\":\"admin\",\"dmaapUserPassword\":\"admin\",\"messageLimit\":1000,\"timeoutMS\":1000},\"dmaapProducerConfiguration\":{\"dmaapContentType\":\"application/json\",\"dmaapHostName\":\"localhost\",\"dmaapPortNumber\":2223,\"dmaapProtocol\":\"http\",\"dmaaptopicName\":\"temp\",\"dmaapuserName\":\"admin\",\"dmaapuserPassword\":\"admin\"}}}}";
    private static PmmapperAppConfig pmmapperAppConfig;
    private static AppConfig appConfig;

    private static String filePath = Objects
        .requireNonNull(PmmapperAppConfigTest.class.getClassLoader().getResource(PRH_ENDPOINTS)).getFile();

    @BeforeEach
    public void setUp() {
        pmmapperAppConfig = spy(PmmapperAppConfig.class);
        appConfig = spy(new AppConfig());
    }

    @Test
    public void whenApplicationWasStarted_FilePathIsSet() {
        //
        // When
        //
        pmmapperAppConfig.setFilepath(filePath);
        //
        // Then
        //
        verify(pmmapperAppConfig, times(1)).setFilepath(anyString());
        verify(pmmapperAppConfig, times(0)).initFileStreamReader();
        Assertions.assertEquals(filePath, pmmapperAppConfig.getFilepath());
    }

    @Test
    public void whenTheConfigurationFits_GetAaiAndDmaapObjectRepresentationConfiguration()
        throws IOException {
        //
        // Given
        //
        InputStream inputStream = new ByteArrayInputStream((jsonString.getBytes(
            StandardCharsets.UTF_8)));
        //
        // When
        //
        pmmapperAppConfig.setFilepath(filePath);
        doReturn(inputStream).when(pmmapperAppConfig).getInputStream(any());
        pmmapperAppConfig.initFileStreamReader();
        appConfig.dmaapConsumerConfiguration = pmmapperAppConfig.getDmaapConsumerConfiguration();
        appConfig.dmaapPublisherConfiguration = pmmapperAppConfig.getDmaapPublisherConfiguration();
        appConfig.aaiClientConfiguration = pmmapperAppConfig.getAAIClientConfiguration();
        //
        // Then
        //
        verify(pmmapperAppConfig, times(1)).setFilepath(anyString());
        verify(pmmapperAppConfig, times(1)).initFileStreamReader();
        Assertions.assertNotNull(pmmapperAppConfig.getAAIClientConfiguration());
        Assertions.assertNotNull(pmmapperAppConfig.getDmaapConsumerConfiguration());
        Assertions.assertNotNull(pmmapperAppConfig.getDmaapPublisherConfiguration());
        Assertions
            .assertEquals(appConfig.getDmaapPublisherConfiguration(), pmmapperAppConfig.getDmaapPublisherConfiguration());
        Assertions
            .assertEquals(appConfig.getDmaapConsumerConfiguration(), pmmapperAppConfig.getDmaapConsumerConfiguration());
        Assertions
            .assertEquals(appConfig.getAAIClientConfiguration(), pmmapperAppConfig.getAAIClientConfiguration());

    }

    @Test
    public void whenFileIsNotExist_ThrowIOException() {
        //
        // Given
        //
        filePath = "/temp.json";
        pmmapperAppConfig.setFilepath(filePath);
        //
        // When
        //
        pmmapperAppConfig.initFileStreamReader();
        //
        // Then
        //
        verify(pmmapperAppConfig, times(1)).setFilepath(anyString());
        verify(pmmapperAppConfig, times(1)).initFileStreamReader();
        Assertions.assertNull(pmmapperAppConfig.getAAIClientConfiguration());
        Assertions.assertNull(pmmapperAppConfig.getDmaapConsumerConfiguration());
        Assertions.assertNull(pmmapperAppConfig.getDmaapPublisherConfiguration());

    }

    @Test
    public void whenFileIsExistsButJsonIsIncorrect() throws IOException {
        //
        // Given
        //
        InputStream inputStream = new ByteArrayInputStream((incorrectJsonString.getBytes(
            StandardCharsets.UTF_8)));
        //
        // When
        //
        pmmapperAppConfig.setFilepath(filePath);
        doReturn(inputStream).when(pmmapperAppConfig).getInputStream(any());
        pmmapperAppConfig.initFileStreamReader();

        //
        // Then
        //
        verify(pmmapperAppConfig, times(1)).setFilepath(anyString());
        verify(pmmapperAppConfig, times(1)).initFileStreamReader();
        Assertions.assertNotNull(pmmapperAppConfig.getAAIClientConfiguration());
        Assertions.assertNotNull(pmmapperAppConfig.getDmaapConsumerConfiguration());
        Assertions.assertNull(pmmapperAppConfig.getDmaapPublisherConfiguration());

    }


    @Test
    public void whenTheConfigurationFits_ButRootElementIsNotAJsonObject()
        throws IOException {
        // Given
        InputStream inputStream = new ByteArrayInputStream((jsonString.getBytes(
            StandardCharsets.UTF_8)));
        // When
        pmmapperAppConfig.setFilepath(filePath);
        doReturn(inputStream).when(pmmapperAppConfig).getInputStream(any());
        JsonElement jsonElement = mock(JsonElement.class);
        when(jsonElement.isJsonObject()).thenReturn(false);
        doReturn(jsonElement).when(pmmapperAppConfig).getJsonElement(any(JsonParser.class), any(InputStream.class));
        pmmapperAppConfig.initFileStreamReader();
        appConfig.dmaapConsumerConfiguration = pmmapperAppConfig.getDmaapConsumerConfiguration();
        appConfig.dmaapPublisherConfiguration = pmmapperAppConfig.getDmaapPublisherConfiguration();
        appConfig.aaiClientConfiguration = pmmapperAppConfig.getAAIClientConfiguration();

        // Then
        verify(pmmapperAppConfig, times(1)).setFilepath(anyString());
        verify(pmmapperAppConfig, times(1)).initFileStreamReader();
        Assertions.assertNull(pmmapperAppConfig.getAAIClientConfiguration());
        Assertions.assertNull(pmmapperAppConfig.getDmaapConsumerConfiguration());
        Assertions.assertNull(pmmapperAppConfig.getDmaapPublisherConfiguration());
    }
}