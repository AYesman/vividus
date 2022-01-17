/*
 * Copyright 2019-2022 the original author or authors.
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

package org.vividus.ui.mobile.action;

import org.openqa.selenium.WebElement;
import org.vividus.selenium.manager.GenericWebDriverManager;
import org.vividus.ui.action.ElementActions;

public class MobileAppElementActions implements ElementActions
{
    private final GenericWebDriverManager genericWebDriverManager;

    public MobileAppElementActions(GenericWebDriverManager genericWebDriverManager)
    {
        this.genericWebDriverManager = genericWebDriverManager;
    }

    public String getTagName(WebElement element)
    {
        /*
         * For Android platform the WebElement.getTagName() method returns content description of the element:
         * https://github.com/appium/appium-uiautomator2-server/blob/master/app/src/main/java/io/appium/uiautomator2/model/UiObject2Element.java#L58
         */
        return genericWebDriverManager.isAndroidNativeApp() ? element.getDomAttribute("class") : element.getTagName();
    }

    @Override
    public String getElementText(WebElement element)
    {
        return element.getText();
    }

    @Override
    public boolean isElementVisible(WebElement element)
    {
        return element.isDisplayed()
                || genericWebDriverManager.isIOSNativeApp() && Boolean.parseBoolean(element.getAttribute("visible"));
    }
}
