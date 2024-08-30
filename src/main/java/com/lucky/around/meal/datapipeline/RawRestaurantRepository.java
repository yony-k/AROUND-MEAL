package com.lucky.around.meal.datapipeline;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;

public interface RawRestaurantRepository extends JpaRepository<RawRestaurant, String> {
  Page<RawRestaurant> findByIsUpdatedTrue(PageRequest pageRequest);

  @Modifying
  @Transactional
  @Query("UPDATE RawRestaurant r SET r.isUpdated = false WHERE r.id IN :ids")
  void updateIsUpdatedToFalseByIds(@Param("ids") List<String> ids);
}
