package api.repository;

import api.domain.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Table, Integer> {
    Table findTableById(Integer id);


    boolean existsByNumTable(@NotNull @PositiveOrZero Integer integer);
}