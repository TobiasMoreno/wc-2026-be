package wc.prode._6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wc.prode._6.entity.ProdeGroup;

import java.util.Optional;

@Repository
public interface ProdeGroupRepository extends JpaRepository<ProdeGroup, Long> {
    Optional<ProdeGroup> findByName(String name);
    boolean existsByName(String name);
}

