package com.marcinryba.tbsem1lab1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.marcinryba.tbsem1lab1.model.Machine;
import com.marcinryba.tbsem1lab1.model.MachineRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MachineRepository machineRepository;

    @BeforeEach
    void cleanDatabase() {
        machineRepository.deleteAll();
    }

    @Test
    void rejectsNameThatDoesNotStartWithLetter() throws Exception {
        mockMvc.perform(post("/machines")
                        .param("name", "123-maszyna")
                        .param("launchDate", "2026-10-09T10:00")
                        .param("operational", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("machines/form"))
                .andExpect(model().attributeHasFieldErrors("machine", "name"))
                .andExpect(model().attributeHasFieldErrorCode("machine", "name", "Pattern"));
    }

    @Test
    void performsMachineCrud() throws Exception {
        mockMvc.perform(post("/machines")
                        .param("name", "Tokarka")
                        .param("launchDate", "2026-10-09T10:00")
                        .param("operational", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/machines"))
                .andExpect(flash().attribute("message", "Maszyna została dodana."));

        Machine machine = machineRepository.findAll().getFirst();

        mockMvc.perform(get("/machines"))
                .andExpect(status().isOk())
                .andExpect(view().name("machines/list"))
                .andExpect(model().attribute("machines", hasSize(1)))
                .andExpect(content().string(containsString("Tokarka")));

        mockMvc.perform(post("/machines/{id}", machine.getId())
                        .param("name", "Frezarka")
                        .param("launchDate", "2026-10-10T12:30")
                        .param("_operational", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/machines"));

        Machine updatedMachine = machineRepository.findById(machine.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedMachine.getName()).isEqualTo("Frezarka");
        org.assertj.core.api.Assertions.assertThat(updatedMachine.getLaunchDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 10, 12, 30));
        org.assertj.core.api.Assertions.assertThat(updatedMachine.isOperational()).isFalse();

        mockMvc.perform(post("/machines/{id}/delete", machine.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/machines"));

        org.assertj.core.api.Assertions.assertThat(machineRepository.existsById(machine.getId())).isFalse();
    }
}
