package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanRepository extends JpaRepository<Clan, Long> {

    List<Clan> findByTierType(TierType tierType);

    List<Clan> findByAnggota_UsernameOrderByIdAsc(String username);

    boolean existsByAnggota_Username(String username);
}
