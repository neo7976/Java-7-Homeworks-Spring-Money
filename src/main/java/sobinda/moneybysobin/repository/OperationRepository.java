package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobinda.moneybysobin.entity.Operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Integer> {

//    List<Operation> findAllByConfirmIsFalse();

    Optional<Operation> findByIdAndSecretCode(Integer id, String secretCode);

}
