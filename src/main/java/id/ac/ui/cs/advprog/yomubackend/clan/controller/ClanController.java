package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/clan")
public class ClanController {

    private final ClanRepository clanRepository;
    private final UserRepository userRepository;

    public ClanController(ClanRepository clanRepository, UserRepository userRepository) {
        this.clanRepository = clanRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String tampilkanClan(Model model, Principal principal) {
        model.addAttribute("clans", clanRepository.findAll());
        model.addAttribute("currentUsername", principal == null ? null : principal.getName());
        return "clan";
    }

    @GetMapping("/create-clan")
    public String buatClan(Model model) {
        model.addAttribute("clan", new Clan());
        return "create-clan";
    }

    @PostMapping("/create-clan")
    public String simpanClan(@ModelAttribute Clan clan, Principal principal) {
        if (principal == null) {
            return "redirect:/login.html";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        clan.setNama(clan.getNama().trim());
        clan.jadikanKetuaSebagaiAnggotaAwal(user);
        clanRepository.save(clan);
        return "redirect:/clan";
    }

    @RequestMapping(value = "/delete-clan", method = { RequestMethod.GET, RequestMethod.POST })
    public String deleteClan(@RequestParam Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login.html";
        }

        Clan clan = clanRepository.findById(id)
                .orElse(null);
        boolean isKetuaClan = false;

        if (clan != null) {
            isKetuaClan = principal.getName().equals(clan.getKetuaClan().getUsername());
        }
        if (clan == null || !isKetuaClan) {
            return "redirect:/clan";
        }

        clan.getAnggota().clear();
        clanRepository.delete(clan);
        return "redirect:/clan";
    }

    @PostMapping("/join-clan")
    public String joinClan(@RequestParam Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login.html";
        }
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        if (!"PELAJAR".equals(user.getRole())) {
            return "redirect:/clan";
        }
        Clan clan = clanRepository.findById(id).orElse(null);
        if (clan == null) {
            return "redirect:/clan";
        }
        clan.tambahAnggota(user);
        clanRepository.save(clan);

        return "redirect:/clan";
    }
}
