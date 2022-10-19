package com.test.accountbook.accountrecord.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRecordRepository extends JpaRepository<AccountRecord, Long> {
    @Query("select ar  from AccountRecord ar where ar.user.id = :userId ")
    List<AccountRecord> findByUserId(@Param("userId") long userId);

    @Query("select ar from AccountRecord ar where ar.id = :id and ar.user.id = :userId")
    Optional<AccountRecord> findByIdAndUserId(@Param("id") long id, @Param("userId") long userId);

    @Query("select ar from AccountRecord ar where ar.user.id = :userId and ar.deleted = true")
    List<AccountRecord> findByUserIdAndDeleted(@Param("userId") long userId);

    @Query("select ar from AccountRecord ar where ar.user.id = :userId and ar.deleted = false")
    List<AccountRecord> findByUserIdAndActive(@Param("userId") long userId);
}
