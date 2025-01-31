/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.reportportal.config;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vividus.ExtendedStoryReporterBuilder;
import org.vividus.reportportal.jbehave.AdaptedReportPortalFormat;

@ExtendWith(MockitoExtension.class)
class ReportPortalConfigurationTests
{
    @Mock private ExtendedStoryReporterBuilder storyBuilder;
    @Mock private AdaptedReportPortalFormat adaptedReportPortalFormat;

    @InjectMocks private ReportPortalConfiguration configuration;

    @Test
    void shouldRegisterReportPortalFormat()
    {
        configuration.afterPropertiesSet();
        verify(storyBuilder).withFormats(adaptedReportPortalFormat);
    }
}
