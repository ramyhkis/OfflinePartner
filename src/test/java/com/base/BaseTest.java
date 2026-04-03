package com.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    Support support;
    Properties prop;
    WebDriverWait wait;

    @BeforeTest
    public void setup() throws IOException {

        support = new Support();

        // ---------- LOAD CONFIG ----------
        prop = new Properties();
        FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/test/resources/config.properties");
        prop.load(fis);

        // ---------- HEADLESS CONFIG ----------
        ChromeOptions options = new ChromeOptions();
        String headless = prop.getProperty("headless");

        if (headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--force-device-scale-factor=1");
        }

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        if (!headless.equalsIgnoreCase("true")) {
            driver.manage().window().maximize();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://uat-adm.drop-it.co/login");

        // ---------- LOGIN ----------
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys(prop.getProperty("UserName"));

        driver.findElement(By.id("password"))
                .sendKeys(prop.getProperty("Password"));

        driver.findElement(By.xpath("//button[normalize-space()='Login']")).click();

        // Wait for dashboard
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@aria-label='open drawer']")));
    }

    @Test(priority = 1)
    public void makePartnersOffline() throws InterruptedException {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ---------- NAVIGATION ----------
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@aria-label='open drawer']"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h6[normalize-space()='Partners Management']"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h6[normalize-space()='Partners (Online)']"))).click();

        // Wait for table load
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr")));

        int num = 1;

        for (int i = 1; i <= num; i++) {

            // ---- WAIT FOR TABLE CONTAINER ----
            WebElement tableContainer = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(@class,'MuiTableContainer')]")
                    )
            );

            // ✅ STEP 1: SCROLL FULL RIGHT FIRST (CRITICAL FIX)
            js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", tableContainer);

            // Small wait for rendering (needed in headless)
            Thread.sleep(1000);

            // ---- STEP 2: WAIT FOR ROW ----
            WebElement row = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//table/tbody/tr[1]")
                    )
            );

            // ---- STEP 3: FIND BUTTON AFTER SCROLL ----
            WebElement button = row.findElement(By.xpath(".//button"));

            // ---- STEP 4: SCROLL INTO VIEW (VERTICAL) ----
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", button);

            // ---- STEP 5: CLICK USING JS ----
            js.executeScript("arguments[0].click();", button);

            // ---- CONFIRM ----
            WebElement confirmBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[normalize-space()='Confirm']")
                    )
            );

            js.executeScript("arguments[0].click();", confirmBtn);

            System.out.println("✅ Partner " + i + " set to OFFLINE");

            // ---- WAIT FOR TABLE REFRESH ----
            wait.until(ExpectedConditions.stalenessOf(row));
        }
    }

    @AfterTest
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}