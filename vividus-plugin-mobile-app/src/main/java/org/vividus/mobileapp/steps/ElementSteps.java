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

package org.vividus.mobileapp.steps;

import static org.apache.commons.lang3.Validate.isTrue;

import java.time.Duration;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vividus.mobileapp.action.TouchActions;
import org.vividus.mobileapp.model.SwipeCoordinates;
import org.vividus.monitor.TakeScreenshotOnFailure;
import org.vividus.selenium.WebDriverUtil;
import org.vividus.selenium.manager.IGenericWebDriverManager;
import org.vividus.steps.ui.validation.IBaseValidations;
import org.vividus.ui.action.JavascriptActions;
import org.vividus.ui.action.search.Locator;
import org.vividus.ui.mobile.action.MobileAppElementActions;

@TakeScreenshotOnFailure
public class ElementSteps
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ElementSteps.class);
    private static final double PERCENT = 100.0;

    private final IGenericWebDriverManager genericWebDriverManager;
    private final JavascriptActions javascriptActions;
    private final IBaseValidations baseValidations;
    private final TouchActions touchActions;
    private final MobileAppElementActions mobileAppElementActions;

    public ElementSteps(JavascriptActions javascriptActions, IGenericWebDriverManager genericWebDriverManager,
            IBaseValidations baseValidations, TouchActions touchActions,
            MobileAppElementActions mobileAppElementActions)
    {
        this.javascriptActions = javascriptActions;
        this.genericWebDriverManager = genericWebDriverManager;
        this.baseValidations = baseValidations;
        this.touchActions = touchActions;
        this.mobileAppElementActions = mobileAppElementActions;
    }

    /**
     * Select a next or previous picker wheel value in date picker
     * @param direction direction of next value, either <b>NEXT</b> or <b>PREVIOUS</b>
     * @param offset offset for pick from a middle of a wheel
     * @param locator locator to find a <i>XCUIElementTypePickerWheel</i> element
     */
    @When("I select $direction value with `$offset` offset in picker wheel located `$locator`")
    public void selectPickerWheelValue(PickerWheelDirection direction, double offset, Locator locator)
    {
        isTrue(genericWebDriverManager.isIOSNativeApp(), "Picker wheel selection is supported only for iOS platform");
        baseValidations.assertElementExists("Picker wheel", locator)
                       .filter(element ->
                       {
                           String pickerTag = "XCUIElementTypePickerWheel";
                           String tag = element.getTagName();
                           isTrue(pickerTag.equals(tag), "Located element must have %s type, but got %s", pickerTag,
                               tag);
                           return true;
                       })
                       .map(element -> WebDriverUtil.unwrap(element, RemoteWebElement.class))
                       .map(RemoteWebElement::getId)
                       .ifPresent(id -> javascriptActions.executeScript("mobile: selectPickerWheelValue",
                           Map.of("order", direction.name().toLowerCase(),
                                  "element", id,
                                  "offset", offset)));
    }

    /**
     * Todo
     *
     * @param locator locator to find a slider
     * @param number target value to slide to
     */
    @SuppressWarnings("MagicNumber")
    @When("I set value of slider located `$locator` to `$number`")
    public void setSliderValue(Locator locator, int number)
    {
        baseValidations.assertElementExists("The slider", locator).map(Slider::new).ifPresent(slider ->
        {
            int initialPosition = slider.getPosition();
            if (initialPosition == number)
            {
                logSliderPosition(number);
                return;
            }

            int sliderPosition = swipeSlider(slider, initialPosition, number);

            if (sliderPosition == number)
            {
                logSliderPosition(number);
                return;
            }

            int diff = Math.abs(number - sliderPosition);

            int previousSlide = number;
            int previousDiff = diff;
            int swipeLimit = 5;

            do
            {
                int adjustment = diff == 1 ? 1 : diff / 2;
                int position = sliderPosition < number ? previousSlide + adjustment : previousSlide - adjustment;
                previousSlide = position;

                sliderPosition = swipeSlider(slider, sliderPosition, position);
                diff = Math.abs(number - sliderPosition);

                if (previousDiff == diff || diff == 0)
                {
                    break;
                }

                previousDiff = diff;

                swipeLimit -= 1;
            }
            while (swipeLimit > 0);

            logSliderPosition(number);
        });
    }

    private static void logSliderPosition(int sliderPosition)
    {
        LOGGER.atInfo().addArgument(sliderPosition).log("Set slider value to '{}'");
    }

    private int swipeSlider(Slider slider, int currentPosition, int targetPosition)
    {
        SwipeCoordinates coordinates = getSwipeCoordinates(slider.getLocation(), slider.getSize(),
                currentPosition, targetPosition);
        touchActions.swipe(coordinates, Duration.ofSeconds(1));
        return slider.getPosition();
    }

    private static SwipeCoordinates getSwipeCoordinates(Point sliderLocation, Dimension sliderSize, int currentValue,
            int targetValue)
    {
        int sliderBarOffsetLeft = sliderLocation.getX();
        int sliderBarWidth = sliderSize.getWidth();
        int sliderBarCenterY = sliderLocation.getY() + sliderSize.getHeight() / 2;

        int startX = (int) (sliderBarOffsetLeft + sliderBarWidth * (currentValue / PERCENT) + 0);
        int startY = sliderBarCenterY;
        int endX = (int) (sliderBarOffsetLeft + sliderBarWidth * (targetValue / PERCENT));
        int endY = sliderBarCenterY;

        return new SwipeCoordinates(startX, startY, endX, endY);
    }

    private final class Slider
    {
        private final Point location;
        private final Dimension size;
        private final WebElement target;

        private Slider(WebElement target)
        {
            String tag = mobileAppElementActions.getTagName(target);
            String sliderTag = genericWebDriverManager.isIOSNativeApp() ? "XCUIElementTypeSlider"
                    : "android.widget.SeekBar";
            isTrue(sliderTag.equals(tag), "The slider element must be '%s', but got '%s'", sliderTag, tag);
            this.location = target.getLocation();
            this.size = target.getSize();
            this.target = target;
        }

        private Point getLocation()
        {
            return location;
        }

        private Dimension getSize()
        {
            return size;
        }

        private int getPosition()
        {
            String text = mobileAppElementActions.getElementText(target);
            return genericWebDriverManager.isIOSNativeApp() ? Integer.parseInt(StringUtils.removeEnd(text, "%"))
                    : Double.valueOf(text).intValue();
        }
    }

    public enum PickerWheelDirection
    {
        NEXT, PREVIOUS;
    }
}
