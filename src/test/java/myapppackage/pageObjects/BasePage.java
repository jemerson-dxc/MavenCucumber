package myapppackage.pageObjects;

import myapppackage.WebDriverController;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class BasePage {

    private WebDriver driver;
    private WebDriverController webDriverController;
    private WebElement CurrentFrame;
    protected WebDriverWait wait;
    private static final int MAX_TIMEOUT = 30;
    private static final int POLL_TIMER = 1;


    public BasePage(WebDriverController webDriverController){
        this.webDriverController = webDriverController;
        driver = webDriverController.getDriver();
    }


    public void visit(String url){
        driver.get(url);
    }

    public WebElement find(By locator){
        return driver.findElement(locator);
    }

//    public void click(By locator){
//        find(locator).click();
//    }

    protected void clearAndTypeIntoField(By locator, String inputText){
        find(locator).clear();
        find(locator).sendKeys(inputText);
    }

    public void setCurrentFrame(WebElement currentFrame) {
        CurrentFrame = currentFrame;
    }

    public static int getTotalFrameCountInCurrentPage(WebDriver driver) {
        int ret = 0;

        By byFrameTag = By.tagName("frame");
        List<WebElement> frameList = driver.findElements(byFrameTag);
        int frameSize = frameList.size();

        System.out.println("There are " + frameSize + " frame in current web page.");

        By byIFrameTag = By.tagName("iframe");
        List<WebElement> iframeList = driver.findElements(byIFrameTag);
        int iframeSize = iframeList.size();

        System.out.println("There are " + iframeSize + " iframe in current web page.");

        ret = frameSize + iframeSize;

        return ret;
    }

    public void switchToFrame(String xpath) {
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(xpath)));

    }

    public void switchToFrameId(String id) {
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(id)));

    }

    public void swithToFrameNumber(int number) {
        driver.switchTo().defaultContent();
        driver.switchTo().frame(number);
    }


    public boolean isElementPresent(By by) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilTheFrameIsVisibleAndSwitch(WebElement element) throws Exception {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
            setCurrentFrame(element);
        } catch (Exception e) {
            throw new Exception("Unable to find the frame" + e.getMessage());
        }
    }

    public void waitUntilUrlContains(String url) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, MAX_TIMEOUT);
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void waitUntilTheElementIsPresent(String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    public void waitUntilTheElementIsVisible(String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public void waitUntilTheElementIsVisible1(String type, String Value) throws Exception {
        if (type.equalsIgnoreCase("id")) {
            WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Value)));
        } else if (type.equalsIgnoreCase("xpath")) {
            WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Value)));
        } else {
            throw new Exception("Unable to find element type");
        }


    }

    public void waitUntilTheElementIsVisible(WebElement element) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 120, 1000);
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            if (e.getMessage().contains("Cannot find context with specified id")) {
                Thread.sleep(3000);
                wait.until(ExpectedConditions.visibilityOf(element));
            }
        }
    }




    public void waitUntilAsyncWebElementIsVisible(String xpath) {
        org.openqa.selenium.support.ui.Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(60, TimeUnit.SECONDS)
                .pollingEvery(5, TimeUnit.SECONDS).ignoring(WebDriverException.class);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                .equals("complete");
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable error) {
            // throw new Exception("Timeout waiting for Page Load Request to complete.");
        }
    }

