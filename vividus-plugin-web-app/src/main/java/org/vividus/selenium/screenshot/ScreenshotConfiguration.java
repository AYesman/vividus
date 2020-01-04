/*
 * Copyright 2019-2020 the original author or authors.
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

package org.vividus.selenium.screenshot;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.jbehave.core.annotations.AsParameters;
import org.openqa.selenium.WebElement;

@AsParameters
public class ScreenshotConfiguration
{
    public static final int SCROLL_TIMEOUT = 500;
    private int webHeaderToCut;
    private int webFooterToCut;
    private int nativeHeaderToCut;
    private int nativeFooterToCut;
    private Supplier<Optional<WebElement>> scrollableElement = Optional::empty;
    private CoordsProviderType coordsProvider = CoordsProviderType.CEILING;
    private Duration scrollTimeout = Duration.ofMillis(SCROLL_TIMEOUT);
    private Optional<ScreenshotShootingStrategy> screenshotShootingStrategy = Optional.empty();

    public int getWebHeaderToCut()
    {
        return webHeaderToCut;
    }

    public void setWebHeaderToCut(int webHeaderToCut)
    {
        this.webHeaderToCut = webHeaderToCut;
    }

    public int getWebFooterToCut()
    {
        return webFooterToCut;
    }

    public void setWebFooterToCut(int webFooterToCut)
    {
        this.webFooterToCut = webFooterToCut;
    }

    public int getNativeHeaderToCut()
    {
        return nativeHeaderToCut;
    }

    public void setNativeHeaderToCut(int nativeHeaderToCut)
    {
        this.nativeHeaderToCut = nativeHeaderToCut;
    }

    public int getNativeFooterToCut()
    {
        return nativeFooterToCut;
    }

    public void setNativeFooterToCut(int nativeFooterToCut)
    {
        this.nativeFooterToCut = nativeFooterToCut;
    }

    public Supplier<Optional<WebElement>> getScrollableElement()
    {
        return scrollableElement;
    }

    public void setScrollableElement(Supplier<Optional<WebElement>> scrollableElement)
    {
        this.scrollableElement = scrollableElement;
    }

    public CoordsProviderType getCoordsProvider()
    {
        return coordsProvider;
    }

    public void setCoordsProvider(String coordsProvider)
    {
        this.coordsProvider = CoordsProviderType.valueOf(coordsProvider);
    }

    public Duration getScrollTimeout()
    {
        return scrollTimeout;
    }

    public void setScrollTimeout(String scrollTimeout)
    {
        this.scrollTimeout = Duration.parse(scrollTimeout);
    }

    public Optional<ScreenshotShootingStrategy> getScreenshotShootingStrategy()
    {
        return screenshotShootingStrategy;
    }

    public void setScreenshotShootingStrategy(Optional<ScreenshotShootingStrategy> screenshotShootingStrategy)
    {
        this.screenshotShootingStrategy = screenshotShootingStrategy;
    }
}
