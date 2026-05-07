package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClanController {

    @GetMapping({"/clan", "/clan/"})
    public String tampilkanClan() {
        return "clan";
    }
}