//    private ExpectedCondition<WebElement> expectedCondition(State condition, By locator) {
//        switch (condition) {
//            case ELEMENT_IS_VISIBLE:
//                return ExpectedConditions.visibilityOfElementLocated(locator);
//            case ELEMENT_IS_CLICKABLE:
//                return ExpectedConditions.elementToBeClickable(locator);
//            default:
//                throw new IllegalArgumentException("invalid condition");
//        }
//    }


    public void loginLocalhost() {


        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("adminpassword");
        driver.findElement(By.id("submit-button")).click();


    }


    /**********************************************************************************
     **CLICK METHODS
     **********************************************************************************/
    public void click(By selector) {
        WebElement element = getElement(selector);
        waitUntilTheElementIsClickable(selector);
        try {
            element.click();
        } catch (Exception e) {
            throw new WebDriverException("The element " + selector.toString() + " is not clickable");
        }

    }

    public void Click(WebElement element) throws InterruptedException {
        boolean clicked = false;
        int attempts = 0;
        while (!clicked && attempts < 2) {
            try {
                this.wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                System.out.println("Successfully clicked on the WebElement: " + "<" + element.toString() + ">");
                clicked = true;
            } catch (Exception e) {
                System.out.println("Unable to wait and click on WebElement, Exception: " + e.getMessage());
                Assert.fail("Unable to wait and click on the WebElement, using locator: " + "<" + element.toString() + ">");
            }
            attempts++;
        }
    }


    public void clickOnElementUsingCustomTimeout(WebElement locator, WebDriver driver, int timeout) {
        try {
            final WebDriverWait customWait = new WebDriverWait(driver, timeout);
            customWait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(locator)));
            locator.click();
            System.out.println("Successfully clicked on the WebElement, using locator: " + "<" + locator + ">" + ", using a custom Timeout of: " + timeout);
        } catch (Exception e) {
            System.out.println("Unable to click on the WebElement, using locator: " + "<" + locator + ">" + ", using a custom Timeout of: " + timeout);
            Assert.fail("Unable to click on the WebElement, Exception: " + e.getMessage());
        }
    }


    /**********************************************************************************
     **ACTION METHODS
     **********************************************************************************/

    public void actionDoubleClick(By selector) throws Exception {
        Actions actions = new Actions(driver);
        WebElement element = getElement(selector);
        try {
            WaitUntilWebElementIsVisible(element);
            actions.doubleClick(element).perform();
        } catch (Exception e) {
            System.out.println("Unable to Action double click on the WebElement, using locator: " + "<" + element.toString() + ">");
            Assert.fail("Unable to Action double click on the WebElement, Exception: " + e.getMessage());
        }
    }

    public void actionMoveAndClick(WebElement element) throws Exception {
        Actions ob = new Actions(driver);
        try {
            this.wait.until(ExpectedConditions.elementToBeClickable(element)).isEnabled();
            ob.moveToElement(element).click().build().perform();
            System.out.println("Successfully Action Moved and Clicked on the WebElement, using locator: " + "<" + element.toString() + ">");
        } catch (StaleElementReferenceException elementUpdated) {
            WebElement elementToClick = element;
            Boolean elementPresent = wait.until(ExpectedConditions.elementToBeClickable(elementToClick)).isEnabled();
            if (elementPresent == true) {
                ob.moveToElement(elementToClick).click().build().perform();
                System.out.println("(Stale Exception) - Successfully Action Moved and Clicked on the WebElement, using locator: " + "<" + element.toString() + ">");
            }
        } catch (Exception e) {
            System.out.println("Unable to Action Move and Click on the WebElement, using locator: " + "<" + element.toString() + ">");
            Assert.fail("Unable to Action Move and Click on the WebElement, Exception: " + e.getMessage());
        }
    }

    public void actionMoveAndClickByLocator(By element) throws Exception {
        Actions ob = new Actions(driver);
        try {
            Boolean elementPresent = wait.until(ExpectedConditions.elementToBeClickable(element)).isEnabled();
            if (elementPresent == true) {
                WebElement elementToClick = driver.findElement(element);
                ob.moveToElement(elementToClick).click().build().perform();
                System.out.println("Action moved and clicked on the following element, using By locator: " + "<" + element.toString() + ">");
            }
        } catch (StaleElementReferenceException elementUpdated) {
            WebElement elementToClick = driver.findElement(element);
            ob.moveToElement(elementToClick).click().build().perform();
            System.out.println("(Stale Exception) - Action moved and clicked on the following element, using By locator: " + "<" + element.toString() + ">");
        } catch (Exception e) {
            System.out.println("Unable to Action Move and Click on the WebElement using by locator: " + "<" + element.toString() + ">");
            Assert.fail("Unable to Action Move and Click on the WebElement using by locator, Exception: " + e.getMessage());
        }
    }


    /**********************************************************************************
     **SEND KEYS METHODS /
     **********************************************************************************/
    public void clearField(WebElement element) {
        try {
            element.clear();
            waitForElementTextToBeEmpty(element);
        } catch (Exception e) {
            System.out.println("The element " + element.toString() + " could not be cleared");
        }
    }

    public void sendKeys(By selector, String value) {
        WebElement element = getElement(selector);
        element.click();
        clearField(element);
        try {
            waitUntilTheElementIsVisible(selector);
            element.sendKeys(value);
        } catch (Exception e) {
            throw new WebDriverException("Error in sending " + value + " to element " + selector.toString());
        }
    }

    public void jsSendKeys(By selector, String value) {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        WebElement element = getElement(selector);
        element.click();
        clearField(element);
        try {
            waitUntilTheElementIsVisible(selector);
            js.executeScript("arguments[0].value="+ value+";", element);
        } catch (Exception e) {
            throw new WebDriverException("Error in sending " + value + " to element " + selector.toString());
        }
    }
    public void jsSendKeys(By selector, float value) {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        WebElement element = getElement(selector);
        clearField(element);
        try {
            waitUntilTheElementIsVisible(selector);
            js.executeScript("arguments[0].value="+ value+";", element);
        } catch (Exception e) {
            throw new WebDriverException("Error in sending " + value + " to element " + selector.toString());
        }
    }

    public void sendKeys(WebElement element, String value) throws Exception {
        try {
            this.WaitUntilWebElementIsVisible(element);
            element.clear();
            element.sendKeys(value);
            System.out.println("Successfully Sent the following keys: '" + value + "' to element: " + "<" + element.toString() + ">");
        } catch (Exception e) {
            System.out.println("Unable to locate WebElement: " + "<" + element.toString() + "> and send the following keys: " + value);
            Assert.fail("Unable to send keys to WebElement, Exception: " + e.getMessage());
        }
    }

    /**********************************************************************************
     **WAIT METHODS
     **********************************************************************************/
    public void waitUntilTheElementIsVisible(By selector) {
        try {
            wait = new WebDriverWait(driver, MAX_TIMEOUT);
            wait.until(ExpectedConditions.presenceOfElementLocated(selector));
        } catch (Exception e) {
            throw new org.openqa.selenium.NoSuchElementException("The element " + selector.toString() + " did not become visible");
        }
    }

    public void waitUntilTheElementIsClickable(By selector) {
        try {
            wait = new WebDriverWait(driver, MAX_TIMEOUT);
            wait.until(ExpectedConditions.elementToBeClickable(selector));
        } catch (Exception e) {
            throw new WebDriverException("The element " + selector.toString() + " is not clickable");
        }
    }


    public void waitForElementTextToBeEmpty(WebElement element) {
        String text;
        try {
            text = element.getText();
            int maxRetries = 3;
            int retry = 0;
            while ((text.length() >= 1) || (retry < maxRetries)) {
                retry++;
                text = element.getText();
            }
        } catch (Exception e) {
            System.out.println("The element " + element.toString() + " was not cleared");
        }
    }


    public boolean WaitUntilWebElementIsVisible(WebElement element) {
        try {
            this.wait.until(ExpectedConditions.visibilityOf(element));
            System.out.println("WebElement is visible using locator: " + "<" + element.toString() + ">");
            return true;
        } catch (Exception e) {
            System.out.println("WebElement is NOT visible, using locator: " + "<" + element.toString() + ">");
            Assert.fail("WebElement is NOT visible, Exception: " + e.getMessage());
            return false;
        }
    }

    public boolean WaitUntilWebElementIsVisibleUsingByLocator(By element) {
        try {
            this.wait.until(ExpectedConditions.visibilityOfElementLocated(element));
            System.out.println("Element is visible using By locator: " + "<" + element.toString() + ">");
            return true;
        } catch (Exception e) {
            System.out.println("WebElement is NOT visible, using By locator: " + "<" + element.toString() + ">");
            Assert.fail("WebElement is NOT visible, Exception: " + e.getMessage());
            return false;
        }
    }

    public boolean isElementClickable(WebElement element) {
        try {
            this.wait.until(ExpectedConditions.elementToBeClickable(element));
            System.out.println("WebElement is clickable using locator: " + "<" + element.toString() + ">");
            return true;
        } catch (Exception e) {
            System.out.println("WebElement is NOT clickable using locator: " + "<" + element.toString() + ">");
            return false;
        }
    }


    public boolean waitUntilPreLoadElementDisappears(By selector) {
        return this.wait.until(ExpectedConditions.invisibilityOfElementLocated(selector));
    }


    /**********************************************************************************
     **PAGE METHODS
     **********************************************************************************/


    public String waitForSpecificPage(String urlToWaitFor) {
        try {
            String url = driver.getCurrentUrl();
            this.wait.until(ExpectedConditions.urlMatches(urlToWaitFor));
            System.out.println("The current URL was: " + url + ", " + "navigated and waited for the following URL: " + urlToWaitFor);
            return urlToWaitFor;
        } catch (Exception e) {
            System.out.println("Exception! waiting for the URL: " + urlToWaitFor + ",  Exception: " + e.getMessage());
            return e.getMessage();
        }
    }

    public void refresh(){
        driver.navigate().refresh();
        waitForPageLoaded();
    }


    /**********************************************************************************
     **ALERT & POPUPS METHODS
     **********************************************************************************/

    public boolean checkPopupIsVisible() {
        try {
            @SuppressWarnings("unused")
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("A popup has been found!");
            return true;
        } catch (Exception e) {
            System.err.println("Error came while waiting for the alert popup to appear. " + e.getMessage());
        }
        return false;
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void closeAlertPopupBox() throws AWTException, InterruptedException {
        try {
            Alert alert = this.wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (UnhandledAlertException f) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (Exception e) {
            System.out.println("Unable to close the popup");
            Assert.fail("Unable to close the popup, Exception: " + e.getMessage());
        }
    }

    /**********************************************************************************
     **SELECT METHODS
     **********************************************************************************/

    public void selectByValue(By selector, String value) {
        try {
            WaitUntilWebElementIsVisibleUsingByLocator(selector);
            Select select = new Select(getElement(selector));
            select.selectByValue(value);
        } catch (Exception e) {
            System.out.println("Error selecting value " + value + " from element " + selector.toString());
        }
    }

    public void selectByVisibleText(By selector, String value) {
        try {
            WaitUntilWebElementIsVisibleUsingByLocator(selector);
            Select select = new Select(getElement(selector));
            select.selectByVisibleText(value);
        } catch (Exception e) {
            System.out.println("Error selecting value " + value + " from element " + selector.toString());
        }
    }


    public void selectByIndex(By selector) {
        try {
            WaitUntilWebElementIsVisibleUsingByLocator(selector);
            Select select = new Select(getElement(selector));
            int selectOptions = select.getOptions().size();
            select.selectByIndex(selectOptions-1);
        } catch (Exception e) {
            System.out.println("Error selecting value " + "" + " from element " + selector.toString());
        }
    }

    /**********************************************************************************
     **GET METHODS
     **********************************************************************************/
    public String getPageTitle() {
        return driver.getTitle();
    }

    public void getURL(String url) {
        driver.get(url);
    }

    public String getCurrentURL() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            throw new WebDriverException("Could not get the current URL");
        }
    }

    public WebElement getElement(By selector) {
        try {
            return driver.findElement(selector);
        } catch (Exception e) {
            System.out.println("The element" + selector.toString() + " could not be found");
        }
        return null;
    }

    public List<WebElement> getElements(By selector) {
        try {
            return driver.findElements(selector);
        } catch (Exception e) {
            System.out.println("The elements" + selector.toString() + " could not be found");
        }
        return null;
    }

    public String getElementText(By selector) {
        waitUntilTheElementIsVisible(selector);
        try {
            return driver.findElement(selector).getText();
        } catch (Exception e) {
            System.out.println("The element " + selector.toString() + " does not exist");
        }
        return null;
    }


    public String getValue(By selector) {
        waitUntilTheElementIsVisible(selector);
        try {
            return driver.findElement(selector).getAttribute("value");
        } catch (Exception e) {
            System.out.println("The element " + selector.toString() + " does not exist");
        }
        return null;
    }

    public boolean getCheckBoxValue(By selector)
    {
        waitUntilTheElementIsVisible(selector);
        try {
            driver.findElement(selector).isSelected();
            return true;
        }
        catch (Exception e) {
            System.out.println("The element " + selector.toString() + " does not exist");
            return false;
        }
    }

    public void pressEnterKey() throws AWTException {
        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);

    }

}
