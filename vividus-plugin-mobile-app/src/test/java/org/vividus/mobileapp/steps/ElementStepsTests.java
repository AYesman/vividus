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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.vividus.mobileapp.action.TouchActions;
import org.vividus.mobileapp.model.SwipeCoordinates;
import org.vividus.mobileapp.steps.ElementSteps.PickerWheelDirection;
import org.vividus.selenium.manager.IGenericWebDriverManager;
import org.vividus.steps.ui.validation.IBaseValidations;
import org.vividus.ui.action.JavascriptActions;
import org.vividus.ui.action.search.Locator;
import org.vividus.ui.mobile.action.MobileAppElementActions;

@ExtendWith({ MockitoExtension.class, TestLoggerFactoryExtension.class })
class ElementStepsTests
{
    private static final String PICKER_WHEEL = "Picker wheel";
    private static final double OFFSET = 0.1;
    private static final String IOS_SLIDER = "XCUIElementTypeSlider";

    @Mock private IGenericWebDriverManager genericWebDriverManager;
    @Mock private JavascriptActions javascriptActions;
    @Mock private IBaseValidations baseValidations;
    @Mock private Locator locator;
    @Mock private TouchActions touchActions;
    @Mock private MobileAppElementActions mobileAppElementActions;
    @InjectMocks private ElementSteps elementSteps;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(ElementSteps.class);

    @Test
    void shouldSelectPickerWheelValue()
    {
        String elementId = "element-id";
        RemoteWebElement remoteElement = mock(RemoteWebElement.class);

        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(true);
        when(baseValidations.assertElementExists(PICKER_WHEEL, locator)).thenReturn(Optional.of(remoteElement));
        when(remoteElement.getId()).thenReturn(elementId);
        when(remoteElement.getTagName()).thenReturn("XCUIElementTypePickerWheel");

        elementSteps.selectPickerWheelValue(PickerWheelDirection.NEXT, OFFSET, locator);

        verify(javascriptActions).executeScript("mobile: selectPickerWheelValue", Map.of("order", "next", "element",
                elementId, "offset", OFFSET));
    }

    @Test
    void shouldNotSelectPickerWheelValueIfElementIsNotPickerWheel()
    {
        RemoteWebElement remoteElement = mock(RemoteWebElement.class);

        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(true);
        when(baseValidations.assertElementExists(PICKER_WHEEL, locator)).thenReturn(Optional.of(remoteElement));
        when(remoteElement.getTagName()).thenReturn("XCUIElementTypeOther");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> elementSteps.selectPickerWheelValue(PickerWheelDirection.NEXT, OFFSET, locator));
        assertEquals("Located element must have XCUIElementTypePickerWheel type, but got XCUIElementTypeOther",
                exception.getMessage());
    }

    @Test
    void shouldNotSelectPickerWheelValueOnAndroid()
    {
        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> elementSteps.selectPickerWheelValue(PickerWheelDirection.NEXT, OFFSET, locator));
        assertEquals("Picker wheel selection is supported only for iOS platform", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "true , 0% , 50%, XCUIElementTypeSlider",
        "false, 0.0, 50.0, android.widget.SeekBar"
    })
    void shouldSetSliderValueWithoutAdjustmentIfGetExactValueAfterFirstSliderSwipe(boolean iosApp,
            String initialSliderValue, String sliderValue, String sliderTag)
    {
        WebElement slider = mockSlider();
        when(mobileAppElementActions.getTagName(slider)).thenReturn(sliderTag);
        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(iosApp);
        when(mobileAppElementActions.getElementText(slider)).thenReturn(initialSliderValue).thenReturn(sliderValue);

        elementSteps.setSliderValue(locator, 50);

        ArgumentCaptor<SwipeCoordinates> coordsCaptor = ArgumentCaptor.forClass(SwipeCoordinates.class);
        verify(touchActions).swipe(coordsCaptor.capture(), eq(Duration.ofSeconds(1)));
        verifyCoordinates(coordsCaptor.getValue(), 77, 384, 680);
    }

    @Test
    void shouldSetSliderValueWithNoExactAdjustment()
    {
        WebElement slider = mockSlider();
        when(mobileAppElementActions.getTagName(slider)).thenReturn(IOS_SLIDER);
        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(true);
        when(mobileAppElementActions.getElementText(slider)).thenReturn("10%").thenReturn("57%").thenReturn("49%")
                .thenReturn("51%");

        elementSteps.setSliderValue(locator, 50);

        ArgumentCaptor<SwipeCoordinates> coordsCaptor = ArgumentCaptor.forClass(SwipeCoordinates.class);
        verify(touchActions, times(3)).swipe(coordsCaptor.capture(), eq(Duration.ofSeconds(1)));
        List<SwipeCoordinates> coords = coordsCaptor.getAllValues();
        verifyCoordinates(coords.get(0), 138, 384, 680);
        verifyCoordinates(coords.get(1), 426, 365, 680);
        verifyCoordinates(coords.get(2), 377, 371, 680);
    }

    @Test
    void shouldNotSetSliderValueIfItIsAlreadyEqualToTargetValue()
    {
        WebElement slider = mockSlider();
        when(mobileAppElementActions.getTagName(slider)).thenReturn(IOS_SLIDER);
        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(true);
        when(mobileAppElementActions.getElementText(slider)).thenReturn("15%");

        elementSteps.setSliderValue(locator, 15);

        verifyNoInteractions(touchActions);
    }

    @Test
    void shouldSetSliderValueWithExactAdjustment()
    {
        WebElement slider = mockSlider();
        when(mobileAppElementActions.getTagName(slider)).thenReturn(IOS_SLIDER);
        when(genericWebDriverManager.isIOSNativeApp()).thenReturn(true);
        when(mobileAppElementActions.getElementText(slider)).thenReturn("0%").thenReturn("43%")
                .thenReturn("60%").thenReturn("50%");

        elementSteps.setSliderValue(locator, 50);

        ArgumentCaptor<SwipeCoordinates> coordsCaptor = ArgumentCaptor.forClass(SwipeCoordinates.class);
        verify(touchActions, times(3)).swipe(coordsCaptor.capture(), eq(Duration.ofSeconds(1)));
        List<SwipeCoordinates> coords = coordsCaptor.getAllValues();
        verifyCoordinates(coords.get(0), 77, 384, 680);
        verifyCoordinates(coords.get(1), 341, 402, 680);
        verifyCoordinates(coords.get(2), 445, 371, 680);
    }

    private static void verifyCoordinates(SwipeCoordinates coordinates, int startX, int endX, int y)
    {
        assertEquals(new Point(startX, y), coordinates.getStart());
        assertEquals(new Point(endX, y), coordinates.getEnd());
    }

    private WebElement mockSlider()
    {
        WebElement sliderElement = mock(WebElement.class);
        Point location = new Point(77, 620);
        when(sliderElement.getLocation()).thenReturn(location);
        Dimension size = new Dimension(614, 120);
        when(sliderElement.getSize()).thenReturn(size);

        when(baseValidations.assertElementExists("The slider", locator)).thenReturn(Optional.of(sliderElement));

        return sliderElement;
    }
}
