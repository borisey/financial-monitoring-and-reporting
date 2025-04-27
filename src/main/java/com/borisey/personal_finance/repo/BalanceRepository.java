package com.borisey.personal_finance.repo;

import com.borisey.personal_finance.models.Balance;
import com.borisey.personal_finance.models.Type;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BalanceRepository extends CrudRepository<Balance, Long> {

    Iterable<Balance> findAll(Sort colName);

    Optional<Balance> findByIdAndUserId(Long id, Long userId);

    // Запрос транзакций пользователя с ограничением дат
    @Query(value = "SELECT b FROM Balance b WHERE b.userId = ?1 AND b.date >= ?2 AND b.date <= ?3")
    Iterable<Balance> findByUserIdDateTimeFromDateTimeTo(Long userId, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Sort colName);

    @Query(value = "SELECT b FROM Balance b WHERE b.userId = ?1 AND b.type = ?2 AND b.date >= ?3 AND b.date <= ?4")
    Iterable<Balance> findByUserIdTypeFromTo(Long userId, Type type, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Sort colName);

    @Query(value = "SELECT sum(amount) FROM Balance b WHERE b.userId = ?1 AND b.type = ?2")
    Iterable<Balance> findSumByUserIdTypeId(Long userId, Type type);

//    @Query(value = "SELECT sum(amount) "
//            + " FROM Balance a "
//            + " WHERE a.userId = ?1 "
//            + " AND a.type = ?2 "
////            + " AND a.date >= ?3 "
////            + " AND a.date <= ?4 "
//            , nativeQuery = true)

    @Query(value = "SELECT sum(amount) FROM Balance b WHERE b.userId = ?1 AND b.type = ?2 AND b.date >= ?3 AND b.date <= ?4")
    Iterable<Balance> findSumByUserIdTypeIdDateTimeFromDateTimeTo(Long userId, Type type, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo);
}
