package pt.ist.stdf.Simulation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{

	@Transactional
	@Modifying(flushAutomatically = true)
	@Query("update Client c set c.sharedKey = :sharedKey where c.client_id= :userId ")
	void setClientSharedKeyById(@Param("userId")Integer userId,@Param("sharedKey") String sharedKey);
}
