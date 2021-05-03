package pt.ist.stdf.HighlyDependableTracker.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.stdf.HighlyDependableTracker.Model.ClientEpoch;
import pt.ist.stdf.HighlyDependableTracker.Model.ClientEpochId;

@Repository
public interface ClientEpochRepository extends JpaRepository<ClientEpoch, ClientEpochId>{

	ClientEpoch findByPrimaryKey(ClientEpochId id);

	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(value ="DELETE FROM ClientEpoch c WHERE c.x_position!=-1")
	void deleteAll();
	
	//todo Transactional nos outros repos
}
