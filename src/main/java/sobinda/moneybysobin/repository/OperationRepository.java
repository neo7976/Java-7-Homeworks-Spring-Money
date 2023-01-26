package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sobinda.moneybysobin.entity.Operation;

public interface OperationRepository extends JpaRepository<Operation, Integer> {
}
