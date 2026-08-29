package com.anuj.LinkedinProfileExtractor.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedInSeleniumClient {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * Initialize WebDriver with headless Chrome
     */
    public void initializeDriver() {
        log.info("Initializing Selenium WebDriver");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        log.info("WebDriver initialized successfully");
    }

    /**
     * Set LinkedIn session cookies for authentication
     */
    public void setLinkedInCookies(String liAtCookie, String jsessionIdCookie) {
        log.info("Setting LinkedIn session cookies");
        
        driver.get("https://www.linkedin.com");
        
        // Add li_at cookie
        Cookie liAt = new Cookie("li_at", liAtCookie, ".linkedin.com", "/", new java.util.Date(System.currentTimeMillis() + 86400000L));
        driver.manage().addCookie(liAt);
        
        // Add JSESSIONID cookie
        Cookie jsessionId = new Cookie("JSESSIONID", jsessionIdCookie, ".linkedin.com", "/", new java.util.Date(System.currentTimeMillis() + 86400000L));
        driver.manage().addCookie(jsessionId);
        
        log.info("LinkedIn cookies set successfully");
    }

    /**
     * Navigate to LinkedIn profile and extract page source
     */
    public String fetchProfilePage(String profileUrl) {
        log.info("Navigating to LinkedIn profile: {}", profileUrl);
        
        try {
            driver.get(profileUrl);
            
            // Wait for page to load - use multiple possible selectors
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pv-text-details__left-panel")));
            } catch (Exception e) {
                log.warn("Primary selector failed, trying alternative selectors");
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
                } catch (Exception e2) {
                    log.warn("H1 selector failed, waiting for body");
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                }
            }
            
            // Wait a bit more for dynamic content
            Thread.sleep(8000);
            
            // Scroll down to load more content
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);
            
            String pageSource = driver.getPageSource();
            log.info("Profile page fetched successfully, page source length: {}", pageSource.length());
            
            // Debug: log first 1000 chars to see what we got
            String preview = pageSource.substring(0, Math.min(1000, pageSource.length()));
            log.info("Page source preview: {}", preview);
            
            return pageSource;
        } catch (Exception e) {
            log.error("Failed to fetch profile page: {}", e.getMessage(), e);
            throw new com.anuj.LinkedinProfileExtractor.exception.ProfileFetchException(
                    "Failed to fetch profile page: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Close WebDriver
     */
    public void closeDriver() {
        if (driver != null) {
            try {
                log.info("Closing WebDriver");
                driver.quit();
            } catch (Exception e) {
                log.warn("Error closing WebDriver: {}", e.getMessage());
            } finally {
                driver = null;
                wait = null;
            }
        }
    }

    /**
     * Get current WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }
}
