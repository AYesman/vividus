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

package org.vividus.azure.eventgrid;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.eventgrid.EventGridManager;
import com.azure.resourcemanager.eventgrid.fluent.models.SystemTopicInner;

import org.jbehave.core.annotations.When;
import org.vividus.azure.util.InnersJacksonAdapter;
import org.vividus.context.VariableContext;
import org.vividus.variable.VariableScope;

public class EventGridManagementSteps
{
    private final EventGridManager eventGridManager;
    private final VariableContext variableContext;
    private final InnersJacksonAdapter innersJacksonAdapter;

    public EventGridManagementSteps(AzureProfile azureProfile, TokenCredential tokenCredential,
            InnersJacksonAdapter innersJacksonAdapter, VariableContext variableContext)
    {
        this.eventGridManager = EventGridManager.authenticate(tokenCredential, azureProfile);
        this.variableContext = variableContext;
        this.innersJacksonAdapter = innersJacksonAdapter;
    }

    /**
     * Collects the info about all the system topics info under the specified resource group and saves it as JSON to a
     * variable.
     *
     * @param resourceGroupName The name of the resource group within the user's subscription to list the system
     *                          topics from. The name is case-insensitive.
     * @param scopes            The set (comma separated list of scopes e.g.: STORY, NEXT_BATCHES) of the variable
     *                          scopes.<br>
     *                          <i>Available scopes:</i>
     *                          <ul>
     *                          <li><b>STEP</b> - the variable will be available only within the step,
     *                          <li><b>SCENARIO</b> - the variable will be available only within the scenario,
     *                          <li><b>STORY</b> - the variable will be available within the whole story,
     *                          <li><b>NEXT_BATCHES</b> - the variable will be available starting from next batch
     *                          </ul>
     * @param variableName      The variable name to store the blob properties.
     */
    @When("I collect system topics in resource group `$resourceGroupName` and save them as JSON to $scopes variable "
            + "`$variableName`")
    public void listSystemTopics(String resourceGroupName, Set<VariableScope> scopes, String variableName)
            throws IOException
    {
        List<SystemTopicInner> systemTopics = eventGridManager.serviceClient()
                .getSystemTopics()
                .listByResourceGroup(resourceGroupName)
                .stream()
                .collect(toList());

        variableContext.putVariable(scopes, variableName, innersJacksonAdapter.serializeToJson(systemTopics));
    }
}
