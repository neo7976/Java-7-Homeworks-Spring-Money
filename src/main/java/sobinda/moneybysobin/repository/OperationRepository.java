package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sobinda.moneybysobin.entity.Operation;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

//    @Query(value = "insert into money_transfer.operation")
//    Optional<Operation> addOperation();
}
