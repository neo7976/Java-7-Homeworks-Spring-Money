package sobinda.moneybysobin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sobinda.moneybysobin.entity.Operation;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    @Query(value = "select o from Operation o where o.confirm=false")
    List<Operation> findAllByConfirm();

    Optional<Operation> findByIdAndSecretCode(Integer id, String secretCode);

    @Modifying
    @Query(value = "update money_transfer.operation set operation.confirm =true where operation.id=:id", nativeQuery = true)
    void setConfirmTrue(@Param("id") int id);
}
