package com.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    WebDriver driver;
    Properties prop;
    WebDriverWait wait;

    @BeforeTest
    public void setup() throws IOException {

        // ---------- LOAD CONFIG ----------
        prop = new Properties();
        FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/test/resources/config.properties");
        prop.load(fis);

        // ---------- HEADLESS CONFIG ----------
        String headless = System.getProperty("headless", prop.getProperty("headless"));

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--force-device-scale-factor=1");
        options.addArguments("--remote-allow-origins=*");

        System.out.println("🚀 Launching browser...");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        if (!headless.equalsIgnoreCase("true")) {
            driver.manage().window().maximize();
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // ---------- OPEN URL ----------
        driver.get("https://uat-adm.drop-it.co/login");

        // ---------- DEBUG LOGS ----------
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("Title: " + driver.getTitle());

        // ---------- WAIT FOR PAGE LOAD ----------
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));

        // ---------- LOGIN ----------
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='email']")))
                .sendKeys(prop.getProperty("UserName"));

        driver.findElement(By.xpath("//input[@id='password']"))
                .sendKeys(prop.getProperty("Password"));

        driver.findElement(By.xpath("//button[normalize-space()='Login']")).click();

        // ---------- WAIT FOR DASHBOARD ----------
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@aria-label='open drawer']")));

        System.out.println("✅ Login successful");
    }

    @Test
    public void makePartnersOffline() throws Exception {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ---------- NAVIGATION ----------
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@aria-label='open drawer']"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h6[normalize-space()='Partners Management']"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h6[normalize-space()='Partners (Online)']"))).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr")));

        int num = 1;

        for (int i = 1; i <= num; i++) {

            WebElement tableContainer = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class,'MuiTableContainer')]")));

            // 🔥 SCROLL FIRST (CRITICAL)
            js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", tableContainer);

            Thread.sleep(1000);

            WebElement row = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//table/tbody/tr[1]")));

            WebElement button = row.findElement(By.xpath(".//button"));

            js.executeScript("arguments[0].scrollIntoView({block:'center'});", button);
            js.executeScript("arguments[0].click();", button);

            WebElement confirmBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[normalize-space()='Confirm']")));

            js.executeScript("arguments[0].click();", confirmBtn);

            System.out.println("✅ Partner " + i + " set to OFFLINE");

            wait.until(ExpectedConditions.stalenessOf(row));
        }
    }

    @AfterTest
    public void teardown() {

        // ---------- TAKE SCREENSHOT FOR CI ----------
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            src.renameTo(new File("screenshot.png"));
            System.out.println("📸 Screenshot captured");
        } catch (Exception e) {
            System.out.println("Screenshot failed");
        }

        if (driver != null) {
            driver.quit();
        }
    }
}