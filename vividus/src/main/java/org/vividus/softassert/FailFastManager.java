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

package org.vividus.softassert;

import com.google.common.eventbus.Subscribe;

import org.jbehave.core.embedder.StoryControls;
import org.vividus.softassert.event.FailTestFastEvent;

public class FailFastManager
{
    private final ISoftAssert softAssert;
    private final StoryControls storyControls;

    public FailFastManager(ISoftAssert softAssert, StoryControls storyControls)
    {
        this.softAssert = softAssert;
        this.storyControls = storyControls;
    }

    @Subscribe
    public void onFailTestFast(FailTestFastEvent event)
    {
        if (event.isFailTestSuiteFast())
        {
            storyControls.currentStoryControls().doResetStateBeforeScenario(false);
        }
        if (event.isFailTestCaseFast())
        {
            softAssert.verify();
        }
    }
}
