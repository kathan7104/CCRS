package com.example.demo.web;

import com.example.demo.TestSupportConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "ccrs.dev.create-authority=false",
        "ccrs.dev.seed-demo-faculty=false"
})
@AutoConfigureMockMvc
@Import(TestSupportConfig.class)
class AuthControllerWebTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageStudentLoads() throws Exception {
        mockMvc.perform(get("/auth/login").queryParam("type", "student"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("loginType", "STUDENT"));
    }

    @Test
    void loginPageAuthorityLoads() throws Exception {
        mockMvc.perform(get("/auth/login").queryParam("type", "authority"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("loginType", "AUTHORITY"));
    }

    @Test
    void registerPageLoads() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void forgotPasswordPageLoads() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"));
    }

    @Test
    void loginPageDefaultsToStudent() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("loginType", "STUDENT"));
    }

    @Test
    void loginPageShowsWrongRoleError() throws Exception {
        mockMvc.perform(get("/auth/login").queryParam("error", "true").queryParam("m", "wrongRole"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("error", "This account does not match the selected login type."));
    }

    @Test
    void loginPageShowsRegistrationClosedError() throws Exception {
        mockMvc.perform(get("/auth/login").queryParam("error", "true").queryParam("m", "registrationClosed"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attribute("error", "Course registrations are closed or all seats are full. Your temporary login is disabled."));
    }

    @Test
    void verifyOtpPageDefaults() throws Exception {
        mockMvc.perform(get("/auth/verify-otp"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/verify-otp"))
                .andExpect(model().attribute("otpType", "EMAIL"))
                .andExpect(model().attribute("identifier", ""));
    }

    @Test
    void verifyOtpPageHonorsParams() throws Exception {
        mockMvc.perform(get("/auth/verify-otp").queryParam("type", "MOBILE").queryParam("identifier", "9990001111"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/verify-otp"))
                .andExpect(model().attribute("otpType", "MOBILE"))
                .andExpect(model().attribute("identifier", "9990001111"));
    }
}
