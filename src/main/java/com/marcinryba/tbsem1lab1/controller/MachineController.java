package com.marcinryba.tbsem1lab1.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.marcinryba.tbsem1lab1.model.Machine;
import com.marcinryba.tbsem1lab1.model.MachineNotFoundException;
import com.marcinryba.tbsem1lab1.model.MachineRepository;

@Controller
public class MachineController {

    private final MachineRepository machineRepository;

    public MachineController(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/machines";
    }

    @GetMapping("/machines")
    public String list(Model model) {
        /**
         * Dla celów edukacyjnych - produkcyjnie antypatern z findAll();
         */
        model.addAttribute("machines", machineRepository.findAll());
        return "machines/list";
    }

    @GetMapping("/machines/new")
    public String createForm(Model model) {
        model.addAttribute("machine", new Machine());
        model.addAttribute("formTitle", "Dodaj maszynę");
        return "machines/form";
    }

    @PostMapping("/machines")
    public String create(
            @Valid @ModelAttribute("machine") Machine machine,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Dodaj maszynę");
            return "machines/form";
        }

        machineRepository.save(machine);
        redirectAttributes.addFlashAttribute("message", "Maszyna została dodana.");
        return "redirect:/machines";
    }

    @GetMapping("/machines/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new MachineNotFoundException(id));
        model.addAttribute("machine", machine);
        model.addAttribute("formTitle", "Edytuj maszynę");
        return "machines/form";
    }

    @PostMapping("/machines/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("machine") Machine machine,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Edytuj maszynę");
            return "machines/form";
        }

        Machine existingMachine = machineRepository.findById(id)
                .orElseThrow(() -> new MachineNotFoundException(id));
        existingMachine.setName(machine.getName());
        existingMachine.setLaunchDate(machine.getLaunchDate());
        existingMachine.setOperational(machine.isOperational());
        machineRepository.save(existingMachine);
        redirectAttributes.addFlashAttribute("message", "Maszyna została zaktualizowana.");
        return "redirect:/machines";
    }

    @PostMapping("/machines/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!machineRepository.existsById(id)) {
            throw new MachineNotFoundException(id);
        }

        machineRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Maszyna została usunięta.");
        return "redirect:/machines";
    }
}
