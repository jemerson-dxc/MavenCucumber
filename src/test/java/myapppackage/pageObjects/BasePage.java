package myapppackage.pageObjects;

import myapppackage.WebDriverController;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasePage {

    private WebDriver driver;
    private WebDriverController webDriverController;


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

    public void click(By locator){
        find(locator).click();
    }

    protected void clearAndTypeIntoField(By locator, String inputText){
        find(locator).clear();
        find(locator).sendKeys(inputText);
    }

}
