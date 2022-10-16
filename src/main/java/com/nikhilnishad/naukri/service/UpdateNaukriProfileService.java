package com.nikhilnishad.naukri.service;

import com.nikhilnishad.naukri.model.User;
import com.nikhilnishad.naukri.model.UserCookie;
import com.nikhilnishad.naukri.repository.CookieRepository;
import com.nikhilnishad.naukri.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.SerializationUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UpdateNaukriProfileService implements TaskSchedulerCustomizer {

    Logger log = LoggerFactory.getLogger(UpdateNaukriProfileService.class);

    private final UserRepository userRepository;
    private final CookieRepository cookieRepository;

    WebDriver webDriverInstance;

    UpdateNaukriProfileService(UserRepository userRepository, CookieRepository cookieRepository) {
        this.userRepository = userRepository;
        this.cookieRepository = cookieRepository;
    }

    private WebDriver webDriver() {
        log.info("Getting WebDriver Ready");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36";
        options.addArguments("user-agent=" + userAgent);
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        log.info("WebDriver Ready");
        return driver;
    }

    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() {
        log.info("Scheduler triggered");
        List<User> userList = userRepository.findAll();
        userList
                .parallelStream()
                .filter(User::isActive)
                .forEach(this::updateProfile);
    }

    private void updateProfile(User user) {
        try {
            log.info("inside updateProfile for {}", user.getEmail());
            webDriverInstance = webDriver();
            webDriverInstance.navigate()
                    .to("https://www.naukri.com/nlogin/login");
            log.info("web driver navigated for login page");
            webDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
            Thread.sleep(1000);
            User currentUser = userRepository.findByEmail(user.getEmail());
            UserCookie currentUserCookies = cookieRepository.findByUserId(currentUser.getUserId());
            if (currentUserCookies == null) {
                log.info("cookies not found for user {}", user.getEmail());
                WebElement usernameTxt = webDriverInstance.findElement(By.id("usernameField"));
                usernameTxt.sendKeys(user.getEmail());
                WebElement passwordTxt = webDriverInstance.findElement(By.id("passwordField"));
                passwordTxt.sendKeys(user.getPassword());
                try {
                    passwordTxt.sendKeys(Keys.RETURN);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Thread.sleep(1000);
                webDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
                log.info("Current URL is {}", webDriverInstance.getCurrentUrl());
                log.info("trying to save user cookie {}", user.getEmail());
                //because cookie don't have no arg constructor to read object
                ArrayList<Cookie> x = new ArrayList<>(webDriverInstance.manage().getCookies().parallelStream().toList());
                byte[] cookieList = SerializationUtils.serialize(x);
                UserCookie newUserCookies = UserCookie.builder()
                        .userId(currentUser.getUserId())
                        .cookies(cookieList)
                        .build();
                cookieRepository.save(newUserCookies);
            } else {
                webDriverInstance.manage().deleteAllCookies();
                //may throw null pointer error
                List<Cookie> cookieList = SerializationUtils.deserialize(currentUserCookies.getCookies());
                cookieList.parallelStream()
                        .forEach(c -> webDriverInstance.manage().addCookie(c));
                webDriverInstance.navigate().to("https://www.naukri.com/mnjuser/homepage");
            }
            log.info("login completed for {}", user.getEmail());
            updateUserProfile(user, webDriverInstance);
        } catch (Exception e) {
            e.printStackTrace();
            webDriverInstance.close();
            webDriverInstance.quit();
            System.exit(0);
        } finally {
            webDriverInstance.close();
            webDriverInstance.quit();
            log.info("WebDriver closed. Waiting for next trigger");
        }
    }

    public void updateUserProfile(User user, WebDriver localWebDriverInstance) throws InterruptedException {
        log.info("Updating Profile for {}", user.getEmail());
        localWebDriverInstance.navigate().to("https://www.naukri.com/mnjuser/profile?id=&altresid");
        localWebDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
        Thread.sleep(1000);
        WebElement editButtonForHeadline = localWebDriverInstance
                .findElement(
                        By.xpath("/html/body/div[3]/div/div/span/div/div/div/div/div/div[2]/div[3]/div[3]/div/div/div/div[1]/span[2]")
                );
        editButtonForHeadline.click();
        log.info("Edit headline pencil button clicked for {}", user.getEmail());
        Thread.sleep(1000);
        WebElement textBoxInput = localWebDriverInstance.findElement(By.id("resumeHeadlineTxt"));
        String headlineText = textBoxInput.getText();
        Thread.sleep(1000);
        if (!headlineText.endsWith(".")) {
            headlineText += ".";
        } else headlineText = headlineText.substring(0, headlineText.lastIndexOf("."));
        textBoxInput.clear();
        Thread.sleep(200);
        textBoxInput.sendKeys(headlineText);
        Thread.sleep(500);
        log.info("Headline text replaced for {}", user.getEmail());
        textBoxInput.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER);
        //localWebDriverInstance.findElement(By.xpath("/html/body/div[6]/div[7]/div[2]/form/div[3]/div/button")).click();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        log.info("Profile updated for {} at {}", user.getEmail(), String.format("Updated Profile at:" + formatter.format(date)));
    }

    @Override
    public void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setErrorHandler(t -> {
            log.error("Scheduled task threw an exception: {}", t.getMessage(), t);
        });
    }
}
