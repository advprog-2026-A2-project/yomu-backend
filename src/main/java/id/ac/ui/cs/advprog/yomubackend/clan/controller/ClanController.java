package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clan")
public class ClanController {

    private final ClanRepository clanRepository;

    public ClanController(ClanRepository clanRepository) {
        this.clanRepository = clanRepository;
    }

    @GetMapping
    public String tampilkanClan(Model model) {
        model.addAttribute("clans", clanRepository.findAll());
        return "clan";
    }

    @GetMapping("/create-clan")
    public String buatClan(Model model) {
        model.addAttribute("clan", new Clan());
        return "create-clan";
    }

    @PostMapping("/create-clan")
    public String simpanClan(@ModelAttribute Clan clan) {
        clan.setNama(clan.getNama().trim());
        clanRepository.save(clan);
        return "redirect:/clan";
    }
}
