package org.g9project4.config.repositories;


import org.g9project4.config.entities.Configs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigsRepository extends JpaRepository<Configs, String> {
}
